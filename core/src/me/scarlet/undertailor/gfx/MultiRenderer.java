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

package me.scarlet.undertailor.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;

/**
 * Implementation of a global renderer to be used by the
 * entire program.
 */
public class MultiRenderer {

    private Color clearColor;
    private SpriteBatch batch;
    private ShapeRenderer renderer;

    public MultiRenderer() {
        this.batch = new SpriteBatch();
        this.renderer = new ShapeRenderer();
    }

    // ---------------- object-global methods ----------------

    /**
     * Sets the projection matrices for the underlying
     * {@link SpriteBatch} and {@link ShapeRenderer}s.
     * 
     * @param matrix the projection matrix to set
     */
    public void setProjectionMatrix(Matrix4 matrix) {
        this.setBatchProjectionMatrix(matrix);
        this.setShapeProjectionMatrix(matrix);
    }

    /**
     * Sets the transform matrices for the underlying
     * {@link SpriteBatch} and {@link ShapeRenderer}.
     * 
     * @param matrix the transform matrix to set
     */
    public void setTransformMatrix(Matrix4 matrix) {
        this.setBatchTransformMatrix(matrix);
        this.setShapeTransformMatrix(matrix);
    }

    /**
     * Returns the underlying {@link SpriteBatch} used by
     * this {@link MultiRenderer}.
     * 
     * @return a SpriteBatch
     */
    public SpriteBatch getSpriteBatch() {
        return batch;
    }

    /**
     * Returns the underlying {@link ShapeRenderer} used by
     * this {@link MultiRenderer}.
     * 
     * @return a ShapeRenderer
     */
    public ShapeRenderer getShapeRenderer() {
        return renderer;
    }

    /**
     * Returns the currently used {@link Color} for clearing
     * the screen with every frame.
     * 
     * <p>Pretty much the global background color.</p>
     * 
     * @return a Color used to clear the screen every frame
     */
    public Color getClearColor() {
        return this.clearColor;
    }

    /**
     * Sets the {@link Color} used to clear the screen with
     * every frame.
     * 
     * @param color the new Color to clear with
     */
    public void setClearColor(Color color) {
        this.clearColor = (color == null ? Color.BLACK : color);
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, 1.0F);
    }

    /**
     * Clears the screen.
     */
    public void clear() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /**
     * Ensures that the current drawings are flushed.
     */
    public void flush() {
        if (batch.isDrawing()) {
            batch.end();
        }

        if (renderer.isDrawing()) {
            renderer.end();
        }

        this.resetColors();
    }

    /**
     * Resets the colors to their default.
     */
    public void resetColors() {
        if(!this.getClearColor().equals(Color.BLACK)) {
            this.setClearColor(Color.BLACK);
        }

        if(!this.getBatchColor().equals(Color.WHITE)) {
            this.setBatchColor(Color.WHITE);
        }

        if(!this.getShapeColor().equals(Color.WHITE)) {
            this.setShapeColor(Color.WHITE);
        }
    }

    // ---------------- spritebatch methods ----------------

    /**
     * Internal method.
     * 
     * <p>Makes sure that the underlying
     * {@link ShapeRenderer} is stopped and that the
     * {@link SpriteBatch} is prepared to take sprite
     * drawings.</p>
     */
    private void startDrawingSprite() {
        if (renderer.isDrawing()) {
            renderer.end();
        }

        if (!batch.isDrawing()) {
            //batch.enableBlending();
            batch.begin();
        }
    }

    /**
     * Returns the projection matrix of the underlying
     * {@link SpriteBatch}.
     * 
     * @return the SpriteBatch's projection matrix
     */
    public Matrix4 getBatchProjectionMatrix() {
        return batch.getProjectionMatrix();
    }

    /**
     * Sets the projection matrix of the underlying
     * {@link SpriteBatch}.
     * 
     * @param matrix the new projection matrix for the
     *        SpriteBatch
     */
    public void setBatchProjectionMatrix(Matrix4 matrix) {
        batch.setProjectionMatrix(matrix);
    }

    /**
     * Returns the transform matrix of the underlying
     * {@link SpriteBatch}.
     * 
     * @return the SpriteBatch's transform matrix
     */
    public Matrix4 getBatchTransformMatrix() {
        return batch.getTransformMatrix();
    }

    /**
     * Sets the transform matrix of the underlying
     * {@link SpriteBatch}.
     * 
     * @param matrix the new transform matrix for the
     *        SpriteBatch
     */
    public void setBatchTransformMatrix(Matrix4 matrix) {
        batch.setTransformMatrix(matrix);
    }

    /**
     * Returns whether or not the underlying
     * {@link SpriteBatch} has blending enabled.
     * 
     * @return true if the SpriteBatch has blending
     */
    public boolean isBatchBlending() {
        return batch.isBlendingEnabled();
    }

    /**
     * Sets whether or not the underlying
     * {@link SpriteBatch} uses blending.
     * 
     * <p>Changes to this value will flush current
     * sprites.</p>
     * 
     * @param flag new state of blending
     */
    public void setBatchBlending(boolean flag) {
        if (batch.isBlendingEnabled() == flag) {
            return;
        }

        if (flag) {
            batch.enableBlending();
        } else {
            batch.disableBlending();
        }
    }

    /**
     * Returns the {@link Color} used to draw with by the
     * underlying {@link SpriteBatch}.
     * 
     * @return the draw Color of the SpriteBatch
     */
    public Color getBatchColor() {
        return batch.getColor();
    }

    /**
     * Sets the {@link Color} used to draw with by the
     * underlying {@link SpriteBatch}.
     * 
     * @param color the color to use
     */
    public void setBatchColor(Color color) {
        batch.setColor(color);
    }

    /**
     * Sets the {@link Color} used to draw with by the
     * underlying {@link SpriteBatch}.
     * 
     * @param color the color to use
     * @param alpha the alpha value of the color
     */
    public void setBatchColor(Color color, float alpha) {
        if(!batch.getColor().equals(color)) {
            color.a = alpha;
            batch.setColor(color);
        }
    }

    /**
     * Returns the {@link ShaderProgram} used by the
     * underlying {@link SpriteBatch}.
     * 
     * @return the SpriteBatch's current ShaderProgram
     */
    public ShaderProgram getBatchShader() {
        return batch.getShader();
    }

    /**
     * Sets the {@link ShaderProgram} used by the underlying
     * {@link SpriteBatch}.
     * 
     * @param shader the new ShaderProgram to draw with, or
     *        null to clear
     */
    public void setBatchShader(ShaderProgram shader) {
        batch.setShader(shader);
    }

    // ---------------- batch draw methods ----------------

    /**
     * Draws a texture at the given position.
     * 
     * @param texture the texture to draw
     * @param x the x position
     * @param y the y position
     */
    public void draw(Texture texture, float x, float y) {
        this.startDrawingSprite();
        batch.draw(texture, x, y);
    }

    /**
     * @see SpriteBatch#draw(TextureRegion, float, float,
     *      float, float, float, float, float, float, float)
     */
    public void draw(TextureRegion region, float x, float y) {
        draw(region, x, y, 1);
    }

    /**
     * @see SpriteBatch#draw(TextureRegion, float, float,
     *      float, float, float, float, float, float, float)
     */
    public void draw(TextureRegion region, float x, float y, float scale) {
        draw(region, x, y, scale, scale);
    }

    /**
     * @see SpriteBatch#draw(TextureRegion, float, float,
     *      float, float, float, float, float, float, float)
     */
    public void draw(TextureRegion region, float x, float y, float scaleX, float scaleY) {
        draw(region, x, y, scaleX, scaleY, 0, 0);
    }

    /**
     * @see SpriteBatch#draw(TextureRegion, float, float,
     *      float, float, float, float, float, float, float)
     */
    public void draw(TextureRegion region, float x, float y, float scaleX, float scaleY,
        float originX, float originY) {
        draw(region, x, y, scaleX, scaleY, originX, originY, 0F);
    }

    /**
     * Draws a {@link TextureRegion} using the underlying
     * {@link SpriteBatch}.
     * 
     * <p>By default, libGDX places the anchor point at the
     * bottom left of the sprite.</p>
     * 
     * @param x the x position of the sprite's anchor point
     * @param y the y position of the sprite's anchor point
     * @param scaleX the scaling of the sprite horizontally
     * @param scaleY the scaling of the sprite vertically
     * @param originX the offset of the anchor point, from
     *        the bottom-left of the sprite
     * @param originY the offset of the anchor point, from
     *        the bottom-left of the sprite
     * @param rotation the rotation of the sprite, anchored
     *        at the anchor point defined by the origin
     *        offset values
     */
    public void draw(TextureRegion region, float x, float y, float scaleX, float scaleY,
        float originX, float originY, float rotation) {
        this.startDrawingSprite();
        float mOriginX = originX * scaleX;
        float mOriginY = originY * scaleY;
        batch.draw(region, x - mOriginX, y - mOriginY, mOriginX, mOriginY,
            region.getRegionWidth() * scaleX, region.getRegionHeight() * scaleY, 1F, 1F, rotation);
    }

    // ---------------- renderer methods ----------------

    /**
     * Internal method.
     * 
     * <p>Makes sure that the underlying {@link SpriteBatch}
     * is stopped and that the {@link ShapeRenderer} is
     * prepared to take shape drawings.</p>
     */
    private void startDrawingShape() {
        if (batch.isDrawing()) {
            batch.end();
        }

        if (!renderer.isDrawing()) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            renderer.setAutoShapeType(true);
            renderer.begin(ShapeType.Filled);
        }
    }

    /**
     * Returns the projection matrix of the underlying
     * {@link ShapeRenderer}.
     * 
     * @return the ShapeRenderer's projection matrix
     */
    public Matrix4 getShapeProjectionMatrix() {
        return renderer.getProjectionMatrix();
    }

    /**
     * Sets the projection matrix of the underlying
     * {@link ShapeRenderer}.
     * 
     * @param matrix the new projection matrix
     */
    public void setShapeProjectionMatrix(Matrix4 matrix) {
        renderer.setProjectionMatrix(matrix);
    }

    /**
     * Returns the transform matrix of the underlying
     * {@link ShapeRenderer}.
     * 
     * @return the ShapeRenderer's transform matrix
     */
    public Matrix4 getShapeTransformMatrix() {
        return renderer.getTransformMatrix();
    }

    /**
     * Sets the transform matrix of the underlying
     * {@link ShapeRenderer}.
     * 
     * @param matrix the new transform matrix
     */
    public void setShapeTransformMatrix(Matrix4 matrix) {
        renderer.setTransformMatrix(matrix);
    }

    /**
     * Returns the {@link Color} used to draw with by the
     * underlying {@link ShapeRenderer}.
     * 
     * @return the draw color of the ShapeRenderer
     */
    public Color getShapeColor() {
        return renderer.getColor();
    }

    /**
     * Sets the {@link Color} used to draw with by the
     * underlying {@link ShapeRenderer}.
     * 
     * @param color the new draw color of the ShapeRenderer
     */
    public void setShapeColor(Color color) {
        if(!renderer.getColor().equals(color)) {
            renderer.setColor(color);
        }
    }

    /**
     * Sets the {@link Color} used to draw with by the
     * underlying {@link ShapeRenderer}.
     * 
     * @param color the new draw color of the ShapeRenderer
     * @param alpha the alpha value of the color
     */
    public void setShapeColor(Color color, float alpha) {
        color.a = alpha;
        renderer.setColor(color);
    }

    // ---------------- shape draw methods ----------------

    /**
     * Draws a line between the two points provided with a
     * given thickness.
     * 
     * @param x1 the x-coordinate of the start point of the
     *        line
     * @param y1 the y-coordinate of the start point of the
     *        line
     * @param x2 the x-coordinate of the end point of the
     *        line
     * @param y2 the y-coordinate of the end point of the
     *        line
     * @param thickness the thickness of the line
     */
    public void drawLine(float x1, float y1, float x2, float y2, float thickness) {
        this.startDrawingShape();
        if (renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }

        renderer.rectLine(x1, y1, x2, y2, thickness);
    }

    /**
     * Draws an arc centered at the given position with a
     * specified radius, start point and length.
     * 
     * @param x the x-coordinate of the centerpoint of the
     *        arc
     * @param y the y-coordinate of the centerpoint of the
     *        arc
     * @param radius the radius of the arc
     * @param start the start point of the arc, in degrees;
     *        0 being the top
     * @param degrees the length of the arc, in degrees
     */
    public void drawArc(float x, float y, float radius, float start, float degrees) {
        this.startDrawingShape();
        if (renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }

        renderer.arc(x, y, radius, start, degrees);
    }

    /**
     * Draws an arc centered at the given position with a
     * specified radius, start point, length, and
     * smoothness.
     * 
     * @param x the x-coordinate of the centerpoint of the
     *        arc
     * @param y the y-coordinate of the centerpoint of the
     *        arc
     * @param radius the radius of the arc
     * @param start the start point of the arc, in degrees;
     *        0 being the top
     * @param degrees the length of the arc, in degrees
     * @param segments the count of segments creating the
     *        arc; the "smoothness"
     */
    public void drawArc(float x, float y, float radius, float start, float degrees, int segments) {
        this.startDrawingShape();
        if (renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }

        renderer.arc(x, y, radius, start, degrees, segments);
    }

    /**
     * Draws a filled polygon.
     * 
     * @param points the vertices of the polygon
     */
    public void drawFilledPolygon(float... points) {
        if (points.length < 6) {
            return;
        }

        this.startDrawingShape();
        if (renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }

        this.renderer.polygon(points);
    }

    /**
     * Draws the outline of a polygon by drawing a line
     * between each of the provided points. The polygon will
     * be closed by drawing a line between the last point
     * and the first point.
     * 
     * @param lineThickness the thickness of the drawn line
     * @param points the vertices of the polygon
     */
    public void drawPolygon(float lineThickness, float... points) {
        this.drawPolygonOutline(lineThickness, true, points);
    }

    /**
     * Draws the outline of a polygon by drawing a line
     * between each of the provided points. The polygon will
     * be left open, leaving a missing edge connecting the
     * last and first points.
     * 
     * @param lineThickness the thickness of the drawn line
     * @param points the vertices of the polygon
     */
    public void drawOpenPolygon(float lineThickness, float... points) {
        this.drawPolygonOutline(lineThickness, false, points);
    }

    /**
     * Internal method.
     * 
     * <p>Implements drawing the outline of an open and
     * closed polygon outline.</p>
     * 
     * @see #drawPolygon(float, float...)
     * @see #drawOpenPolygon(float, float...)
     */
    private void drawPolygonOutline(float lineThickness, boolean close, float... points) {
        if (points.length < 3) {
            return; // Won't draw anything; does not have at least 3 edges.
        }

        if (points.length % 2 != 0) {
            throw new IllegalArgumentException("uneven point");
        }

        this.startDrawingShape();
        if (renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }

        for (int i = 2; i < points.length; i++) {
            if (i % 2 == 0) {
                this.drawLine(points[i - 2], points[i - 1], points[i], points[i + 1],
                    lineThickness);
            }
        }

        if (close)
            this.drawLine(points[points.length - 2], points[points.length - 1], points[0],
                points[1], lineThickness);
    }

    /**
     * Draws a rectangular outline anchored at the given
     * point with a specified width, height and edge
     * thickness.
     * 
     * <p>The anchor point denotes the bottom-left corner of
     * the rectangle.</p>
     * 
     * @param x the x-coordinate of the bottom-left corner
     *        of the rectangle
     * @param y the y-coordinate of the bottom-left corner
     *        of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param lineThickness the thickness of the rectangle's
     *        edges
     */
    public void drawRectangle(float x, float y, float width, float height, float lineThickness) {
        this.startDrawingShape();
        if (renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }

        this.drawPolygon(lineThickness, x, y, x + width, y, x + width, y + height, x, y + height);
        /*
         * this.drawLine(tL, tR, lineThickness);
         * this.drawLine(tR, bR, lineThickness);
         * this.drawLine(bR, pos, lineThickness);
         * this.drawLine(pos, tL, lineThickness);
         */
    }

    /**
     * Draws a filled rectangle anchored at the given point
     * with a specified width and height.
     * 
     * @param x the x-coordinate of the bottom-left corner
     *        of the rectangle
     * @param y the y-coordinate of the bottom-left corner
     *        of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */
    public void drawFilledRectangle(float x, float y, float width, float height) {
        this.startDrawingShape();
        if (renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }

        renderer.rect(x, y, width, height);
    }

    /**
     * Draws a circle outline centered at the given position
     * with a specified radius.
     * 
     * @param x the x-coordinate of the center of the circle
     * @param y the y-coordinate of the center of the circle
     * @param radius the radius of the circle
     */
    public void drawCircle(float x, float y, float radius) {
        this.startDrawingShape();
        if (renderer.getCurrentType() != ShapeType.Line) {
            renderer.set(ShapeType.Line);
        }

        renderer.circle(x, y, radius);
    }

    /**
     * Draws a filled circle centered at the given position
     * with a specified radius.
     * 
     * @param x the x-coordinate of the center of the circle
     * @param y the y-coordinate of the center of the circle
     * @param radius the radius of the circle
     */
    public void drawFilledCircle(float x, float y, float radius) {
        this.startDrawingShape();
        if (renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }

        renderer.circle(x, y, radius);
    }

    /**
     * Draws a triangular outline with edges at the given
     * points.
     * 
     * @param x1 the x-coordinate of the first point of the
     *        triangle
     * @param y1 the y-coordinate of the first point of the
     *        triangle
     * @param x2 the x-coordinate of the second point of the
     *        triangle
     * @param y2 the y-coordinate of the second point of the
     *        triangle
     * @param x3 the x-coordinate of the third point of the
     *        triangle
     * @param y3 the y-coordinate of the third point of the
     *        triangle
     * @param lineThickness the thickness of the triangle's
     *        edges
     */
    public void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3,
        float lineThickness) {
        this.startDrawingShape();
        if (renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }

        /*
         * this.drawLine(vx1, vx2, lineThickness);
         * this.drawLine(vx2, vx3, lineThickness);
         * this.drawLine(vx3, vx1, lineThickness);
         */
        this.drawPolygon(lineThickness, x1, y1, x2, y2, x3, y3);
    }

    /**
     * Draws a triangle.
     * 
     * @param x1 the x-coordinate of the first point of the
     *        triangle
     * @param y1 the y-coordinate of the first point of the
     *        triangle
     * @param x2 the x-coordinate of the second point of the
     *        triangle
     * @param y2 the y-coordinate of the second point of the
     *        triangle
     * @param x3 the x-coordinate of the third point of the
     *        triangle
     * @param y3 the y-coordinate of the third point of the
     *        triangle
     */
    public void drawFilledTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.startDrawingShape();
        if (renderer.getCurrentType() != ShapeType.Filled) {
            renderer.set(ShapeType.Filled);
        }

        renderer.triangle(x1, y1, x2, y2, x3, y3);
    }
}
