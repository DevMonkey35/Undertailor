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
import mod.com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;
import org.lwjgl.openal.AL10;

import me.scarlet.undertailor.resource.Resource;
import me.scarlet.undertailor.util.BoundedFloat;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Wrapping implementation of short sound effects.
 */
public class Sound extends Resource<com.badlogic.gdx.audio.Sound> implements Audio {

    /**
     * Tracks data for a specified ID under a Sound
     * instance.
     */
    public static class SoundData {

        private long id;
        private Sound parent;

        private BoundedFloat volume;
        private BoundedFloat pitch;
        private BoundedFloat pan;
        private boolean looping;

        private SoundData(Sound parent, long id) {
            this.id = id;
            this.parent = parent;

            this.volume = new BoundedFloat(0.0F, 1.0F, 1.0F);
            this.pitch = new BoundedFloat(0.5F, 2.0F, 1.0F);
            this.pan = new BoundedFloat(-1.0F, 1.0F, 0.0F);
            this.looping = false;
        }

        /**
         * Returns the volume of the sound.
         * 
         * @return the volume
         */
        public float getVolume() {
            return this.volume.get();
        }

        /**
         * Sets the volume of the sound.
         * 
         * @param volume the new volume
         */
        public void setVolume(float volume) {
            this.volume.set(volume);
            this.parent.getReference().setVolume(this.id, parent.getFinalVolume(this.volume.get()));
        }

        /**
         * Returns the pitch of the sound.
         * 
         * @return the pitch
         */
        public float getPitch() {
            return this.pitch.get();
        }

        /**
         * Sets the pitch of the sound.
         * 
         * @param pitch the new pitch
         */
        public void setPitch(float pitch) {
            this.pitch.set(pitch);
            this.parent.getReference().setPitch(this.id, this.pitch.get());
        }

        /**
         * Returns the pan of the sound.
         * 
         * @return the pan
         */
        public float getPan() {
            return this.pan.get();
        }

        /**
         * Sets the pan of the sound.
         * 
         * @param pan the new pan
         */
        public void setPan(float pan) {
            this.pan.set(pan);
            this.parent.getReference().setPan(this.id, this.pan.get(),
                parent.getFinalVolume(this.volume.get()));
        }

        /**
         * Returns whether or not the sound is looping.
         * 
         * @return looping?
         */
        public boolean isLooping() {
            return this.looping;
        }

        /**
         * Sets whether or not the sound is looping.
         * 
         * @param flag new looping state
         */
        public void setLooping(boolean flag) {
            this.looping = flag;
            this.parent.getReference().setLooping(this.id, flag);
        }

        /**
         * Returns whether or not this sound is still
         * playing.
         * 
         * @return if the sound is playing
         */
        public boolean isPlaying() {
            int sourceId = ((OpenALAudio) Gdx.audio).getSoundSourceId(this.id);
            if (sourceId == -1)
                return false;

            return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
        }

        /**
         * Stops the sound's playback.
         */
        public void stop() {
            this.parent.getReference().stop(this.id);
        }
    }

    public static final long RESOURCE_LIFETIME = 3000;

    static {
        Resource.setLifetimeForResource(com.badlogic.gdx.audio.Sound.class,
            Sound.RESOURCE_LIFETIME);
    }

    // resource variables
    private File soundFile;
    private String soundName;
    private AudioManager manager;
    private Map<Long, WeakReference<SoundData>> soundData;

    public Sound(AudioManager manager, String soundName, File soundFile)
        throws UnsupportedAudioFileException {
        this.manager = manager;
        this.soundName = soundName;
        this.soundFile = soundFile;
        this.soundData = new HashMap<>();

        try {
            Gdx.audio.newSound(Gdx.files.absolute(this.soundFile.getAbsolutePath()));
        } catch (GdxRuntimeException exception) {
            UnsupportedAudioFileException thrown = new UnsupportedAudioFileException();
            thrown.initCause(exception);
            throw thrown;
        }
    }

    @Override
    protected com.badlogic.gdx.audio.Sound newReference() {
        return Gdx.audio.newSound(Gdx.files.absolute(this.soundFile.getAbsolutePath()));
    }

    @Override
    protected boolean isDisposable() {
        return super.isDisposable() && !this.isPlaying();
    }

    @Override
    protected Class<com.badlogic.gdx.audio.Sound> getResourceClass() {
        return com.badlogic.gdx.audio.Sound.class;
    }

    @Override
    protected void onDispose() {
        this.soundData.clear();
    }

    @Override
    public String getAudioName() {
        return this.soundName;
    }

    /**
     * Plays a new instance of the underlying
     * {@link com.badlogic.gdx.audio.Sound}, looping.
     * 
     * @param volume the volume to play the sound at
     * @param pitch the pitch to play the sound at
     * @param pan the pan to play the sound with
     * 
     * @return the SoundData associated with the now-playing
     *         sound
     */
    public SoundData loop(float volume, float pitch, float pan) {
        float finalVolume = this.getFinalVolume(volume);
        long id = this.getReference().loop(finalVolume, pitch, pan);

        SoundData data = new SoundData(this, id);
        data.volume.set(volume);
        data.pitch.set(pitch);
        data.pan.set(pan);
        data.looping = true;

        this.soundData.put(id, new WeakReference<>(data));
        return data;
    }

    /**
     * Plays a new instance of the underlying
     * {@link com.badlogic.gdx.audio.Sound}, looping,
     * providing default parameters.
     * 
     * @return the SoundData associated with the now-playing
     *         sound
     */
    public SoundData loop() {
        return this.loop(1.0F, 1.0F, 0.0F);
    }

    /**
     * Plays a new instance of the underlying
     * {@link com.badlogic.gdx.audio.Sound}.
     * 
     * @param volume the volume to play the sound at
     * @param pitch the pitch to play the sound at
     * @param pan the pan to play the sound with
     * 
     * @return the SoundData associated with the now-playing
     *         sound
     */
    public SoundData play(float volume, float pitch, float pan) {
        float finalVolume = this.getFinalVolume(volume);
        long id = this.getReference().play(finalVolume, pitch, pan);

        SoundData data = new SoundData(this, id);
        data.volume.set(volume);
        data.pitch.set(pitch);
        data.pan.set(pan);

        this.soundData.put(id, new WeakReference<>(data));
        return data;
    }

    /**
     * Plays a new instance of the underlying
     * {@link com.badlogic.gdx.audio.Sound}, providing
     * default parameters.
     * 
     * @return the SoundData associated with the now-playing
     *         sound
     */
    public SoundData play() {
        return this.play(1.0F, 1.0F, 0.0F);
    }

    /**
     * Stops all running instances of the underlying
     * {@link com.badlogic.gdx.audio.Sound}.
     */
    public void stop() {
        this.getReference().stop();
    }

    /**
     * Internal method.
     * 
     * <p>Tests whether or not there is any running instance
     * of the Sound currently playing. Since playback of a
     * specific ID cannot be restarted, it will also remove
     * the IDs of entries that are not playing.</p>
     */
    private boolean isPlaying() {
        Iterator<Long> keySet = this.soundData.keySet().iterator();
        long current = -1;
        while (keySet.hasNext()) {
            current = keySet.next();

            if (this.soundData.get(current).get() == null) {
                keySet.remove();
                continue;
            }

            int sourceId = ((OpenALAudio) Gdx.audio).getSoundSourceId(current);
            if (sourceId != -1
                && AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING) {
                return true;
            } else {
                keySet.remove();
            }
        }

        return false;
    }

    /**
     * Internal method.
     * 
     * <p>Returns the final volume of the sound after the
     * application of the master and sound volume set by the
     * parenting {@link AudioManager}.</p>
     */
    private float getFinalVolume(float volume) {
        return manager.getMasterVolume() * manager.getSoundVolume() * volume;
    }
}
