package me.scarlet.undertailor.wrappers;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.HashSet;
import java.util.Set;

public abstract class DisposableWrapper<T extends Disposable> {
    
    private static Set<DisposableWrapper<?>> instances;
    
    static {
        instances = new HashSet<>();
    }
    
    public static Set<DisposableWrapper<?>> getWrappers() {
        return instances;
    }
    
    private T disposable;
    private long lastAccess;
    private boolean alwaysAlive;
    protected DisposableWrapper(T disposable) {
        this.disposable = disposable;
        
        if(!isDisposed()) {
            this.lastAccess = TimeUtils.millis();
        } else {
            this.lastAccess = -1;
        }
        
        instances.add(this);
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
    
    public abstract T newReference();
    public abstract boolean allowDispose();
}
