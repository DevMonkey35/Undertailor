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

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.scarlet.undertailor.manager.AudioManager;
import me.scarlet.undertailor.manager.FontManager;
import me.scarlet.undertailor.scene.ActionPlayText;
import me.scarlet.undertailor.scene.ActorBoxDialog;
import me.scarlet.undertailor.util.TextComponent;
import me.scarlet.undertailor.util.TextComponent.Text;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Undertailor extends Game implements ComponentListener {
    
    public static Undertailor instance;
    
    private long frameCap;
    private Stage uiStage;
    private Stage gameStage;
    private FontManager fontManager;
    private AudioManager audioManager;
    
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
        
        this.uiStage = new Stage(new FitViewport(640, 480)); // dialogue boxes and ui (including all of combat)
        this.gameStage = new Stage(new FitViewport(640, 480)); // overworld
        this.audioManager = new AudioManager();
        this.fontManager = new FontManager();
        
        audioManager.loadMusic(new File("music/"), null, false);
        audioManager.loadSounds(new File("sounds/"), null, false);
        fontManager.loadFonts(new File("sprites/fonts/"));
        Color cc = Color.BLACK;
        Gdx.gl.glClearColor(cc.r, cc.g, cc.b, cc.a);
        
        // audioManager.getMusic("mus_vsasgore").get().setLooping(true);
        // audioManager.getMusic("mus_vsasgore").get().play();
        ActorBoxDialog dia = new ActorBoxDialog(new ActorBoxDialog.BoxDialogMeta());
        uiStage.addActor(dia);
        dia.setVisible(true);
        List<Text> texts = new ArrayList<>();
        texts.add(new Text(Color.BROWN, null, fontManager.getFont("8bitop"), audioManager.getSound("ds_ovw").get(), 35, 1.0F, new TextComponent("* It's the end.", null, null, null, 5, 0F)));
        texts.add(new Text(Color.GOLD, null, fontManager.getFont("8bitop"), audioManager.getSound("ds_ovw").get(), 35, 1.0F, new TextComponent("* ... I think.", null, null, null, 5, 0F)));
        texts.add(new Text(Color.LIME, null, fontManager.getFont("8bitop"), audioManager.getSound("ds_ovw").get(), 35, 1.0F, new TextComponent("* Well, ", null, null, null, 5, 0.7F), new TextComponent("I guess you have time.", null, null, null, 5, 0F)));
        
        dia.addAction(new ActionPlayText(dia, texts, true));
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
        
        gameStage.act();
        uiStage.act();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gameStage.draw();
        uiStage.draw();
        
        SpriteBatch batch = new SpriteBatch();
        FontManager.write(batch, fontManager.getFont("8bitop"), Gdx.graphics.getFramesPerSecond() + "", 10, 450, 2);
        batch.dispose();
    }
    
    @Override
    public void resize(int width, int height) {
        gameStage.getViewport().update(width, height);
        uiStage.getViewport().update(width, height);
    }
    
    public void setStageViewport(Viewport port) {
        this.uiStage.setViewport(port);
        this.gameStage.setViewport(port);
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
}
