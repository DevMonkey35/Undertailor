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
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.scarlet.undertailor.lua.LuaText;
import me.scarlet.undertailor.lua.lib.ColorsLib;
import me.scarlet.undertailor.lua.lib.GameLib;
import me.scarlet.undertailor.lua.lib.MathUtilLib;
import me.scarlet.undertailor.lua.lib.TextLib;
import me.scarlet.undertailor.manager.AudioManager;
import me.scarlet.undertailor.manager.FontManager;
import me.scarlet.undertailor.manager.SpriteSheetManager;
import me.scarlet.undertailor.manager.StyleManager;
import me.scarlet.undertailor.texts.Font;
import me.scarlet.undertailor.texts.TextComponent.Text;
import me.scarlet.undertailor.ui.UIController;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;

public class Undertailor extends ApplicationAdapter {
    
    public static boolean debug = true;
    public static Undertailor instance;
    
    private long frameCap;
    private LuaValue libs[];
    private SpriteBatch drawBatch;
    
    private FontManager fontManager;
    private AudioManager audioManager;
    private StyleManager styleManager;
    private SpriteSheetManager sheetManager;
    
    private UIController uiController;
    private Viewport uiViewport;
    private Camera uiCamera;
    
    private Text writtenText;
    
    @Override
    public void pause() {
        
    }
    
    @Override
    public void resume() {
        
    }
    
    @Override
    public void create() {
        Undertailor.instance = this;
        if(debug) {
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        }
        
        this.drawBatch = new SpriteBatch();
        this.frameCap = 0; // refresh rate; 15 is 60fps (1000/60)
        this.libs = new LuaValue[] {new ColorsLib(), new GameLib(), new MathUtilLib(), new TextLib()};
        
        this.sheetManager = new SpriteSheetManager();
        this.audioManager = new AudioManager();
        this.styleManager = new StyleManager();
        this.fontManager = new FontManager();
        
        this.uiController = new UIController();
        
        this.uiCamera = new OrthographicCamera(640, 480);
        this.uiViewport = new FitViewport(640, 480, uiCamera);
        this.uiCamera.position.set(uiCamera.viewportWidth/2, uiCamera.viewportHeight/2, 0);
        this.uiCamera.update();
        
        audioManager.loadMusic(new File("music/"), null, false);
        audioManager.loadSounds(new File("sounds/"), null, false);
        fontManager.loadFonts(new File("fonts/"));
        sheetManager.loadSprites(new File("sprites/"));
        styleManager.loadStyles(new File("fonts/styles"));
        
        /*actor = new Actor();
        multiStage.getStages().get(1).addActor(actor);*/
        Globals globals = Undertailor.newGlobals();
        globals.loadfile("scripts/texttest.lua").invoke();
        writtenText = LuaText.checkText(globals.get("testtext").call()).getText();
        
        Color cc = Color.BLACK;
        Gdx.gl.glClearColor(cc.r, cc.g, cc.b, cc.a);
        
        new DisposerThread().start();
    }
    
    @Override
    public void render() {
        long sleepTime = 0;
        if(frameCap > 0) {
            try {
                sleepTime = (long) Math.round(frameCap - (Gdx.graphics.getDeltaTime()));
                Thread.sleep(sleepTime);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        uiController.process(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //uiController.render(drawBatch);
        
        drawBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        drawBatch.begin();
        /*
        Font bitop = fontManager.getFont("8bitop");
        Font aster = fontManager.getFont("aster");
        bitop.write(drawBatch, Gdx.graphics.getFramesPerSecond() + "", null, null, 10, 450, 2);
        bitop.write(drawBatch, "Tits mcgee.", null, null, 10, 250, 1);
        aster.write(drawBatch, "Ah, that's much better.", null, null, 10, 410, 2);
        aster.write(drawBatch, "You wouldn't happen to know where", null, null, 10, 370, 1);
        aster.write(drawBatch, "Sans is, would you?", null, null, 10, 330, 2);
        //fontManager.getFont("aster").write(drawBatch, "nopqrstuvwxyz", null, null, 10, 290, 2);
        //writtenText.getFont().write(drawBatch, writtenText, 10, 410);*/
        sheetManager.getSpriteSheet("aster").sheetTest(drawBatch);
        drawBatch.end();
    }
    
    @Override
    public void resize(int width, int height) {
        this.uiViewport.update(width, height);
        this.uiCamera.position.set(uiCamera.viewportWidth/2, uiCamera.viewportHeight/2, 0);
        this.uiCamera.update();
    }
    
    public static void debug(String tag, String message) {
        Gdx.app.debug("[DBUG] " + tag, message);
    }
    
    public static void log(String tag, String message) {
        Gdx.app.log("[INFO] " + tag, message);
    }
    
    public static void error(String tag, String message) {
        Gdx.app.error("[ERRR] " + tag, message);
    }
    
    public static void warn(String tag, String message) {
        log("[WARN] " + tag, message);
    }
    
    public static UIController getUIController() {
        return Undertailor.instance.uiController;
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
    
    public static Globals newGlobals() {
        Globals returned = JsePlatform.standardGlobals();
        for(LuaValue lib : Undertailor.instance.libs) {
            returned.load(lib);
        }
        
        return returned;
    }
}
