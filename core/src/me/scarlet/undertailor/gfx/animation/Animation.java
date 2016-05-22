package me.scarlet.undertailor.gfx.animation;

import me.scarlet.undertailor.gfx.Renderable;

/**
 * Base class for a collection of graphics stitched
 * together, made to look like a single moving entity.
 */
public abstract class Animation implements Renderable {
    
    /**
     * Returns the current runtime of this {@link Animation}
     * , in milliseconds.
     * 
     * @return the Animation's runtime since starting, in
     *         milliseconds
     */
    public abstract long getRuntime();
    
    /**
     * Sets the current runtime of this {@link Animation},
     * in milliseconds.
     * 
     * @param runtime the new runtime of the Animation
     */
    public abstract void setRuntime(long runtime);
    
    /**
     * Returns whether or not this {@link Animation} repeats
     * its playback after finishing.
     */
    public abstract void isLooping();
    
    /**
     * Set whether or not this {@link Animation} repeats its
     * playback after finishing.
     * 
     * @param flag the new state of looping
     */
    public abstract void setLooping(boolean flag);
    
    /**
     * Returns whether or not this {@link Animation} is
     * playing.
     * 
     * @return if the Animation is playing
     */
    public abstract boolean isPlaying();
    
    /**
     * Plays the animation from where it left off, or from
     * the beginning if it had been previously stopped or
     * never played.
     */
    public abstract void play();
    
    /**
     * Returns whether or not this {@link Animation} has
     * been paused.
     * 
     * @return if the Animation is paused
     */
    public abstract boolean isPaused();
    
    /**
     * Pauses this {@link Animation}, freezing it at its
     * current frame and runtime.
     */
    public abstract void pause();
    
    /**
     * Stops the animation, returning its runtime to 0ms and
     * freezing it at the first frame.
     */
    public abstract void stop();
    
}
