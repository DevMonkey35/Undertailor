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
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.scarlet.undertailor.manager.AudioManager;
import me.scarlet.undertailor.manager.FontManager;
import me.scarlet.undertailor.manager.SpriteSheetManager;
import me.scarlet.undertailor.ui.UIController;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;

public class Undertailor extends Game implements ComponentListener {
    
    public static boolean debug = false;
    public static Undertailor instance;
    
    private long frameCap;
    private FontManager fontManager;
    private AudioManager audioManager;
    private SpriteSheetManager sheetManager;
    
    private static UIController uiController;
    
    public AudioManager getAudioManager() {
        return this.audioManager;
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
        this.frameCap = 0; // refresh rate; 15 is 60fps (1000/60)
        
        this.sheetManager = new SpriteSheetManager();
        this.audioManager = new AudioManager();
        this.fontManager = new FontManager();
        
        Undertailor.uiController = new UIController();
        
        audioManager.loadMusic(new File("music/"), null, false);
        audioManager.loadSounds(new File("sounds/"), null, false);
        fontManager.loadFonts(new File("fonts/"));
        sheetManager.loadSprites(new File("sprites/"));
        
        /*actor = new Actor();
        multiStage.getStages().get(1).addActor(actor);*/
        
        Color cc = Color.BLACK;
        Gdx.gl.glClearColor(cc.r, cc.g, cc.b, cc.a);
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
        // multiStage.act(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        SpriteBatch batch = new SpriteBatch();
        uiController.render(batch);
        // multiStage.draw();
        
        batch.begin();
        fontManager.getFont("8bitop").write(batch, Gdx.graphics.getFramesPerSecond() + "", null, 10, 450, 2);
        batch.end();
        batch.dispose();
    }
    
    @Override
    public void resize(int width, int height) {
    }
    
    public void setStageViewport(Viewport port) {
    }

    @Override
    public void componentResized(ComponentEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // TODO Auto-generated method stub   
    }

    @Override
    public void componentShown(ComponentEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // TODO Auto-generated method stub
    }
    
    public static void debug(String tag, String message) {
        if(debug) {
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
            Gdx.app.debug(tag, message);
        }
    }
    
    public static UIController getUIController() {
        return Undertailor.uiController;
    }
}
