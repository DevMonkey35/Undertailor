package me.scarlet.undertailor.resource;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

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
 * dispose of any disposable objects deemed no longer in
 * active use, by the means of object lifetime or if a
 * referent has removed itself from the list of its
 * users.</p>
 */
public abstract class Resource<T extends Disposable> {
    
    static Map<Class<? extends Disposable>, Long> lifetimeMapping;
    public static final long DEFAULT_LIFETIME = 3000; // 3s
    
    static {
        Resource.lifetimeMapping = new HashMap<>();
    }
    
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
        if(Resource.lifetimeMapping.containsKey(clazz)) {
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
        if(lifetime <= 100) { // Should not be less than 100ms.
            if(Resource.lifetimeMapping.containsKey(clazz)) Resource.lifetimeMapping.remove(clazz);
        } else {
            Resource.lifetimeMapping.put(clazz, lifetime);
        }
    }
    
    private T disposable;
    private long lastAccessTime;
    private Map<Object, Object> referrers; // Used more like a list/set than a map.
    
    public Resource() {
        this.disposable = null;
        this.lastAccessTime = TimeUtils.millis();
        this.referrers = new WeakHashMap<>();
    }
    
    /**
     * Returns the raw instance of the resource, which may
     * or may not be null.
     * 
     * <p>Retrieving the resource through this method does
     * not trigger a refresh of its access lifetime.</p>
     * 
     * @return the currently held instance of the resource
     */
    final T getRawReference() {
        return this.disposable;
    }
    
    /**
     * Returns an instance of the resource, of which may not
     * be the same since the last time it was accessed.
     * 
     * @return an instance of the resource
     */
    final T getReference() {
        if(this.disposable == null) {
            this.disposable = this.newReference();
        }
        
        this.lastAccessTime = TimeUtils.millis();
        return this.disposable;
    }
    
    /**
     * Disposes of the underlying resource, allowing the
     * system to reclaim the memory used by the former.
     */
    public final void dispose() {
        if(this.disposable != null) {
            this.referrers.clear();
            this.disposable.dispose();
            this.disposable = null;
            this.lastAccessTime = -1;
        }
    }
    
    /**
     * Generates a new reference to the underlying resource.
     * 
     * <p>This method is called when the resource is not
     * currently active in memory and has to be recreated
     * for usage, an event that only occurs during the
     * wrapper's first instantiation or after it has been
     * previously disposed.</p>
     */
    abstract T newReference();
    
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
    boolean isDisposable() {
        return this.disposable != null
            && this.referrers.isEmpty()
            && TimeUtils.timeSinceMillis(this.lastAccessTime) > Resource.getLifetimeForResource(disposable.getClass());
    }
}
