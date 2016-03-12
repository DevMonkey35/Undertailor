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

package me.scarlet.undertailor.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALMusic;
import me.scarlet.undertailor.util.NumberUtil;
import me.scarlet.undertailor.wrappers.DisposableWrapper;
import org.lwjgl.openal.AL10;

import java.io.File;

/**
 * {@link Audio} and {@link DisposableWrapper} implementation for {@link Music}
 * instances.
 */
public class MusicWrapper extends DisposableWrapper<Music> implements Audio<String> {

    public static final long MAX_LIFETIME = 60000; // 1min
    
    private String id;
    protected String rescName;
    private File fileReference;
    private AudioResourceManager<MusicWrapper> manager;
    public MusicWrapper(String id, AudioResourceManager<MusicWrapper> manager, File fileReference) {
        super(null);
        
        this.fileReference = fileReference;
        this.manager = manager;
        this.id = id;
        audio();
    }

    @Override
    public Music newReference() {
        return Gdx.audio.newMusic(Gdx.files.absolute(fileReference.getAbsolutePath()));
    }
    
    @Override
    public boolean allowDispose() {
        return super.allowDispose() && !this.getReference().isPlaying();
    }
    
    // audio impl
    
    private float loopPoint;
    private float volume;
    private float pitch;
    private float pan;
    
    void audio() {
        this.volume = 1.0F;
        this.pitch = 1.0F;
        this.pan = 0.0F;
        this.loopPoint = -1F;
    }
    
    // generic implementation
    
    @Override public String getAudioName() { return this.rescName; }
    @Override public float getAffectedVolume() { return manager.getAffectedVolume() * volume; }
    @Override public float getVolume() { return volume; }
    @Override public void setVolume(float volume) { this.volume = NumberUtil.boundFloat(volume, 0.0F, 1.0F); }
    @Override public float getPan() { return pan; }
    @Override public void setPan(float pan) { this.pan = NumberUtil.boundFloat(pan, -1.0F, 1.0F); }
    @Override public float getPitch() { return pitch; }
    @Override public boolean isLooping() { return loopPoint >= 0; }
    @Override public void setLoopPoint(float loopPoint) { this.loopPoint = loopPoint; }
    
    // implementation specific for Music
    
    @Override
    public void setPitch(float pitch) {
        this.pitch = NumberUtil.boundFloat(pitch, 0.5F, 2.0F);
        if(this.getReference().isPlaying()) {
            AL10.alSourcef(((OpenALMusic) this.getReference()).getSourceId(), AL10.AL_PITCH, this.pitch);
        }
    }
    
    @Override
    public float getPosition() {
        return this.getReference().getPosition();
    }
    
    @Override
    public void setPosition(float position) {
        this.getReference().setPosition(position);
    }
    
    @Override
    public boolean isPlaying(String unused) {
        return this.getReference().isPlaying();
    }

    @Override
    public boolean isPaused(String unused) {
        return !this.getReference().isPlaying() && this.getReference().getPosition() <= 0.0F;
    }

    @Override
    public String play(float volume, float pan, float pitch) {
        this.setVolume(volume);
        this.setPan(pan);
        this.setPitch(pitch);
        return play();
    }

    @Override
    public String play() {
        this.getReference().setPan(pan, getAffectedVolume());
        updateLoop();
        
        this.getReference().play();
        this.setPitch(this.pitch); // set after
        return id;
    }

    @Override
    public void pause(String id) {
        this.getReference().pause();
    }

    @Override
    public void stop(String id) {
        this.getReference().stop();
    }
    
    /**
     * Updates the looping task for this {@link MusicWrapper}.
     * 
     * <p>Ensures that the sound will adhere to a loop point if this
     * MusicWrapper has one set.</p>
     */
    void updateLoop() {
        if(loopPoint < 0) {
            this.getReference().setOnCompletionListener(null);
            this.getReference().setLooping(false);
        } else if(loopPoint == 0) {
            this.getReference().setOnCompletionListener(null);
            this.getReference().setLooping(true);
        } else {
            this.getReference().setOnCompletionListener(music -> {
                music.play();
                music.setPosition(loopPoint);
                AL10.alSourcef(((OpenALMusic) music).getSourceId(), AL10.AL_PITCH, this.pitch);
            });
        }
    }
}
