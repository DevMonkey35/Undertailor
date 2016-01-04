package me.scarlet.undertailor;

import javafx.scene.control.TextArea;

public class ConsoleThread extends Thread {
    
    private boolean running;
    private StringBuilder buffer;
    private TextArea console;
    public ConsoleThread(TextArea console) {
        this.buffer = new StringBuilder();
        this.console = console;
        this.running = true;
    }
    
    public synchronized void append(int b) {
        buffer.append((char) b);
    }
    
    @Override
    public void run() {
        while(running) {
            try {
                Thread.sleep(200); // update console every 1/5th of a second
                synchronized(buffer) {
                    console.appendText(buffer.toString().trim());
                    buffer.setLength(0);
                }
            } catch(InterruptedException e) {}
        }
    }
    
    public void kill() {
        this.running = false;
    }
}
