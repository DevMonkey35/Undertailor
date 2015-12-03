package me.scarlet.undertailor.util;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

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
}
