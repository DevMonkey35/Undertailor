package me.scarlet.undertailor;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import me.scarlet.undertailor.util.Blocker;
import me.scarlet.undertailor.util.JFXUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Console {
    
    public class Output extends OutputStream {
        
        private Console console;
        private PrintStream original;
        public Output(PrintStream original, Console console) {
            this.original = original;
            this.console = console;
        }
        
        @Override
        public void write(int b) throws IOException {
            if(original != null) {
                original.print((char) b);
            }
            
            console.getThread().append(b);
        }
    }
    
    public static class ConsoleThread extends Thread {
        
        private boolean running;
        private StringBuilder buffer;
        private Console console;
        
        public ConsoleThread(Console console) {
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

    private Stage stage;
    private TextArea output;
    private ConsoleThread thread;
    
    public Console() {
        Blocker.block(() -> {
            this.stage = new Stage();
            this.output = new TextArea();
            AnchorPane pane = new AnchorPane();
            GridPane header = new GridPane();
            CheckBox wrap = new CheckBox("Wrap Text");
            Label consoleTitle = new Label("Undertailor Console");
            
            output.setEditable(false);
            consoleTitle.setFont(new javafx.scene.text.Font(16));
            javafx.scene.text.Font newFont = new javafx.scene.text.Font("Consolas", 12);
            if(newFont.getName().equals("Consolas")) {
                output.setFont(newFont);
            }
            
            wrap.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if(newValue.booleanValue()) {
                    output.setWrapText(true);
                } else {
                    output.setWrapText(false);
                }
            });
            
            if(!wrap.isSelected()) {
                wrap.fire();
            }
            
            GridPane.setColumnIndex(consoleTitle, 0);
            GridPane.setColumnIndex(wrap, 1);
            header.getColumnConstraints().add(new ColumnConstraints());
            header.getColumnConstraints().add(new ColumnConstraints(0, wrap.getPrefWidth(), wrap.getPrefWidth(), Priority.ALWAYS, HPos.RIGHT, false));
            
            header.getChildren().add(consoleTitle);
            header.getChildren().add(wrap);
            
            pane.getChildren().add(header);
            pane.getChildren().add(output);
            
            JFXUtil.setAnchorBounds(header, 15.0, null, 20.0, 20.0);
            JFXUtil.setAnchorBounds(output, 20.0);
            JFXUtil.setAnchorBounds(output, 50.0, null, null, null);
            JFXUtil.loadIcon(stage, "defaultIcon_small.png");
            JFXUtil.loadIcon(stage, "defaultIcon.png");
            
            stage.setScene(new Scene(pane, 600, 400));
            stage.setTitle("Undertailor Console");
        } , true);
        
        this.thread = new ConsoleThread(this);
        PrintStream original = System.out;
        System.setOut(new PrintStream(new Output(original, this)));
        thread.start();
    }
    
    public ConsoleThread getThread() {
        return thread;
    }
    
    void appendText(String text) {
        output.appendText(text);
    }
    
    public void show() {
        if(!stage.isShowing()) {
            Platform.runLater(() -> {
                stage.show();
            });
            
            Undertailor.instance.log(Undertailor.MANAGER_TAG, "showing console");
        }
    }
}
