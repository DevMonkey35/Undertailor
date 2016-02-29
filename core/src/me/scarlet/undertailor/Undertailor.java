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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2D;
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
import me.scarlet.undertailor.environment.Environment;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaImplementable;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.impl.StyleImplementable;
import me.scarlet.undertailor.lua.impl.UIComponentImplementable;
import me.scarlet.undertailor.lua.impl.WorldObjectImplementable;
import me.scarlet.undertailor.lua.impl.WorldRoomImplementable;
import me.scarlet.undertailor.lua.lib.BaseLib;
import me.scarlet.undertailor.lua.lib.TimeLib;
import me.scarlet.undertailor.manager.AnimationManager;
import me.scarlet.undertailor.manager.AudioManager;
import me.scarlet.undertailor.manager.EnvironmentManager;
import me.scarlet.undertailor.manager.FontManager;
import me.scarlet.undertailor.manager.ScriptManager;
import me.scarlet.undertailor.manager.SpriteSheetManager;
import me.scarlet.undertailor.manager.StyleManager;
import me.scarlet.undertailor.manager.TilemapManager;
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
    public static File ASSETS_DIRECTORY;
    public static final Rectangle RENDER_AREA;
    public static final String MANAGER_TAG = "tailor";
    public static final LuaLibrary[] LIBS = new LuaLibrary[] {
            new BaseLib(),
            Lua.LIB_COLORS,
            Lua.LIB_GAME,
            Lua.LIB_TEXT,
            Lua.LIB_UTIL,
            Lua.LIB_STORE,
            Lua.LIB_TIME };
    
    @SuppressWarnings("rawtypes")
    public static final LuaImplementable[] IMPLS = new LuaImplementable[] {
            new StyleImplementable(),
            new UIComponentImplementable(),
            new WorldObjectImplementable(),
            new WorldRoomImplementable()
    };
    
    static {
        RENDER_AREA = new Rectangle(0, 0, 640, 480);
        
        try {
            // TODO allow this to be changeable through options
            ASSETS_DIRECTORY = new File(Undertailor.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch(Exception e) {
            System.out.println("Failed to find jar directory. Program will exit.");
            e.printStackTrace();
            System.exit(0);
            
            // shut the fuck up build errors it doesnt need to be initialized
            RuntimeException thrown = new RuntimeException();
            thrown.initCause(e);
            throw thrown;
        }
    }
    
    public static EnvironmentManager getEnvironmentManager() {
        return Undertailor.instance.environmentManager;
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
    
    public static ScriptManager getScriptManager() {
        return Undertailor.instance.scriptManager;
    }
    
    public static void setFrameCap(int cap) {
        int frameCap = cap < 30 ? (cap == 0 ? 0 : 30) : cap;
        frameCap = frameCap > 120 ? 120 : frameCap;
        Undertailor.instance.config.backgroundFPS = frameCap;
        Undertailor.instance.config.foregroundFPS = frameCap;
    }
    
    // -----
    
    private short strict;
    private boolean debug;
    private boolean paused;
    
    private LwjglApplicationConfiguration config;
    
    private SystemHandler system;
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
    
    private EnvironmentManager environmentManager;
    private InputRetriever inputRetriever;
    
    public Undertailor(LwjglApplicationConfiguration config, File assetDir) {
        if(assetDir != null && assetDir.isDirectory()) {
            Undertailor.ASSETS_DIRECTORY = assetDir;
        }
        
        this.config = config;
        this.paused = false;
        config.foregroundFPS = 60;
        config.backgroundFPS = 60;
    }
    
    @Override
    public void pause() {
        this.paused = true;
    }
    
    @Override
    public void resume() {
        this.paused = false;
    }
    
    @Override
    public void create() {
        Undertailor.instance = this;
        Box2D.init();
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
        
        fontManager.loadObjects(new File(Undertailor.ASSETS_DIRECTORY, "fonts/"));
        audioManager.loadMusic(new File(Undertailor.ASSETS_DIRECTORY, "music/"));
        audioManager.loadSounds(new File(Undertailor.ASSETS_DIRECTORY, "sounds/"));
        sheetManager.loadObjects(new File(Undertailor.ASSETS_DIRECTORY, "sprites/"));
        tilemapManager.loadObjects(new File(Undertailor.ASSETS_DIRECTORY, "tilemaps/"));
        styleManager.loadObjects(new File(Undertailor.ASSETS_DIRECTORY, "fonts/styles/"));
        animationManager.loadObjects(new File(Undertailor.ASSETS_DIRECTORY, "animation/"));
        
        this.environmentManager = new EnvironmentManager();
        this.inputRetriever = new InputRetriever();
        
        Gdx.input.setInputProcessor(inputRetriever);
        environmentManager.getUIComponentLoader().loadComponents(new File(Undertailor.ASSETS_DIRECTORY, "scripts/uicomponent/"));
        environmentManager.getWorldObjectLoader().loadObjects(new File(Undertailor.ASSETS_DIRECTORY, "scripts/objects/"));
        environmentManager.getRoomLoader().loadScripts(new File(Undertailor.ASSETS_DIRECTORY, "scripts/rooms/"));
        environmentManager.getRoomLoader().loadObjects(new File(Undertailor.ASSETS_DIRECTORY, "rooms/"));
        
        renderer.clear();
        
        File mainFile = new File(Undertailor.ASSETS_DIRECTORY, "main.lua");
        if(mainFile.exists()) {
            Globals globals = scriptManager.generateGlobals(true);
            globals.loadfile(mainFile.getAbsolutePath()).invoke();
        } else {
            error("tailor", "main.lua file not found; no start code was executed");
        }
        
        this.system = new SystemHandler();
        disposer = new DisposerThread();
        disposer.start();
    }
    
    @Override
    public void render() {
        TimeLib.updateDeltaTime();
        InputData input = inputRetriever.getCurrentData();
        Environment activeEnv = environmentManager.getActiveEnvironment();
        float delta = TimeLib.getDeltaTime();
        
        TimeLib.advanceTime(delta);
        Undertailor.getRenderer().clear();
        
        if(activeEnv != null) {
            activeEnv.render();
        }
        
        environmentManager.getGlobalScheduler().process(delta, input);
        
        if(activeEnv != null) {
            activeEnv.process(delta, input);
        }
        
        this.system.process(delta, input);
        inputRetriever.update();
    }
    
    @Override
    public void resize(int width, int height) {
        this.environmentManager.resize(width, height);
    }
    
    public Console getConsole() {
        return this.console;
    }
    
    public boolean isPaused() {
        return this.paused;
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
            
            confirm.setOnMouseReleased(event -> stage.close());
            
            wrap.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if(newValue) {
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
