package me.scarlet.undertailor.util;

import static com.google.common.base.Preconditions.checkArgument;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class RenderUtil {
    
    private RenderUtil() {}
    
    public static final class Rectangle {
        
        public static class Builder {
            
            private Rectangle rect;
            public Builder() {
                rect = new Rectangle();
            }
            
            public Builder lineColor(Color color) {
                rect.lineColor = color;
                return this;
            }
            
            public Builder fillColor(Color color) {
                rect.fillColor = color;
                return this;
            }
            
            public Builder posX(float x) {
                rect.x = x;
                return this;
            }
            
            public Builder posY(float y) {
                rect.y = y;
                return this;
            }
            
            public Builder width(float width) {
                rect.width = width;
                return this;
            }
            
            public Builder height(float height) {
                rect.height = height;
                return this;
            }
            
            public Builder lineThickness(float lineThickness) {
                checkArgument(lineThickness >= 0, "thickness cannot be negative");
                rect.lineThickness = lineThickness;
                return this;
            }
            
            public Rectangle build() {
                return rect;
            }
        }
        
        public static Builder builder() {
            return new Builder();
        }
        
        private Color lineColor, fillColor;
        private float x, y, width, height, lineThickness;
        private Rectangle() {
            this.lineThickness = 1;
            this.lineColor = Color.WHITE;
            this.fillColor = Color.BLACK;
            this.x = 0; this.y = 0; this.width = 0; this.height = 0;
        }
    }
    
    public static void drawRectangle(Rectangle rect, float alpha) {
        ShapeRenderer renderer = new ShapeRenderer();
        drawRectangle(renderer, rect, alpha);
        renderer.dispose();
    }
    
    public static void drawRectangle(ShapeRenderer renderer, Rectangle rect, float alpha) {
        renderer.setAutoShapeType(true);
        renderer.begin();
        renderer.set(ShapeType.Filled);
        if(rect.lineColor != null && rect.lineThickness > 0) {
            Color color = new Color(rect.lineColor);
            color.a = color.a * alpha;
            renderer.setColor(color);
            renderer.rect(rect.x, rect.y, rect.width, rect.height);
            
            // renderer.line(rect.x - 1, rect.y - 1, rect.x + rect.lineThickness - 1, rect.y + rect.lineThickness - 1);
        }
        
        if(rect.fillColor != null) {
            Color color = new Color(rect.fillColor);
            color.a = color.a * alpha;
            renderer.setColor(color);
            renderer.rect(rect.x + rect.lineThickness, rect.y + rect.lineThickness, rect.width - rect.lineThickness * 2, rect.height - rect.lineThickness * 2);
        }
        
        renderer.end();
    }
}
