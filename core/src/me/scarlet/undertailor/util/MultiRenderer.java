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

package me.scarlet.undertailor.util;

import com.badlogic.gdx.Gdx;
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
    
    private Color clearColor;
    private SpriteBatch batch;
    private ShapeRenderer renderer;
    public MultiRenderer() {
        this.clearColor = Color.BLACK;
        this.batch = new SpriteBatch();
        this.renderer = new ShapeRenderer();
    }
    
    public void setProjectionMatrix(Matrix4 matrix) {
        this.setBatchProjectionMatrix(matrix);
        this.setShapeProjectionMatrix(matrix);
    }
    
    public void setTransformMatrix(Matrix4 matrix) {
        this.setBatchTransformMatrix(matrix);
        this.setShapeTransformMatrix(matrix);
    }
    
    public SpriteBatch getSpriteBatch() {
        return batch;
    }
    
    public ShapeRenderer getShapeRenderer() {
        return renderer;
    }
    
    public Color getClearColor() {
        return this.clearColor;
    }
    
    public void setClearColor(Color color) {
        this.clearColor = (color == null ? Color.BLACK : color);
    }
    
    public void clear() {
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, 1.0F);
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
            //batch.enableBlending();
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
    
    public void setBatchColor(Color color, float alpha) {
        color.a = alpha;
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

    public void draw(TextureRegion region, float x, float y) {
        draw(region, x, y, 0, 0);
    }

    public void draw(TextureRegion region, float x, float y, float originX, float originY) {
        draw(region, x, y, originX, originY, region.getRegionWidth(), region.getRegionHeight());
    }

    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height) {
        draw(region, x, y, originX, originY, width, height, 1F);
    }

    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scale) {
        draw(region, x, y, originX, originY, width, height, scale, scale);
    }

    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY) {
        draw(region, x, y, originX, originY, width, height, scaleX, scaleY, 0F);
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
            //Gdx.gl.glEnable(GL20.GL_BLEND);
            //Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            renderer.setAutoShapeType(true);
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
    
    public void setShapeColor(Color color, float alpha) {
        color.a = alpha;
        renderer.setColor(color);
    }
    
    public void drawLine(Vector2 begin, Vector2 end, float thickness) {
        this.startDrawingShape();
        if(renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }
        
        renderer.rectLine(begin, end, thickness);
    }
    
    public void drawArc(Vector2 pos, float radius, float start, float degrees) {
        this.startDrawingShape();
        if(renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }
        
        renderer.arc(pos.x, pos.y, radius, start, degrees);
    }
    
    
    public void drawArc(Vector2 pos, float radius, float start, float degrees, int segments) {
        this.startDrawingShape();
        if(renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }
        
        renderer.arc(pos.x, pos.y, radius, start, degrees, segments);
    }
    
    public void drawRectangle(Vector2 pos, float width, float height, float lineThickness) {
        this.startDrawingShape();
        if(renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }
        
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
        if(renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }
        
        renderer.rect(pos.x, pos.y, width, height);
    }
    
    public void drawCircle(float x, float y, float radius) {
        this.startDrawingShape();
        if(renderer.getCurrentType() != ShapeType.Line) {
            renderer.set(ShapeType.Line);
        }
        
        renderer.circle(x, y, radius);
    }
    
    public void drawFilledCircle(float x, float y, float radius) {
        this.startDrawingShape();
        if(renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }
        
        renderer.circle(x, y, radius);
    }
    
    public void drawTriangle(Vector2 vx1, Vector2 vx2, Vector2 vx3, float lineThickness) {
        this.startDrawingShape();
        if(renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }
        
        this.drawLine(vx1, vx2, lineThickness);
        this.drawLine(vx2, vx3, lineThickness);
        this.drawLine(vx3, vx1, lineThickness);
    }
    
    public void drawFilledTriangle(Vector2 vx1, Vector2 vx2, Vector2 vx3) {
        this.startDrawingShape();
        if(renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }
        
        renderer.triangle(vx1.x, vx1.y, vx2.x, vx2.y, vx3.x, vx3.y);
    }
}
