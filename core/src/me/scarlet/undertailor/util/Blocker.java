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
        
        private final Blocker blocker;
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
            Platform.runLater(super::run);
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
        if (jfx) {
            blockerThread = new JavaFXBlockingThread(blocker, runnable);
        } else {
            blockerThread = new BlockingThread(blocker, runnable);
        }

        blockerThread.start();

        while (blocker.isBlocking()) {
            try {
                blocker.wait();
            } catch (InterruptedException ignored) {
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
