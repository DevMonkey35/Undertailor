package me.scarlet.undertailor.util;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import me.scarlet.undertailor.Undertailor;

import java.io.InputStream;

public class JFXUtil {
    public static void setAnchorBounds(Node node, Double top, Double bottom, Double left, Double right) {
        if(top != null) AnchorPane.setTopAnchor(node, top);
        if(bottom != null) AnchorPane.setBottomAnchor(node, bottom);
        if(left != null) AnchorPane.setLeftAnchor(node, left);
        if(right != null) AnchorPane.setRightAnchor(node, right);
    }
    
    public static void setAnchorBounds(Node node, Double all) {
        setAnchorBounds(node, all, all, all, all);
    }
    
    public static Image loadIcon(Stage stage, String imagePath) {
        InputStream imageStream = Undertailor.class.getResourceAsStream("/assets/" + imagePath);
        if(imageStream != null) {
            Image icon = new Image(imageStream);
            stage.getIcons().add(icon);
            return icon;
        }
        
        return null;
    }
}
