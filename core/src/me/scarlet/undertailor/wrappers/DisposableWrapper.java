package me.scarlet.undertailor.wrappers;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import me.scarlet.undertailor.lua.LuaAnimation;
import me.scarlet.undertailor.lua.LuaMusic;
import me.scarlet.undertailor.lua.LuaSound;
import org.luaj.vm2.LuaValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class DisposableWrapper<T extends Disposable> {
    
    public static boolean isWrapper(LuaValue value) {
        return value instanceof LuaAnimation
                || value instanceof LuaSound
                || value instanceof LuaMusic;
    }
    
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
    private Set<Object> referrers;
    protected DisposableWrapper(T disposable) {
        this.disposable = disposable;
        
        if(!isDisposed()) {
            this.lastAccess = TimeUtils.millis();
        } else {
            this.lastAccess = -1;
        }
        
        registerInstance(this);
        this.referrers = new HashSet<>();
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
    
    public final T getReference(Object referrer) {
        if(!referrers.contains(referrer)) {
            referrers.add(referrer);
        }
        
        return getReference();
    }
    
    public void removeReference(Object referrer) {
        if(referrers.contains(referrer)) {
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
    
    protected Set<Object> getReferrers() {
        return referrers;
    }
    
    public abstract T newReference();
    public abstract boolean allowDispose();
}
