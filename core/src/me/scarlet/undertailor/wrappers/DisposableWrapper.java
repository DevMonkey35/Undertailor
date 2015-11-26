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
    
    protected T disposable;
    protected long lastAccess;
    protected boolean alwaysAlive;
    protected DisposableWrapper(T disposable) {
        this.disposable = disposable;
        
        if(!isDisposed()) {
            this.lastAccess = TimeUtils.millis();
        } else {
            this.lastAccess = -1;
        }
        
        instances.add(this);
    }
    
    public T getReference() {
        if(this.isDisposed()) {
            this.newReference();
        }
        
        this.lastAccess = TimeUtils.millis();
        return disposable;
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
    
    public void setAlwaysAlive(boolean flag) {
        this.alwaysAlive = flag;
        if(this.alwaysAlive) {
            getReference();
        }
    }
    
    public boolean isDisposed() {
        return disposable == null;
    }
    
    public boolean dispose() {
        disposable.dispose();
        this.lastAccess = -1;
        disposable = null;
        
        return true;
    }
    
    public abstract void newReference();
}
