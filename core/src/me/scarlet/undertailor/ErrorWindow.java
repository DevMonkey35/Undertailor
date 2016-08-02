/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without
 * limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package me.scarlet.undertailor;

import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import me.scarlet.undertailor.jfx.JFXUtil;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * The error window that pops up when something goes wrong
 * in the game.
 */
public class ErrorWindow extends Stage {

    public ErrorWindow(Throwable thrown) {
        AnchorPane parent = new AnchorPane();
        VBox vbox = new VBox(10);
        Label titleLabel = new Label("Whoops!");
        Separator sep = new Separator(Orientation.HORIZONTAL);
        Label info = new Label(
            "Uh-oh! Looks like Undertailor found a problem it didn't know how to resolve...");
        TextArea errr = new TextArea();
        if (thrown == null) {
            errr.appendText("No Java stacktrace given.\n");
        } else {
            StringWriter writer = new StringWriter();
            thrown.printStackTrace(new PrintWriter(writer));
            errr.appendText(writer.toString());
        }

        Label last = new Label(
            "If this is from game scripts, you might wanna send this to the scripts' developer(s) and help them squash it! "
                + "But, if you think this is Undertailor's fault, poke the developer of Undertailor about it!");
        GridPane footer = new GridPane();
        Button confirm = new Button("Oh, okay :c.");
        CheckBox wrap = new CheckBox("Wrap Text");

        JFXUtil.setAnchorBounds(vbox, 50.0, 10.0, 25.0, 25.0);

        titleLabel.setFont(new javafx.scene.text.Font(20));
        JFXUtil.setAnchorBounds(titleLabel, 10.0, null, 15.0, null);
        JFXUtil.setAnchorBounds(last, null, 15.0, null, null);

        errr.positionCaret(0);
        errr.setEditable(false);
        VBox.setVgrow(errr, Priority.ALWAYS);
        info.setWrapText(true);
        last.setWrapText(true);
        javafx.scene.text.Font newFont = new javafx.scene.text.Font("Consolas", 12);
        if (newFont.getName().equals("Consolas")) {
            errr.setFont(newFont);
        }

        footer.getChildren().add(wrap);
        footer.getChildren().add(confirm);
        ColumnConstraints defaultt = new ColumnConstraints();
        defaultt.setFillWidth(true);
        footer.getColumnConstraints().add(defaultt);
        footer.getColumnConstraints().add(new ColumnConstraints(0, wrap.getPrefWidth(),
            wrap.getPrefWidth(), Priority.ALWAYS, HPos.RIGHT, false));
        footer.getColumnConstraints().add(new ColumnConstraints(0, confirm.getPrefWidth(),
            confirm.getPrefWidth(), Priority.ALWAYS, HPos.RIGHT, false));
        GridPane.setColumnIndex(wrap, 0);
        GridPane.setColumnIndex(confirm, 2);
        GridPane.setHalignment(confirm, HPos.RIGHT);

        confirm.setOnMouseReleased(event -> this.close());
        errr.wrapTextProperty().bind(wrap.selectedProperty());
        wrap.setSelected(false); // by default, don't wrap

        vbox.getChildren().add(sep);
        vbox.getChildren().add(info);
        vbox.getChildren().add(errr);
        vbox.getChildren().add(last);
        vbox.getChildren().add(footer);
        parent.getChildren().add(titleLabel);
        parent.getChildren().add(vbox);

        JFXUtil.loadIcon(this, "errorIcon_small.png");
        JFXUtil.loadIcon(this, "errorIcon.png");
        InputStream imageStream = Undertailor.class.getResourceAsStream("/assets/errorIcon.png");
        if (imageStream != null) {
            Image icon = new Image(imageStream);
            this.getIcons().add(icon);
            titleLabel.setGraphic(new ImageView(icon));
        }

        this.setResizable(true);
        this.setMinHeight(350);
        this.setMinWidth(600);
        this.setTitle("Error");
        this.centerOnScreen();

        Scene scene = new Scene(parent, this.getMinWidth(), this.getMinHeight());
        this.setScene(scene);
    }
}
