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
import me.scarlet.undertailor.resource.ResourceFactory;
import me.scarlet.undertailor.util.BoundedFloat;

import java.io.File;

import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * {@link ResourceFactory} implementation for generating
 * {@link MusicFactory.Music} instances.
 */
public class MusicFactory extends ResourceFactory<com.badlogic.gdx.audio.Music, MusicFactory.Music>
    implements Audio {

    /**
     * {@link Resource} implementation for interfacing with
     * long tracks of audio (
     * {@link com.badlogic.gdx.audio.Music}).
     */
    public static class Music extends Resource<com.badlogic.gdx.audio.Music> implements Audio {

        private String audioName;
        private AudioManager manager;
        private MusicFactory factory;

        private Music(String audioName, AudioManager manager, MusicFactory factory) {
            this.factory = factory;
            this.audioName = audioName;
            this.manager = manager;
        }

        // ---------------- abstract method implementation ----------------

        @Override
        public String getAudioName() {
            return this.audioName;
        }

        // ---------------- g/s audio parameters ----------------

        /**
         * Returns the volume of the music.
         * 
         * @return the volume of the music
         */
        public float getVolume() {
            return factory.volume.get();
        }

        /**
         * Sets the volume of the music.
         * 
         * @param volume the new volume of the music
         */
        public void setVolume(float volume) {
            factory.volume.set(volume);
            this.getReference().setVolume(this.getFinalVolume(volume));
        }

        /**
         * Returns the pitch of the music.
         * 
         * @return the pitch of the music
         */
        public float getPitch() {
            return factory.pitch.get();
        }

        /**
         * Sets the pitch value of the music.
         * 
         * @param pitch the new pitch of the music
         */
        public void setPitch(float pitch) {
            factory.pitch.set(pitch);
            ((OpenALMusic) this.getReference()).setPitch(factory.pitch.get());
        }

        /**
         * Returns the panning value of the music.
         * 
         * @return the pan of the music
         */
        public float getPan() {
            return factory.pan.get();
        }

        /**
         * Sets the panning value of the music.
         * 
         * @param pan the new pan of the music
         */
        public void setPan(float pan) {
            factory.pan.set(pan);
            this.getReference().setPan(pan, this.getFinalVolume(this.getVolume()));
        }

        /**
         * Returns whether or not the music is looping.
         * 
         * @return if this music is looping
         */
        public boolean isLooping() {
            return factory.loopPoint >= 0;
        }

        /**
         * Sets whether or not the music is looping.
         * 
         * <p>When activated through this method, the loop
         * point is set to the beginning of the music
         * track.</p>
         * 
         * @param flag whether or not the music is looping
         */
        public void setLooping(boolean flag) {
            if (flag) {
                factory.loopPoint = 0;
            } else {
                factory.loopPoint = -1;
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
            if (factory.loopPoint < 0) {
                return 0;
            }

            return factory.loopPoint;
        }

        /**
         * Sets the looping point of the music.
         * 
         * @param loopPoint the point to return to after
         *        ending, in seconds
         */
        public void setLoopPoint(float loopPoint) {
            factory.loopPoint = loopPoint;
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
            this.play(factory.volume.get(), factory.pitch.get(), factory.pan.get());
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
         * Plays the music with its current parameters,
         * looping.
         */
        public void loop() {
            this.loop(this.getVolume(), this.getPitch(), this.getPan());
        }

        /**
         * Plays the music with the given parameters,
         * looping.
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
         * Plays the music with the given parameters,
         * looping.
         * 
         * @param loopPoint the point at which the music
         *        will return to after ending, in seconds
         * @param volume the volume of the music
         * @param pitch the pitch of the music
         * @param pan the pan of the music
         */
        public void loop(float loopPoint, float volume, float pitch, float pan) {
            this.setLoopPoint(loopPoint);
            this.play(volume, pitch, pan);
        }

        /**
         * Stops the music from playing.
         */
        public void stop() {
            if(this.isPlaying()) {
                this.getReference().stop();
            }
        }

        // ---------------- internal methods ----------------

        /**
         * Internal method.
         * 
         * <p>Refreshes the completion listener and updates
         * the return point of looping.</p>
         */
        private void refreshLoop() {
            if (factory.loopPoint >= 0) {
                this.getReference().setOnCompletionListener(music -> {
                    music.setPosition(factory.loopPoint);
                    music.play();
                });
            } else {
                this.getReference().setOnCompletionListener(null);
            }
        }

        /**
         * Internal method.
         * 
         * <p>Returns the final volume of the music after
         * the application of the master and music volume
         * set by the parenting {@link AudioManager}.</p>
         */
        private float getFinalVolume(float volume) {
            return manager.getMasterVolume() * manager.getMusicVolume() * volume;
        }
    }

    // ---------------- resource variables ----------------

    private File musicFile;
    private String audioName;
    private AudioManager manager;

    // ---------------- music variables ----------------

    private BoundedFloat volume, pitch, pan;
    private float loopPoint;

    public MusicFactory(String audioName, AudioManager manager, File musicFile) throws UnsupportedAudioFileException {
        this.musicFile = musicFile;
        this.audioName = audioName;
        this.manager = manager;
        
        try {
            this.newDisposable().dispose(); // try loading it once and then dispose of it
        } catch(GdxRuntimeException e) {
            UnsupportedAudioFileException thrown =
                new UnsupportedAudioFileException("Could not load music file at "
                    + this.musicFile.getAbsolutePath() + " (unsupported type, ogg/mp3/wav only)");
            thrown.initCause(e);
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
        return this.audioName;
    }

    @Override
    protected com.badlogic.gdx.audio.Music newDisposable() {
        return Gdx.audio.newMusic(Gdx.files.absolute(this.musicFile.getAbsolutePath()));
    }

    @Override
    protected Music newResource() {
        return new Music(this.audioName, this.manager, this);
    }

    @Override
    public boolean isDisposable(com.badlogic.gdx.audio.Music music) {
        return !music.isPlaying();
    }

    /**
     * Forcefully stops music coming from any instance
     * spawned by this {@link MusicFactory}.
     */
    public void stop() {
        if(this.getDisposable() != null) {
            this.getDisposable().stop();
        }
    }
}
