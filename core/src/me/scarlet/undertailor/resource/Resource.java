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

package me.scarlet.undertailor.resource;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Wrapping class providing the functional methods for usage
 * of managed resources created by associated
 * ResourceFactories ({@link ResourceFactory}).
 * 
 * <p>Implementation of this class should purely be
 * functional and have bare minimum to no data storage, as
 * the data associated with instances of this class will not
 * be defined as the consistent data of the underlying
 * managed resource. All data should be stored by a parent
 * ResourceFactory, and should be accessed by requesting
 * from the former should the functions of this class
 * require them.</p>
 * 
 * @param <T> the type of the underlying Disposable provided
 *        by parent ResourceFactory
 */
public abstract class Resource<T extends Disposable> {

    T disposable;
    boolean loaded;
    Array<Runnable> actionQueue;

    protected Resource() {
        this.loaded = false;
        this.disposable = null;
        this.actionQueue = new Array<>(true, 16);
    }

    /**
     * Returns the current instance of the
     * {@link Disposable} object provided by the factory
     * that generated this {@link Resource}.
     * 
     * <p>This method may return null or return an unusable
     * Disposable due to already having been disposed,
     * though if either occurs then it is safe to assume the
     * object is no longer in use, and the object should no
     * longer be operational.</p>
     * 
     * @return the Disposable resource
     */
    protected T getReference() {
        return this.disposable;
    }

    /**
     * Queues an action to be done once this
     * {@link Resource} has been fully loaded.
     * 
     * <p>Care should be exercised when allowing a Resource
     * to be accessed during loading. It is encouraged to
     * follow the provided guidelines.</p>
     * 
     * <pre>
     * * Any task requiring the underlying {@link Disposable}
     *   should ALWAYS be queued.
     * * Keep track of what is queuing actions. A queued action
     *   should not be scheduling another queued action.
     * * Methods whose return type is not void should strictly
     *   return null while the underlying Dispoasble has not
     *   been loaded.
     * </pre>
     * 
     * @param action the Runnable to execute on completion
     */
    protected void queueAction(Runnable action) {
        if(this.actionQueue != null) {
            this.actionQueue.add(action);
        } else {
            action.run();
        }
    }

    /**
     * Returns whether or not this {@link Resource}'s
     * underlying {@link Disposable} has been loaded.
     * 
     * @return if this Resource has been fully loaded
     */
    public boolean isLoaded() {
        return this.loaded;
    }

    /**
     * Internal method.
     * 
     * <p>Called by the owning {@link ResourceFactory} to
     * notify that this {@link Resource}'s
     * {@link Disposable} has successfully been loaded, and
     * can therefore execute tasks that require it.</p>
     */
    void load(T disposable) {
        this.loaded = true;
        this.disposable = disposable;
        this.actionQueue.forEach(Runnable::run);
        this.actionQueue = null;
    }
}
