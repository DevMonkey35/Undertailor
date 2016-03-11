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

/**
 * Manages the console window written in JavaFX, as an alternative to whatever
 * terminal the program is started with, if it wasn't headless (javaw).
 * 
 * <p><strong>Note:</strong> Doesn't do much right now except do a crappy job at
 * relaying what would've been printed out to a normal console window. The final
 * outcome of this class is intended to be a full console window organizing the
 * different kinds of messages and differentiating between system events that
 * come from the Java programming and the Lua printouts written by a script
 * executing the <code>print</code> global function.</p>
 */
public class Console {
    
    /**
     * The {@link OutputStream} used by {@link Console} instances. Instances of
     * this class is also intended to stand as replacements for the program
     * output stream.
     * 
     * <p>The stream keeps track of a separate {@link PrintStream} deemed as the
     * "original" stream that was set prior to an instance to this one, to which
     * the object will print to whenever it receives input to ensure the
     * original stream still receives messages.</p>
     */
    public class Output extends OutputStream {
        
        private Console console;
        private PrintStream original;
        
        /**
         * Instantiates a new {@link Output} object.
         * 
         * @param original the original {@link PrintStream} this Output will
         *            also relay to
         * @param console the {@link Console} object to relay to
         */
        public Output(PrintStream original, Console console) {
            this.original = original;
            this.console = console;
        }
        
        @Override
        public void write(int b) throws IOException {
            if(original != null) {
                original.print((char) b);
            }
            
            console.appendText(String.valueOf((char) b));
        }
    }

    private Stage stage;
    private TextArea output;
    
    /**
     * Instantiates a new {@link Console} object.
     */
    public Console() {
        // make sure we wait for this to finish before doing anything else,
        // so we get all the output
        Blocker.block(() -> {
            // build that noice jfx ui
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
                if(newValue) {
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
        
        // replace the print stream and use ours
        PrintStream original = System.out;
        System.setOut(new PrintStream(new Output(original, this)));
    }
    
    /**
     * Writes text to the console object.
     * 
     * <p>This method may safely be called from any thread; the write will
     * automagically be relayed to and performed on the JavaFX application
     * thread.</p>
     * 
     * @param text the text to write
     */
    void appendText(String text) {
        Platform.runLater(() -> output.appendText(text));
    }
    
    /**
     * Shows the console window.
     * 
     * <p>Results in a no-op if the window is already shown.</p>
     */
    public void show() {
        if(!stage.isShowing()) {
            Platform.runLater(() -> stage.show());
            
            Undertailor.instance.log(Undertailor.MANAGER_TAG, "showing console");
        }
    }
}
