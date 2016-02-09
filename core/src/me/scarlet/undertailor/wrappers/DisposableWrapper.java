/* 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor.wrappers;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class DisposableWrapper<T extends Disposable> {
    
    /*public static boolean isWrapper(LuaValue value) {
        return value instanceof LuaAnimation
                || value instanceof LuaSound
                || value instanceof LuaMusic;
    }*/
    
    private static Map<Class<? extends DisposableWrapper<?>>, Set<DisposableWrapper<?>>> instances;
    public static final long DEFAULT_LIFETIME = 10000; // 10s
    
    static {
        instances = new HashMap<>();
    }
    
    public static Map<Class<? extends DisposableWrapper<?>>, Set<DisposableWrapper<?>>> getAllWrappers() {
        return instances;
    }
    
    public static Set<DisposableWrapper<?>> getWrappers(Class<? extends DisposableWrapper<?>> clazz) {
        return instances.get(clazz);
    }
    
    public static <T> void resetLifetime(Class<? extends DisposableWrapper<?>> clazz, T ref) {
        if(getWrappers(clazz) != null) {
            for(DisposableWrapper<?> wrapper : getWrappers(clazz)) {
                if(wrapper.getRawReference().equals(ref)) {
                    wrapper.lastAccess = TimeUtils.millis();
                    return;
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void registerInstance(DisposableWrapper<?> wrapper) {
        if(!instances.containsKey(wrapper.getClass())) {
            instances.put((Class<? extends DisposableWrapper<?>>) wrapper.getClass(), new HashSet<>());
        }
        
        instances.get(wrapper.getClass()).add(wrapper);
    }
    
    private T disposable;
    private long lastAccess;
    private boolean alwaysAlive;
    private Map<Object, Object> referrers;
    protected DisposableWrapper(T disposable) {
        this.disposable = disposable;
        
        if(!isDisposed()) {
            this.lastAccess = TimeUtils.millis();
        } else {
            this.lastAccess = -1;
        }
        
        registerInstance(this);
        this.referrers = new WeakHashMap<>();
    }
    
    protected T getRawReference() {
        return disposable;
    }
    
    public final T getReference() {
        synchronized(this) {
            if(this.isDisposed()) {
                disposable = this.newReference();
            }
            
            this.lastAccess = TimeUtils.millis();
            return disposable;
        }
    }
    
    public boolean hasReferrers() {
        return !this.referrers.isEmpty();
    }
    
    public final T getReference(Object referrer) {
        if(!referrers.containsKey(referrer)) {
            referrers.put(referrer, null);
        }
        
        return getReference();
    }
    
    public void removeReference(Object referrer) {
        if(referrers.containsKey(referrer)) {
            referrers.remove(referrer);
        }
        
        this.lastAccess = TimeUtils.millis();
    }
    
    public long getLastAccessTime() {
        if(lastAccess <= -1) {
            return 0;
        }
        
        return TimeUtils.timeSinceMillis(lastAccess);
    }
    
    public boolean isAlwaysAlive() {
        return alwaysAlive;
    }
    
    public final void setAlwaysAlive(boolean flag) {
        synchronized(this) {
            this.alwaysAlive = flag;
        }
        
        if(this.alwaysAlive) {
            getReference();
        }
    }
    
    public boolean isDisposed() {
        return disposable == null;
    }
    
    public final boolean dispose() {
        if(!this.isDisposed() && this.allowDispose()) {
            synchronized(this) {
                disposable.dispose();
                this.lastAccess = -1;
                disposable = null;
                
                return true;
            }
        }
        
        return false;
    }
    
    public long getMaximumLifetime() {
        return DEFAULT_LIFETIME;
    }
    
    public abstract T newReference();
    public abstract boolean allowDispose();
}
