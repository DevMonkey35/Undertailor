package me.scarlet.undertailor.gfx.animation;

import com.badlogic.gdx.utils.TimeUtils;

import me.scarlet.undertailor.gfx.Renderable;

/**
 * Base class for a collection of graphics stitched
 * together, made to look like a single moving entity.
 */
public abstract class Animation implements Renderable {

    private long startTime; // The time the animation was started, or -1 if not started.
    private long pauseTime; // The time tha animation was paused, or -1 if not paused.
    private boolean looping; // Whether or not the animation is looping.

    /**
     * Returns the current runtime of this {@link Animation}
     * , in milliseconds.
     * 
     * @return the Animation's runtime since starting, in
     *         milliseconds
     */
    public long getRuntime() {
        if (this.isPaused()) {
            return TimeUtils.timeSinceMillis(this.startTime)
                - TimeUtils.timeSinceMillis(this.pauseTime);
        } else if(this.isPlaying()) {
            return TimeUtils.timeSinceMillis(this.startTime);
        } else {
            return 0;
        }
    }

    /**
     * Sets the current runtime of this {@link Animation},
     * in milliseconds.
     * 
     * @param runtime the new runtime of the Animation
     */
    public void setRuntime(long runtime) {
        this.startTime = TimeUtils.millis() - runtime;
        if (this.isPaused()) {
            this.pause();
        }
    }

    /**
     * Returns whether or not this {@link Animation} repeats
     * its playback after finishing.
     */
    public boolean isLooping() {
        return this.looping;
    }

    /**
     * Set whether or not this {@link Animation} repeats its
     * playback after finishing.
     * 
     * @param flag the new state of looping
     */
    public void setLooping(boolean flag) {
        this.looping = flag;
    }

    /**
     * Returns whether or not this {@link Animation} is
     * playing.
     * 
     * @return if the Animation is playing
     */
    public boolean isPlaying() {
        return this.startTime >= 0 && !this.isPaused();
    }

    /**
     * Plays the animation from where it left off, or from
     * the beginning if it had been previously stopped or
     * never played.
     */
    public void play() {
        if (this.isPaused()) {
            this.startTime += TimeUtils.timeSinceMillis(this.pauseTime);
            this.pauseTime = -1;
        } else {
            if (!this.isPlaying()) {
                this.startTime = TimeUtils.millis();
            }
        }
    }

    /**
     * Returns whether or not this {@link Animation} has
     * been paused.
     * 
     * @return if the Animation is paused
     */
    public boolean isPaused() {
        return this.pauseTime >= 0;
    }

    /**
     * Pauses this {@link Animation}, freezing it at its
     * current frame and runtime.
     */
    public void pause() {
        if(this.isPlaying() && !this.isPaused()) {
            this.pauseTime = TimeUtils.millis();
        }
    }

    /**
     * Stops the animation, returning its runtime to 0ms and
     * freezing it at the first frame.
     */
    public void stop() {
        this.pauseTime = -1;
        this.startTime = -1;
    }
}
