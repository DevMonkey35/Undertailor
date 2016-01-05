package me.scarlet.undertailor.util;

import javafx.application.Platform;

/**
 * Low-profile setup for waiting on another thread's task to finish before
 * continuing execution.
 */
public class Blocker {
    
    /**
     * Normal blocking thread that will run its task on itself before notifying
     * the thread holding the target blocker to continue.
     */
    static class BlockingThread extends Thread {
        
        private Blocker blocker;
        private Runnable runnable;
        
        public BlockingThread(Blocker blocker, Runnable runnable) {
            this.blocker = blocker;
            this.runnable = runnable;
        }
        
        @Override
        public void run() {
            runnable.run();
            synchronized(blocker) {
                blocker.setBlocking(false);
                blocker.notify();
            }
        }
    }
    
    /**
     * An extended implementation of a {@link BlockingThread} to ensure the
     * given task is ran on the JavaFX application thread.
     */
    static class JavaFXBlockingThread extends BlockingThread {
        
        public JavaFXBlockingThread(Blocker blocker, Runnable runnable) {
            super(blocker, runnable);
        }
        
        @Override
        public void run() {
            Platform.runLater(() -> {
                super.run();
            });
        }
    }
    
    /**
     * Blocks the thread that calls this method, and runs the given
     * {@link Runnable} on a separate thread. The calling thread will be
     * unblocked when the Runnable on the other thread finishes.
     * 
     * @param runnable the runnable to run concurrently
     * @param jfx whether or not to execute the runnable on the JavaFX
     *            application thread
     */
    public static void block(Runnable runnable, boolean jfx) {
        Blocker blocker = new Blocker();
        
        BlockingThread blockerThread;
        if(jfx) {
            blockerThread = new JavaFXBlockingThread(blocker, runnable);
        } else {
            blockerThread = new BlockingThread(blocker, runnable);
        }
        
        blockerThread.start();
        
        synchronized(blocker) {
            while(blocker.isBlocking()) {
                try {
                    blocker.wait();
                } catch(InterruptedException e) {}
            }
        }
    }
    
    private boolean isBlocking;
    
    public Blocker() {
        this.isBlocking = true;
    }
    
    public boolean isBlocking() {
        return this.isBlocking;
    }
    
    void setBlocking(boolean flag) {
        this.isBlocking = flag;
    }
}
