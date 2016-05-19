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
package me.scarlet.undertailor.jfx;

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
