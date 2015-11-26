package me.scarlet.undertailor;

import me.scarlet.undertailor.wrappers.DisposableWrapper;

public class DisposerThread extends Thread {
    
    public static final long DISPOSABLE_LIFETIME = 6000;
    
    private boolean running;
    
    public DisposerThread() {
        this.running = true;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public void setRunning(boolean flag) {
        this.running = flag;
    }
    
    @Override
    public void run() {
        Undertailor.log("disposer", "disposer thread has been started");
        while(true) {
            if(running) {
                DisposableWrapper.getWrappers().forEach(wrapper -> {
                    if(!wrapper.isAlwaysAlive()) {
                        if(!wrapper.isDisposed() && wrapper.getLastAccessTime() > DISPOSABLE_LIFETIME) {
                            if(wrapper.dispose()) {
                                Undertailor.debug("disposer", "a reference was disposed for exceeding lifetime");
                            }
                        }
                    }
                });
            }
        }
    }
}
