package me.scarlet.undertailor;

import javafx.beans.value.ObservableValue;
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

public class ErrorWindow extends Stage {

    static String toString(Throwable thrown) {
        StringWriter writer = new StringWriter();
        thrown.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

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
            errr.appendText(toString(thrown));
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
        GridPane.setColumnIndex(wrap, 1);
        GridPane.setColumnIndex(confirm, 2);
        GridPane.setHalignment(confirm, HPos.RIGHT);

        confirm.setOnMouseReleased(event -> this.close());

        wrap.selectedProperty().addListener(
            (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue) {
                    errr.setWrapText(true);
                } else {
                    errr.setWrapText(false);
                }
            });

        if (!wrap.isSelected()) {
            wrap.fire();
        }

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
