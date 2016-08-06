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

package me.scarlet.undertailor.engine.scheduler;

import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.OrderedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.engine.Destructible;
import me.scarlet.undertailor.engine.Environment;
import me.scarlet.undertailor.engine.Processable;
import me.scarlet.undertailor.engine.Subsystem;

import java.util.Iterator;

/**
 * Implementation of a task handling class performing sets
 * of actions every frame.
 */
public class Scheduler implements Processable, Subsystem, Destructible {

    public static long nextId;
    static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    static {
        nextId = 0;
    }

    private Environment env;
    private boolean destroyed;
    private LongMap<Task> tasks;
    private OrderedMap<Long, Task> activeTasks;

    public Scheduler(Environment env) {
        this.env = env;
        this.destroyed = false;
        this.tasks = new LongMap<>();
        this.activeTasks = new OrderedMap<>();
    }

    // ---------------- abstract method implementation ----------------

    @Override
    public Environment getEnvironment() {
        return this.env;
    }

    @Override
    public boolean process() {
        Iterator<LongMap.Entry<Task>> iterator = tasks.entries().iterator();
        while (iterator.hasNext()) {
            LongMap.Entry<Task> entry = iterator.next();
            long id = entry.key;
            Task task = entry.value;
            String taskName =
                (task.getName() == null ? "#" + id : task.getName() + " (#" + id + ")");

            try {
                if (!task.process()) {
                    log.debug("task " + taskName + " finished and was removed");
                    task.onFinish(false);
                    iterator.remove();
                }
            } catch (Exception e) {
                log.warn("task " + taskName + " was removed due to caught error: "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
                e.printStackTrace();
                task.onFinish(true);
                iterator.remove();
            }
        }

        if (activeTasks.size > 0) {
            long id = activeTasks.orderedKeys().get(0);
            Task task = activeTasks.get(id);

            if (task != null) {
                String taskName =
                    (task.getName() == null ? "#" + id : task.getName() + " (#" + id + ")");
                try {
                    if (!task.process()) {
                        this.activeTasks.remove(id);
                        log.debug("active task " + taskName + " finished and was removed");
                        task.onFinish(false);
                    }
                } catch (Exception e) {
                    log.warn("active task " + taskName + " was removed due to caught error: "
                        + e.getClass().getSimpleName() + ": " + e.getMessage());
                    e.printStackTrace();
                    task.onFinish(true);
                    this.activeTasks.remove(id);
                }
            }
        }

        return true;
    }

    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }

    @Override
    public void destroy() {
        if (this.destroyed) {
            return;
        }

        this.activeTasks.clear();
        this.tasks.clear();

        this.activeTasks = null;
        this.tasks = null;
        this.destroyed = true;
    }

    // ---------------- functional methods ----------------

    /**
     * Registers a new {@link Task} to be processed by this
     * {@link Scheduler}.
     * 
     * <p>An "active" task is a task that is not executed
     * with other tasks of its type. That is, active tasks
     * work on a first-come, first-serve basis, where until
     * the current task has decided its work is done, no
     * other active task will be processed. Tasks are
     * otherwise executed consecutively in an undefined
     * order.</p>
     * 
     * <p>This method will quietly ignore operations where
     * <code>task</code> is null.</p>
     * 
     * @param task the task to register
     * @param active whether or not to register the task as
     *        an active task
     * 
     * @return the id assigned to the task
     */
    public long registerTask(Task task, boolean active) {
        if (task == null) {
            return -1;
        }

        long id = this.getNextId();
        String taskName = (task.getName() == null ? "#" + id : task.getName() + " (#" + id + ")");
        if (active) {
            activeTasks.put(id, task);
            log.debug("active task " + taskName + " registered");
        } else {
            tasks.put(id, task);
            log.debug("task " + taskName + " registered");
        }

        return id;
    }

    /**
     * Cancels the task associated with the provided ID.
     * 
     * @param id the ID of the task to cancel
     */
    public void cancelTask(long id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            String taskName =
                (task.getName() == null ? "#" + id : task.getName() + " (#" + id + ")");

            task.onFinish(true);
            log.debug("task " + taskName + " was removed by scheduler call");
            tasks.remove(id);
        }

        if (activeTasks.containsKey(id)) {
            Task task = activeTasks.get(id);
            String taskName =
                (task.getName() == null ? "#" + id : task.getName() + " (#" + id + ")");

            task.onFinish(true);
            log.debug("active task " + taskName + " was removed by scheduler call");
            activeTasks.remove(id);
        }
    }

    /**
     * Returns whether or not a given task currently exists
     * in the {@link Scheduler}.
     * 
     * <p>Tasks do not exist in the Scheduler after they've
     * finished; this method can be used to check whether a
     * task is still running.</p>
     * 
     * @param id the ID of the task to check
     * 
     * @return whether or not the task exists
     */
    public boolean hasTask(long id) {
        return tasks.containsKey(id) || activeTasks.containsKey(id);
    }

    // ---------------- internal methods ----------------

    /**
     * Internal method.
     * 
     * <p>Generates a new task ID.</p>
     */
    private long getNextId() {
        if (nextId == Long.MAX_VALUE) {
            nextId = 0;
        }

        return nextId++;
    }
}
