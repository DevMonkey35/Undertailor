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

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Wrapping class for any object that is considered a
 * managed resource; that is, not under the control of the
 * Java Virtual Machine that determines whether or not the
 * object is actively in use and therefore viable to be
 * trashed and reclaimed by the garbage collector.
 * 
 * <p>The aim of the class is to ensure that any underlying
 * resource is disposed of whenever it is no longer in use.
 * To perform this task, a wrapper (instances of this class)
 * is tasked with tracking a disposable object. When
 * requested, the disposable object should always be
 * available, or will be made available upon making the
 * request. This class works in tandem with the
 * {@link ResourceHandler} class which will track and
 * dispose of any Resources no longer in active use, by the
 * means of becoming weakly reachable as determined by the
 * Java Virtual Machine or through the end of its lifetime
 * after the last time it was accessed.</p>
 * 
 * <p>With the above noted, the underlying resource should
 * <strong>never</strong> be directly stored by classes that
 * wish to use the resource, and should instead always
 * retrieve the resource through the use of this class, if
 * the implementation permits it as opposed to being a proxy
 * for the functionality of the former.</p>
 */
public abstract class Resource<T extends Disposable> {

    // ---------------- static variables and initializer ----------------

    static Map<Class<? extends Disposable>, Long> lifetimeMapping;
    protected static Map<Disposable, WeakReference<Resource<?>>> livingResources;

    public static final long DEFAULT_LIFETIME = 3000; // 3s

    static {
        Resource.lifetimeMapping = new HashMap<>();
        Resource.livingResources = new WeakHashMap<>();
    }

    // ---------------- static methods ----------------

    /**
     * Returns the lifetime for resources of the given type.
     * 
     * <p>If no lifetime was set, the default lifetime under
     * the constant {@link Resource#DEFAULT_LIFETIME} is
     * returned instead.</p>
     * 
     * @param clazz the type of resource to get a lifetime
     *        value for
     * 
     * @return the access lifetime, in milliseconds, for the
     *         provided type
     */
    public static long getLifetimeForResource(Class<? extends Disposable> clazz) {
        if (Resource.lifetimeMapping.containsKey(clazz)) {
            return Resource.lifetimeMapping.get(clazz);
        }

        return Resource.DEFAULT_LIFETIME;
    }

    /**
     * Sets the lifetime for resources of the given type.
     * 
     * <p>Setting a lifetime to a value less than 101 will
     * result in the lifetime being reset to the value of
     * {@link Resource#DEFAULT_LIFETIME}.</p>
     * 
     * @param clazz the type of resource to set a lifetime
     *        for
     * @param lifetime the access lifetime period, in
     *        milliseconds, for the provided type
     */
    public static void setLifetimeForResource(Class<? extends Disposable> clazz, long lifetime) {
        if (lifetime <= 100) { // Should not be less than 100ms.
            if (Resource.lifetimeMapping.containsKey(clazz))
                Resource.lifetimeMapping.remove(clazz);
        } else {
            Resource.lifetimeMapping.put(clazz, lifetime);
        }
    }

    // ---------------- object ----------------

    private T disposable;
    private long lastAccessTime;

    protected Resource() {
        this.disposable = null;
        this.lastAccessTime = TimeUtils.millis();
    }

    /**
     * Returns the raw instance of the resource, which may
     * or may not be null.
     * 
     * <p>Retrieving the resource through this method does
     * not trigger a refresh of its last access time.</p>
     * 
     * @return the currently held instance of the resource
     */
    protected final T getRawReference() {
        return this.disposable;
    }

    /**
     * Returns an instance of the resource, of which may not
     * be the same since the last time it was accessed.
     * 
     * @return an instance of the resource
     */
    protected final T getReference() {
        if (this.disposable == null) {
            this.createReference();
        }

        this.lastAccessTime = TimeUtils.millis();
        return this.disposable;
    }

    /**
     * Returns the count of milliseconds since the last time
     * this resource was accessed.
     * 
     * @return how long its been since
     *         {@link #getReference()} was called
     */
    protected final long getTimeSinceLastAccess() {
        return TimeUtils.timeSinceMillis(this.lastAccessTime);
    }

    /**
     * Disposes of the underlying resource, allowing the
     * system to reclaim the memory used by the former.
     */
    public final void dispose() {
        this.onDispose();
        if (this.disposable != null) {
            Resource.livingResources.remove(this.disposable);
            this.disposable.dispose();
            this.disposable = null;
            this.lastAccessTime = -1;
        }
    }

    /**
     * Called right before the system disposes of the
     * underlying resource.
     */
    protected void onDispose() {};

    /**
     * Queries whether or not the underlying resource is
     * disposable, that is, ready to be reclaimed.
     * 
     * <p>Implementations may override this method in order
     * to ensure the resource is not disposed of while in
     * use, <strong>however this method has a base
     * implementation that must be executed in order to be
     * properly disposed of.</strong></p>
     * 
     * <pre>
     * boolean isDisposable() {
     *     boolean condition; // your condition here
     *     return super.isDisposable() && condition;
     * }
     * </pre>
     */
    protected boolean isDisposable() {
        return this.disposable != null && TimeUtils.timeSinceMillis(this.lastAccessTime) > Resource
            .getLifetimeForResource(disposable.getClass());
    }

    private void createReference() {
        this.disposable = this.newReference();
        Resource.livingResources.put(this.disposable, new WeakReference<>(this));
    }

    // ---------------- abstract declarations ----------------

    /**
     * Generates a new reference to the underlying resource.
     * 
     * <p>This method is called when the resource is not
     * currently active in memory and has to be recreated
     * for usage, an event that only occurs during the
     * wrapper's first instantiation or after it has been
     * previously disposed.</p>
     */
    protected abstract T newReference();

    /**
     * Returns the class type of the underlying resouce.
     * 
     * @return the class of the underlying disposable object
     */
    protected abstract Class<T> getResourceClass();
}
