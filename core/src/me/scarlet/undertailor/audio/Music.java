/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without
 * limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package me.scarlet.undertailor.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import mod.com.badlogic.gdx.backends.lwjgl.audio.OpenALMusic;

import me.scarlet.undertailor.resource.Resource;
import me.scarlet.undertailor.util.BoundedFloat;

import java.io.File;

import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Wrapping implementation of long music tracks.
 */
public class Music extends Resource<com.badlogic.gdx.audio.Music> implements Audio {

    // resource variables
    private File musicFile;
    private String musicName;
    private AudioManager manager;

    // audio variables
    private BoundedFloat volume;
    private BoundedFloat pitch;
    private BoundedFloat pan;
    private float loopPoint;

    public Music(AudioManager manager, String musicName, File musicFile)
        throws UnsupportedAudioFileException {
        this.manager = manager;
        this.musicName = musicName;
        this.musicFile = musicFile;

        try {
            Gdx.audio.newMusic(Gdx.files.absolute(this.musicFile.getAbsolutePath()));
        } catch (GdxRuntimeException exception) {
            UnsupportedAudioFileException thrown = new UnsupportedAudioFileException();
            thrown.initCause(exception);
            throw thrown;
        }

        this.volume = new BoundedFloat(0.0F, 1.0F, 1.0F);
        this.pitch = new BoundedFloat(0.5F, 2.0F, 1.0F);
        this.pan = new BoundedFloat(-1.0F, 1.0F, 0.0F);
        this.loopPoint = -1;
    }

    // ---------------- abstract method implementation ----------------

    @Override
    public String getAudioName() {
        return this.musicName;
    }

    @Override
    protected com.badlogic.gdx.audio.Music newReference() {
        return Gdx.audio.newMusic(Gdx.files.absolute(this.musicFile.getAbsolutePath()));
    }

    @Override
    protected Class<com.badlogic.gdx.audio.Music> getResourceClass() {
        return com.badlogic.gdx.audio.Music.class;
    }

    // ---------------- g/s audio parameters ----------------

    /**
     * Returns the volume of the music.
     * 
     * @return the volume of the music
     */
    public float getVolume() {
        return this.volume.get();
    }

    /**
     * Sets the volume of the music.
     * 
     * @param volume the new volume of the music
     */
    public void setVolume(float volume) {
        this.volume.set(volume);
        if (checkReference())
            this.getReference().setVolume(this.getFinalVolume(volume));
    }

    /**
     * Returns the pitch of the music.
     * 
     * @return the pitch of the music
     */
    public float getPitch() {
        return this.pitch.get();
    }

    /**
     * Sets the pitch value of the music.
     * 
     * @param pitch the new pitch of the music
     */
    public void setPitch(float pitch) {
        this.pitch.set(pitch);
        if (checkReference())
            ((OpenALMusic) this.getReference()).setPitch(this.pitch.get());
    }

    /**
     * Returns the panning value of the music.
     * 
     * @return the pan of the music
     */
    public float getPan() {
        return this.pan.get();
    }

    /**
     * Sets the panning value of the music.
     * 
     * @param pan the new pan of the music
     */
    public void setPan(float pan) {
        this.pan.set(pan);
        if (checkReference())
            this.getReference().setPan(pan, this.getFinalVolume(this.getVolume()));
    }

    /**
     * Returns whether or not the music is looping.
     * 
     * @return if this music is looping
     */
    public boolean isLooping() {
        return this.loopPoint >= 0;
    }

    /**
     * Sets whether or not the music is looping.
     * 
     * <p>When activated through this method, the loop point
     * is set to the beginning of the music track.</p>
     * 
     * @param flag whether or not the music is looping
     */
    public void setLooping(boolean flag) {
        if (flag) {
            this.loopPoint = 0;
        } else {
            this.loopPoint = -1;
        }
    }

    /**
     * Returns the looping point of the music.
     * 
     * <p>The looping point is the point the music will
     * return to after finishing, in the measure of
     * seconds.</p>
     * 
     * @return the looping point of the music
     */
    public float getLoopPoint() {
        if (this.loopPoint < 0) {
            return 0;
        }

        return this.loopPoint;
    }

    /**
     * Sets the looping point of the music.
     * 
     * @param loopPoint the point to return to after ending,
     *        in seconds
     */
    public void setLoopPoint(float loopPoint) {
        this.loopPoint = loopPoint;
    }

    // ---------------- playback methods ---------------- 

    /**
     * Returns whether or not this music is playing.
     * 
     * @return if the music is playing
     */
    public boolean isPlaying() {
        return this.getReference() != null && this.getReference().isPlaying();
    }

    /**
     * Plays the music with its current parameters.
     */
    public void play() {
        this.play(this.volume.get(), this.pitch.get(), this.pan.get());
    }

    /**
     * Plays the music with the given parameters.
     * 
     * @param volume the volume of the music
     * @param pitch the pitch of the music
     * @param pan the pan of the music
     */
    public void play(float volume, float pitch, float pan) {
        this.getReference().play();
        this.setVolume(volume);
        this.setPitch(pitch);
        this.setPan(pan);

        this.refreshLoop();
    }

    /**
     * Plays the music with its current parameters, looping.
     */
    public void loop() {
        this.loop(this.getVolume(), this.getPitch(), this.getPan());
    }

    /**
     * Plays the music with the given parameters, looping.
     * 
     * @param volume the volume of the music
     * @param pitch the pitch of the music
     * @param pan the pan of the music
     */
    public void loop(float volume, float pitch, float pan) {
        if (!this.isLooping()) {
            this.setLooping(true);
        }

        this.play(volume, pitch, pan);
    }

    /**
     * Plays the music with the given parameters, looping.
     * 
     * @param loopPoint the point at which the music will
     *        return to after ending, in seconds
     * @param volume the volume of the music
     * @param pitch the pitch of the music
     * @param pan the pan of the music
     */
    public void loop(float loopPoint, float volume, float pitch, float pan) {
        this.setLoopPoint(loopPoint);
        this.play(volume, pitch, pan);
    }

    // ---------------- internal methods ----------------

    /**
     * Internal method.
     * 
     * <p>Refreshes the completion listener and updates the
     * return point of looping.</p>
     */
    private void refreshLoop() {
        if (this.checkReference()) {
            this.getReference().setOnCompletionListener(music -> {
                music.setPosition(this.loopPoint);
                music.play();
            });
        }
    }

    /**
     * Internal method.
     * 
     * <p>Returns the final volume of the music after the
     * application of the master and music volume set by the
     * parenting {@link AudioManager}.</p>
     */
    private float getFinalVolume(float volume) {
        return manager.getMasterVolume() * manager.getMusicVolume() * volume;
    }

    /**
     * Internal method.
     * 
     * <p>Checks whether the reference currently exists.</p>
     * 
     * @return if {@link #getRawReference()} doesn't return
     *         null
     */
    private boolean checkReference() {
        return this.getRawReference() != null;
    }
}
