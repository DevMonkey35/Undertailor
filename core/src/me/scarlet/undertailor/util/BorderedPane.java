package me.scarlet.undertailor.util;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;

public class BorderedPane extends StackPane {
    
    private SimpleObjectProperty<Label> titleProperty;
    private SimpleObjectProperty<Node> contentProperty;
    
    private final ChangeListener<? super Node> objListener;
    
    public BorderedPane() {
        this(null);
    }
    
    public BorderedPane(String title) {
        this(title, null);
    }
    
    public BorderedPane(String title, String tooltip) {
        this.objListener = (value, old, neww) -> {
            if(old != neww) {
                this.getChildren().remove(old);
                this.getChildren().add(neww);
            }
        };
        
        this.titleProperty = new SimpleObjectProperty<>();
        this.contentProperty = new SimpleObjectProperty<>();
        
        this.titleProperty.addListener((value, old, neww) -> {
            if(old != neww && neww != null) {
                this.getChildren().remove(old);
                this.getChildren().add(neww);
                
                StackPane.setAlignment(neww, Pos.TOP_LEFT);
                neww.setStyle("-fx-background-color: #F4F4F4;"
                            + "-fx-padding: 0 0.33em 0 0.33em;");
                neww.setTranslateX(8);
                neww.setTranslateY(-10);
            }
        });
        
        this.contentProperty.addListener(objListener);
        
        if(title != null) {
            Label label = new Label(title);
            
            if(tooltip != null) {
                label.setTooltip(new Tooltip(tooltip));
            }
            
            this.titleProperty.set(new Label(title));
        }
        
        this.setStyle("-fx-border-width: 1px;"
                    + "-fx-border-color: #D4D4D4;");
    }
    
    public SimpleObjectProperty<Label> getTitleProperty() {
        return this.titleProperty;
    }
    
    public SimpleObjectProperty<Node> getContentProperty() {
        return this.contentProperty;
    }
}
