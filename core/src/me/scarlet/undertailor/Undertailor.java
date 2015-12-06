/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Marc Tellerva
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.scarlet.undertailor.lua.lib.ColorsLib;
import me.scarlet.undertailor.lua.lib.GameLib;
import me.scarlet.undertailor.lua.lib.MathUtilLib;
import me.scarlet.undertailor.lua.lib.SchedulerLib;
import me.scarlet.undertailor.lua.lib.TextLib;
import me.scarlet.undertailor.manager.AnimationManager;
import me.scarlet.undertailor.manager.AudioManager;
import me.scarlet.undertailor.manager.FontManager;
import me.scarlet.undertailor.manager.RoomManager;
import me.scarlet.undertailor.manager.SpriteSheetManager;
import me.scarlet.undertailor.manager.StyleManager;
import me.scarlet.undertailor.manager.TilemapManager;
import me.scarlet.undertailor.overworld.OverworldController;
import me.scarlet.undertailor.scheduler.Scheduler;
import me.scarlet.undertailor.texts.Font;
import me.scarlet.undertailor.ui.UIController;
import me.scarlet.undertailor.util.Blocker;
import me.scarlet.undertailor.util.InputRetriever;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.util.JFXUtil;
import me.scarlet.undertailor.util.MultiRenderer;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class Undertailor extends ApplicationAdapter {
    
    public static Undertailor instance;
    public static final Rectangle RENDER_AREA;
    
    static {
        RENDER_AREA = new Rectangle(0, 0, 640, 480);
    }
    
    public static UIController getUIController() {
        return Undertailor.instance.uiController;
    }
    
    public static OverworldController getOverworldController() {
        return Undertailor.instance.ovwController;
    }
    
    public static FontManager getFontManager() {
        return Undertailor.instance.fontManager;
    }
    
    public static StyleManager getStyleManager() {
        return Undertailor.instance.styleManager;
    }
    
    public static AudioManager getAudioManager() {
        return Undertailor.instance.audioManager;
    }
    
    public static TilemapManager getTilemapManager() {
        return Undertailor.instance.tilemapManager;
    }
    
    public static MultiRenderer getRenderer() {
        return Undertailor.instance.renderer;
    }
    
    public static AnimationManager getAnimationManager() {
        return Undertailor.instance.animationManager;
    }
    
    public static SpriteSheetManager getSheetManager() {
        return Undertailor.instance.sheetManager;
    }
    
    public static RoomManager getRoomManager() {
        return Undertailor.instance.roomManager;
    }
    
    public static Scheduler getScheduler() {
        return Undertailor.instance.scheduler;
    }
    
    public static void setFrameCap(int cap) {
        int frameCap = cap < 30 ? (cap == 0 ? 0 : 30) : cap;
        Undertailor.instance.config.backgroundFPS = frameCap;
        Undertailor.instance.config.foregroundFPS = frameCap;
    }
    
    public static Globals newGlobals() {
        Globals returned = JsePlatform.standardGlobals();
        for(LuaValue lib : Undertailor.instance.libs) {
            returned.load(lib);
        }
        
        return returned;
    }
    
    public class Output extends OutputStream {
        
        private TextArea console;
        private PrintStream original;
        public Output(PrintStream original, TextArea console) {
            this.original = original;
            this.console = console;
        }
        
        @Override
        public void write(int b) throws IOException {
            if(original != null) {
                original.print((char) b);
            }
            
            Platform.runLater(() -> {
                console.appendText("" + (char) b);
            });
        }
    }
    
    // -----
    
    private short strict;
    private boolean debug;
    
    private LuaValue libs[];
    private LwjglApplicationConfiguration config;
    
    private Stage consoleStage;
    private TextArea consoleOutput;
    private DisposerThread disposer;
    private MultiRenderer renderer;
    
    private FontManager fontManager;
    private AudioManager audioManager;
    private StyleManager styleManager;
    private TilemapManager tilemapManager;
    private SpriteSheetManager sheetManager;
    private AnimationManager animationManager;
    private RoomManager roomManager;
    
    private UIController uiController;
    private OverworldController ovwController;
    private InputRetriever inputRetriever;
    private Scheduler scheduler;
    
    public Undertailor(LwjglApplicationConfiguration config) {
        this.config = config;
        //config.foregroundFPS = 0;
        //config.backgroundFPS = 0;
    }
    
    @Override
    public void pause() {
        
    }
    
    @Override
    public void resume() {
        
    }
    
    @Override
    public void create() {
        Undertailor.instance = this;
        this.strict = 1;
        this.debug = true;
        Object[] console = prepareConsole();
        this.consoleStage = (Stage) console[0];
        this.consoleOutput = (TextArea) console[1];
        PrintStream original = System.out;
        System.setOut(new PrintStream(new Output(original, consoleOutput)));
        this.showConsole();
        
        Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
            if(e instanceof LuaError) {
                this.error("lua", e.getMessage(), e.getStackTrace());
                e.printStackTrace();
            } else {
                this.error("tailor", e.getClass().getSimpleName() + ": " + e.getMessage(), e.getStackTrace());
                e.printStackTrace();
            }
        });
        
        if(debug) {
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        }
        
        this.libs = new LuaValue[] {new ColorsLib(), new GameLib(), new MathUtilLib(), new TextLib(), new SchedulerLib()};
        this.renderer = new MultiRenderer();
        
        this.fontManager = new FontManager();
        this.audioManager = new AudioManager();
        this.styleManager = new StyleManager();
        this.tilemapManager = new TilemapManager();
        this.sheetManager = new SpriteSheetManager();
        this.animationManager = new AnimationManager();
        this.roomManager = new RoomManager();
        
        fontManager.loadObjects(new File("fonts/"));
        audioManager.loadMusic(new File("music/"));
        audioManager.loadSounds(new File("sounds/"));
        sheetManager.loadObjects(new File("sprites/"));
        tilemapManager.loadObjects(new File("tilemaps/"));
        styleManager.loadObjects(new File("fonts/styles/"));
        animationManager.loadObjects(new File("animation/"));
        roomManager.loadObjects(new File("scripts/rooms/"));
        
        this.uiController = new UIController(new FitViewport(0F, 0F)); // dimensions set by controller
        this.ovwController = new OverworldController(new FitViewport(0F, 0F));
        this.inputRetriever = new InputRetriever();
        this.scheduler = new Scheduler();
        
        Gdx.input.setInputProcessor(inputRetriever);
        uiController.getLuaLoader().loadComponents(new File("scripts/uicomponent/"));
        ovwController.getObjectLoader().loadObjects(new File("scripts/objects/"));
        
        Color cc = Color.BLACK;
        Gdx.gl.glClearColor(cc.r, cc.g, cc.b, cc.a);
        
        File mainFile = new File("main.lua");
        if(mainFile.exists()) {
            Globals globals = Undertailor.newGlobals();
            globals.loadfile("main.lua").invoke();
        } else {
            error("tailor", "main.lua file not found; no start code was executed");
        }
        
        disposer = new DisposerThread();
        disposer.setDaemon(true);
        disposer.start();
    }
    
    @Override
    public void render() {
        InputData input = inputRetriever.getCurrentData();
        float delta = Gdx.graphics.getDeltaTime();
        scheduler.process(delta, input);
        uiController.process(delta, input);
        ovwController.process(delta, input);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        ovwController.render();
        uiController.render();
        
        Font bitop = fontManager.getObject("8bitop");
        bitop.write(Gdx.graphics.getFramesPerSecond() + "", null, null, 10, 450, 2);
        renderer.flush();
    }
    
    @Override
    public void resize(int width, int height) {
        this.uiController.resize(width, height);
    }
    
    public void debug(String tag, String message) {
        Gdx.app.debug("[DBUG] " + tag, message);
    }
    
    public void log(String tag, String message) {
        Gdx.app.log("[INFO] " + tag, message);
    }
    
    public void error(String tag, String message) {
        error(tag, message, null);
    }
    
    public void error(String tag, String message, StackTraceElement[] trace) {
        Gdx.app.error("[ERRR] " + tag, message);
        if(trace != null) {
            for(StackTraceElement element : trace) {
                Gdx.app.error("[ERRR] " + tag, element.toString());
            }
        }
        
        if(strict >= 1) {
            String errMessage = "[ERRR] " + tag + ": " + message;
            errorDialog("I'm an error, weee!", errMessage, trace);
            System.exit(0);
        }
    }
    
    public void warn(String tag, String message) {
        Gdx.app.log("[WARN] " + tag, message);
        
        if(strict >= 2) {
            String errMessage = "[WARN] " + tag + ": " + message;
            errorDialog("I'm an error, weee!", errMessage, null);
            System.exit(0);
        }
    }
    
    public void showConsole() {
        Platform.runLater(() -> { 
            this.consoleStage.show();
        });
    }
    
    private Object[] prepareConsole() {
        Object[] window = new Object[2];
        
        Blocker.block(() -> {
            Stage stage = new Stage();
            AnchorPane pane = new AnchorPane();
            GridPane header = new GridPane();
            CheckBox wrap = new CheckBox("Wrap Text");
            Label consoleTitle = new Label("Undertailor Console");
            TextArea console = new TextArea();
            
            console.setEditable(false);
            consoleTitle.setFont(new javafx.scene.text.Font(16));
            javafx.scene.text.Font newFont = new javafx.scene.text.Font("Consolas", 12);
            if(newFont.getName().equals("Consolas")) {
                console.setFont(newFont);
            }
            
            wrap.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if(newValue.booleanValue()) {
                    console.setWrapText(true);
                } else {
                    console.setWrapText(false);
                }
            });
            
            if(!wrap.isSelected()) {
                wrap.fire();
            }
            
            GridPane.setColumnIndex(consoleTitle, 0);
            GridPane.setColumnIndex(wrap, 1);
            header.getColumnConstraints().add(new ColumnConstraints());
            header.getColumnConstraints().add(new ColumnConstraints(0, wrap.getPrefWidth(), wrap.getPrefWidth(), Priority.ALWAYS, HPos.RIGHT, false));
            
            header.getChildren().add(consoleTitle);
            header.getChildren().add(wrap);
            
            pane.getChildren().add(header);
            pane.getChildren().add(console);
            
            JFXUtil.setAnchorBounds(header, 15.0, null, 20.0, 20.0);
            JFXUtil.setAnchorBounds(console, 20.0);
            JFXUtil.setAnchorBounds(console, 50.0, null, null, null);
            
            stage.setScene(new Scene(pane, 600, 400));
            stage.setTitle("Undertailor Console");
            window[0] = stage;
            window[1] = console;
        }, true);
        
        return window;
    }
    
    private void errorDialog(String title, String message, StackTraceElement[] stacktrace) {
        Blocker.block(() -> {
            Stage stage = new Stage();
            AnchorPane parent = new AnchorPane();
            VBox vbox = new VBox(10);
            Label titleLabel = new Label("Whoops!");
            Separator sep = new Separator(Orientation.HORIZONTAL);
            Label info = new Label("Uh-oh! Looks like Undertailor found a problem it didn't know how to resolve...");
            TextArea errr = new TextArea(message.endsWith("\n") ? message : message + "\n");
            if(stacktrace == null) {
                errr.appendText("No stacktrace given.\n");
            } else {
                for(StackTraceElement element : stacktrace) {
                    errr.appendText(element.toString() + "\n");
                }
            }
            
            Label last = new Label("If this is from game scripts, you might wanna send this to the scripts' developer(s) and help them squash it! "
                    + "But, if you think this is Undertailor's fault, poke the developer of Undertailor about it!");
            GridPane footer = new GridPane();
            Label strict = new Label("Developers: strictness level was set at " + Undertailor.instance.strict);
            Button confirm = new Button("Oh, okay :c.");
            CheckBox wrap = new CheckBox("Wrap Text");
            
            JFXUtil.setAnchorBounds(vbox, 50.0, 10.0, 25.0, 25.0);
            
            titleLabel.setFont(new javafx.scene.text.Font(20));
            JFXUtil.setAnchorBounds(titleLabel, 10.0, null, 15.0, null);
            JFXUtil.setAnchorBounds(last, null, 15.0, null, null);
            
            errr.positionCaret(0);
            errr.setEditable(false);
            VBox.setVgrow(errr, Priority.ALWAYS);
            strict.setWrapText(true);
            info.setWrapText(true);
            last.setWrapText(true);
            javafx.scene.text.Font newFont = new javafx.scene.text.Font("Consolas", 12);
            if(newFont.getName().equals("Consolas")) {
                errr.setFont(newFont);
            }
            
            footer.getChildren().add(strict);
            footer.getChildren().add(wrap);
            footer.getChildren().add(confirm);
            ColumnConstraints defaultt = new ColumnConstraints();
            defaultt.setFillWidth(true);
            footer.getColumnConstraints().add(defaultt);
            footer.getColumnConstraints().add(new ColumnConstraints(0, wrap.getPrefWidth(), wrap.getPrefWidth(), Priority.ALWAYS, HPos.RIGHT, false));
            footer.getColumnConstraints().add(new ColumnConstraints(0, confirm.getPrefWidth(), confirm.getPrefWidth(), Priority.ALWAYS, HPos.RIGHT, false));
            GridPane.setColumnIndex(strict, 0);
            GridPane.setColumnIndex(wrap, 1);
            GridPane.setColumnIndex(confirm, 2);
            GridPane.setHalignment(confirm, HPos.RIGHT);
            
            confirm.setOnMouseReleased(event -> {
                stage.close();
            });
            
            wrap.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if(newValue.booleanValue()) {
                    errr.setWrapText(true);
                } else {
                    errr.setWrapText(false);
                }
            });
            
            if(!wrap.isSelected()) {
                wrap.fire();
            }
            
            vbox.getChildren().add(sep);
            vbox.getChildren().add(info);
            vbox.getChildren().add(errr);
            vbox.getChildren().add(last);
            vbox.getChildren().add(footer);
            parent.getChildren().add(titleLabel);
            parent.getChildren().add(vbox);
            
            InputStream imageStream = Undertailor.class.getResourceAsStream("/assets/errorIcon.png");
            if(imageStream != null) {
                Image icon = new Image(imageStream);
                stage.getIcons().add(icon);
                titleLabel.setGraphic(new ImageView(icon));
            }
            
            stage.setResizable(true);
            stage.setMinHeight(350);
            stage.setMinWidth(600);
            stage.setTitle(title);
            stage.centerOnScreen();
            
            Scene scene = new Scene(parent, stage.getMinWidth(), stage.getMinHeight());
            stage.setScene(scene);
            stage.showAndWait();
        }, true);
    }
}
