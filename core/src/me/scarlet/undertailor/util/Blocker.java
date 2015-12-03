package me.scarlet.undertailor.util;

import javafx.application.Platform;

public class Blocker {
    
    public static class BlockingThread extends Thread {
        
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
    
    public static class JavaFXBlockingThread extends BlockingThread {
        
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
    
    public void setBlocking(boolean flag) {
        this.isBlocking = flag;
    }
}
