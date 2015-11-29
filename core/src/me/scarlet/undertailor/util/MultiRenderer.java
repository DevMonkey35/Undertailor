package me.scarlet.undertailor.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class MultiRenderer {
    
    private SpriteBatch batch;
    private ShapeRenderer renderer;
    public MultiRenderer() {
        this.batch = new SpriteBatch();
        this.renderer = new ShapeRenderer();
    }
    
    public void flush() {
        if(batch.isDrawing()) {
            batch.end();
        }
        
        if(renderer.isDrawing()) {
            renderer.end();
        }
    }
    
//     ### SpriteBatch methods
    
    private void startDrawingSprite() {
        if(renderer.isDrawing()) {
            renderer.end();
        }
        
        if(!batch.isDrawing()) {
            batch.begin();
        }
    }
    
    public Matrix4 getBatchProjectionMatrix() {
        return batch.getProjectionMatrix();
    }
    
    public void setBatchProjectionMatrix(Matrix4 matrix) {
        batch.setProjectionMatrix(matrix);
    }
    
    public Matrix4 getBatchTransformMatrix() {
        return batch.getTransformMatrix();
    }
    
    public void setBatchTransformMatrix(Matrix4 matrix) {
        batch.setTransformMatrix(matrix);
    }
    
    public boolean isBatchBlending() {
        return batch.isBlendingEnabled();
    }
    
    public void setBatchBlending(boolean flag) {
        if(batch.isBlendingEnabled() == flag) {
            return;
        }
        
        if(flag) {
            batch.enableBlending();
        } else {
            batch.disableBlending();
        }
    }
    
    public Color getBatchColor() {
        return batch.getColor();
    }
    
    public void setBatchColor(Color color) {
        batch.setColor(color);
    }
    
    public ShaderProgram getBatchShader() {
        return batch.getShader();
    }
    
    public void setBatchShader(ShaderProgram shader) {
        batch.setShader(shader);
    }
    
    public void draw(Texture texture, float x, float y) {
        this.startDrawingSprite();
        batch.draw(texture, x, y);
    }
    
    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        this.startDrawingSprite();
        batch.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
    }
    
//     ### ShapeRenderer methods
    
    private void startDrawingShape() {
        if(batch.isDrawing()) {
            batch.end();
        }
        
        if(!renderer.isDrawing()) {
            renderer.begin(ShapeType.Filled);
        }
    }
    
    public Matrix4 getShapeProjectionMatrix() {
        return renderer.getProjectionMatrix();
    }
    
    public void setShapeProjectionMatrix(Matrix4 matrix) {
        renderer.setProjectionMatrix(matrix);
    }
    
    public Matrix4 getShapeTransformMatrix() {
        return renderer.getTransformMatrix();
    }
    
    public void setShapeTransformMatrix(Matrix4 matrix) {
        renderer.setTransformMatrix(matrix);
    }
    
    public Color getShapeColor() {
        return renderer.getColor();
    }
    
    public void setShapeColor(Color color) {
        renderer.setColor(color);
    }
    
    public void drawLine(Vector2 begin, Vector2 end, float thickness) {
        this.startDrawingShape();
        renderer.rectLine(begin, end, thickness);
    }
    
    public void drawRectangle(Vector2 pos, float width, float height, float lineThickness) {
        this.startDrawingShape();
        Vector2 bR = new Vector2(pos.x + width, pos.y);
        Vector2 tL = new Vector2(pos.x, pos.y + height);
        Vector2 tR = new Vector2(bR.x, tL.y);
        this.drawLine(tL, tR, lineThickness);
        this.drawLine(tR, bR, lineThickness);
        this.drawLine(bR, pos, lineThickness);
        this.drawLine(pos, tL, lineThickness);
    }
    
    public void drawFilledRectangle(Vector2 pos, float width, float height) {
        this.startDrawingShape();
        renderer.rect(pos.x, pos.y, width, height);
    }
    
    public void drawCircle(float x, float y, float radius) {
        this.startDrawingShape();
        renderer.circle(x, y, radius);
    }
}
