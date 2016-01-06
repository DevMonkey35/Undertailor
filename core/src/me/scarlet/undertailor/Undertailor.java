/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Tellerva, Marc Lawrence
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
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
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
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaImplementable;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.impl.StyleImplementable;
import me.scarlet.undertailor.lua.impl.UIComponentImplementable;
import me.scarlet.undertailor.lua.impl.WorldObjectImplementable;
import me.scarlet.undertailor.lua.impl.WorldRoomImplementable;
import me.scarlet.undertailor.lua.lib.BaseLib;
import me.scarlet.undertailor.manager.AnimationManager;
import me.scarlet.undertailor.manager.AudioManager;
import me.scarlet.undertailor.manager.FontManager;
import me.scarlet.undertailor.manager.RoomManager;
import me.scarlet.undertailor.manager.ScriptManager;
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
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.util.MultiRenderer;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;

import java.io.File;
import java.io.InputStream;

public class Undertailor extends ApplicationAdapter {
    
    public static Undertailor instance;
    public static final Rectangle RENDER_AREA;
    public static final String MANAGER_TAG = "tailor";
    public static final LuaLibrary[] LIBS = new LuaLibrary[] {
            new BaseLib(),
            Lua.LIB_COLORS,
            Lua.LIB_GAME,
            Lua.LIB_SCHEDULER,
            Lua.LIB_TEXT,
            Lua.LIB_UTIL };
    
    @SuppressWarnings("rawtypes")
    public static final LuaImplementable[] IMPLS = new LuaImplementable[] {
            new StyleImplementable(),
            new UIComponentImplementable(),
            new WorldObjectImplementable(),
            new WorldRoomImplementable()
    };
    
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
    
    public static ScriptManager getScriptManager() {
        return Undertailor.instance.scriptManager;
    }
    
    public static void setFrameCap(int cap) {
        int frameCap = cap < 30 ? (cap == 0 ? 0 : 30) : cap;
        Undertailor.instance.config.backgroundFPS = frameCap;
        Undertailor.instance.config.foregroundFPS = frameCap;
    }
    
    // -----
    
    private short strict;
    private boolean debug;
    
    private LwjglApplicationConfiguration config;
    
    private DisposerThread disposer;
    private MultiRenderer renderer;
    private Console console;
    
    private ScriptManager scriptManager;
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
        config.foregroundFPS = 60;
        config.backgroundFPS = 60;
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void create() {
        Undertailor.instance = this;
        this.strict = 1;
        this.debug = true;
        this.console = new Console();
        
        Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
            if(e instanceof LuaError) {
                this.error("lua", e.getMessage(), (Exception) e);
            } else {
                this.error("tailor", LuaUtil.formatJavaException((Exception) e), (Exception) e);
            }
        });
        
        if(debug) {
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        }
        
        this.renderer = new MultiRenderer();
        
        this.scriptManager = new ScriptManager();
        this.scriptManager.registerLibraries(LIBS);
        this.scriptManager.registerImplementables(IMPLS);
        
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
            Globals globals = scriptManager.generateGlobals(true);
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
        
        Font bitop = fontManager.getStyle("8bitop");
        bitop.write(Gdx.graphics.getFramesPerSecond() + "", null, null, 10, 415, 2);
        renderer.flush();
        //System.out.println("RENDER CALLS: " + renderer.getSpriteBatch().renderCalls);
        //System.out.println("MAX SPRITES: " + renderer.getSpriteBatch().maxSpritesInBatch);
        //System.exit(0);
        
        if(input.getPressData(Keys.F3).justPressed(0F)) {
            this.console.show();
        }
            
        inputRetriever.update();
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
    
    public void error(String tag, String message, Exception e) {
        
        String trace = null;
        if(e != null) {
            e.printStackTrace();
        }
        
        Gdx.app.error("[ERRR] " + tag, message + (trace == null ? "" : "\n\t" + trace.trim()));
        
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
    
    private void errorDialog(String title, String message, String stacktrace) {
        Blocker.block(() -> {
            Stage stage = new Stage();
            AnchorPane parent = new AnchorPane();
            VBox vbox = new VBox(10);
            Label titleLabel = new Label("Whoops!");
            Separator sep = new Separator(Orientation.HORIZONTAL);
            Label info = new Label("Uh-oh! Looks like Undertailor found a problem it didn't know how to resolve...");
            TextArea errr = new TextArea(message.endsWith("\n") ? message : message + "\n");
            if(stacktrace == null) {
                errr.appendText("No Java stacktrace given.\n");
            } else {
                errr.appendText(stacktrace + "\n");
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

            JFXUtil.loadIcon(stage, "errorIcon_small.png");
            JFXUtil.loadIcon(stage, "errorIcon.png");
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
