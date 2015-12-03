package me.scarlet.undertailor;

import me.scarlet.undertailor.wrappers.DisposableWrapper;

/**
 * A thread that'll handle some memory stuffs to ensure stuff that isn't used
 * doesn't stay around and hog resources for too long.
 */
public class DisposerThread extends Thread {
    
    /** The current running DisposerThread instance. */
    public static DisposerThread currentInstance;
    
    private boolean running;
    private boolean working;
    
    /**
     * Instantiates a new {@link DisposerThread}.
     * 
     * <p>By default, the DisposerThread will immediately carry out its disposal
     * tasks as soon as {@link Thread#start()} is called.</p>
     */
    public DisposerThread() {
        this.running = false;
        this.working = true;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>Attempting to start another {@link DisposerThread} while one is
     * already running will result in an IllegalStateException.</p>
     */
    @Override
    public synchronized void start() {
        if(currentInstance != null) {
            throw new IllegalStateException("Cannot coexist with another running DisposerThread");
        }
        
        this.running = true;
        currentInstance = this;
        super.start();
    }
    
    /**
     * Returns whether or not the {@link DisposerThread} is actually working, as
     * in, actively tracking and disposing objects.
     * 
     * @see #setWorking(boolean)
     */
    public boolean isWorking() {
        return working;
    }
    
    /**
     * Sets whether or not the {@link DisposerThread} is actually working.
     * 
     * <p>Setting this to false does not actually kill the thread; instead the
     * thread simply skips performing disposal tasks. To end the thread
     * properly, use the {@link #kill()} method.</p>
     * 
     * @param flag whether or not the DisposerThread should work
     *            
     * @see #isWorking()
     * @see #kill()
     */
    public void setWorking(boolean flag) {
        this.working = flag;
    }
    
    /**
     * Kills this {@link DisposerThread}.
     * 
     * <p>A killed DisposerThread will no longer be running; using the
     * {@link #setWorking(boolean)} method will not allow this DisposerThread to
     * continue carrying out disposal tasks. References to this DisposerThread
     * object should be trashed upon calling this method.</p>
     */
    public void kill() {
        if(this.running) {
            this.running = false;
            currentInstance = null;
        }
    }
    
    /**
     * Disposer thread's execution.
     */
    @Override
    public void run() {
        Undertailor.instance.log("disposer", "disposer thread has been started");
        while(running) {
            if(working) {
                DisposableWrapper.getWrappers().forEach(wrapper -> {
                    if(!wrapper.isAlwaysAlive()) {
                        if(!wrapper.isDisposed() && wrapper.getLastAccessTime() > wrapper.getMaximumLifetime()) {
                            if(wrapper.dispose()) {
                                Undertailor.instance.debug("disposer", "a reference was disposed for exceeding lifetime");
                            }
                        }
                    }
                });
            }
        }
    }
}
