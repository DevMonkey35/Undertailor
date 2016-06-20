package me.scarlet.undertailor.engine.scheduler;

import me.scarlet.undertailor.engine.Processable;

/**
 * Functional class used to perform a task to run across
 * several frames of a running game instance.
 */
public interface Task extends Processable {

    /**
     * Returns the name of this {@link Task}.
     * 
     * @return the name of this Task
     */
    String getName();

    /**
     * Called when this {@link Task} finishes (when
     * {@link #process(Object...)} returns false).
     * 
     * @param forced if the task was ended preemptively by
     *        means of an error or a scheduler call
     */
    void onFinish(boolean forced);

    /**
     * Processes this {@link Task} for the current frame.
     * 
     * <p>The generic return value resolves to whether or
     * not this Task should keep running after this method
     * has been called. If false is returned, the Task is
     * removed from the host scheduler.</p>
     * 
     * @return if the Task should keep running after this
     *         frame
     */
    @Override
    boolean process(Object... params);
}
