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

package me.scarlet.undertailor.engine.overworld.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

import me.scarlet.undertailor.engine.overworld.OverworldController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A layer defining a set of shapes and points.
 */
public class ObjectLayer {

    /**
     * Databag class holding information about a defined
     * {@link Shape}.
     */ // we use this instead of storing the shape itself
    // so we don't have to worry about disposing it layer
    public static class ShapeData {

        int id;
        String name;
        Shape.Type type; // null for rect, chain for polyline
        float[] shapeVertices;
        float shapeHeight;
        float shapeWidth;

        Vector2 position;

        ShapeData() {
            this.position = new Vector2();
        }

        /**
         * Returns the type of the {@link Shape} defined by
         * this {@link ShapeData}.
         * 
         * <p>This class reuses an already-existing class of
         * the same purpose, with different values. The
         * following enumeration values resolve to the given
         * shapes.</p>
         * 
         * <pre>
         * null : Rectangle
         * {@link com.badlogic.gdx.physics.box2d.Shape.Type#Circle} : Circle
         * {@link com.badlogic.gdx.physics.box2d.Shape.Type#Polygon} : Polygon
         * {@link com.badlogic.gdx.physics.box2d.Shape.Type#Chain} : Chain/Polyline
         * </pre>
         * 
         * @return the type of the Shape defined by this
         *         ShapeData
         */
        public Shape.Type getType() {
            return this.type;
        }

        /**
         * Returns the vertices that create the
         * {@link Shape} defined by this {@link ShapeData}.
         * 
         * <p>Vertices are only generated for imperfect
         * circles, rectangles, polygons and
         * chain/polylines. For perfect circles, see
         * {@link #getRadius()}.</p>
         * 
         * @return the vertices that make up the underlying
         *         Shape, or null if this is a perfect
         *         circle
         */
        public float[] getVertices() {
            return this.shapeVertices;
        }

        /**
         * Returns the radius of the {@link Shape} defined
         * by this {@link ShapeData}.
         * 
         * <p>Only returns a value other than 0 if the
         * underlying Shape is a perfect circle.</p>
         * 
         * @return the radius of the underlying Shape
         */
        public float getRadius() {
            if(this.type == Shape.Type.Circle && this.shapeHeight == this.shapeWidth) {
                return this.shapeHeight / 2.0F;
            }

            return 0F;
        }

        /**
         * Generates a Box2D {@link Shape} object from the
         * data held by this {@link ShapeData}.
         * 
         * <p>The returned Shape should be cached and
         * disposed of, as this ShapeData will not track its
         * Shape instances.</p>
         * 
         * @return a Shape
         */
        public Shape generateShape() {
            Shape shape = null;

            if (type == Shape.Type.Circle && shapeHeight == shapeWidth) { // perfect circle?
                shape = new CircleShape();
                ((CircleShape) shape).setRadius(shapeHeight / 2.0F);
            }

            if (type == Shape.Type.Chain) {
                shape = new ChainShape();
                ((ChainShape) shape).createChain(shapeVertices);
            }

            if (shape == null && this.shapeVertices != null) { // type polygon happens here too
                shape = new PolygonShape();
                ((PolygonShape) shape).set(this.shapeVertices);
            }

            return shape;
        }

        /**
         * Returns the position of this {@link Shape}.
         * 
         * @return the position of this shape
         */
        public Vector2 getPosition() {
            return this.position;
        }

        /**
         * Internal method.
         * 
         * <p>Generates the vertices for the {@link Shape}s
         * created by {@link #generateShape()}.</p>
         */
        void generateVertices() {
            if (this.shapeVertices == null) {
                float shapeWidth = this.shapeWidth * OverworldController.PIXELS_TO_METERS;
                float shapeHeight = this.shapeHeight * OverworldController.PIXELS_TO_METERS;

                if (type == null) { // rect
                    // rectangle origin in Tiled is on top left,
                    // so need negative height
                    float negHeight = shapeHeight * -1;
                    this.shapeVertices = new float[] {0, 0, // tl
                        0, negHeight, // bl
                        shapeWidth, negHeight, // br
                        shapeWidth, 0 // tr
                    };
                } else if (type == Shape.Type.Circle && shapeHeight != shapeWidth) {
                    float halfHeight = shapeHeight / 2.0F;
                    float halfWidth = shapeWidth / 2.0F;
                    float eighthHeight = halfHeight / 4.0F;
                    float eighthWidth = halfWidth / 4.0F;
                    this.shapeVertices = new float[] {0, halfHeight, // top
                        halfWidth - eighthWidth, halfHeight - eighthHeight, // topright
                        halfWidth, 0, // right
                        halfWidth - eighthWidth, (halfHeight - eighthHeight) * -1, // bottomright
                        0, halfHeight * -1, // bottom
                        (halfWidth - eighthWidth) * -1, (halfHeight - eighthHeight) * -1, // bottomleft
                        halfWidth * -1, 0, // left
                        (halfWidth - eighthWidth) * -1, halfHeight - eighthHeight // topleft
                    };
                }
            }
        }
    }

    // ---------------- object ----------------

    String name;
    Set<ShapeData> shapes;
    Map<String, Vector2> points;

    ObjectLayer() {
        this.shapes = new HashSet<>();
        this.points = new HashMap<>();
    }

    /**
     * Returns the name of this {@link ObjectLayer}.
     * 
     * @return the name of this ObjectLayer
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns a {@link Set} of all {@link ShapeData}
     * instances held by this {@link ObjectLayer}.
     * 
     * @return all this ObjectLayer's ShapeData
     */
    public Set<ShapeData> getShapes() {
        return this.shapes;
    }

    /**
     * Returns the {@link ShapeData} under the given name.
     * 
     * @param name the name of the shape
     * 
     * @return the ShapeData under the given name, or null
     *         if not found
     */
    public ShapeData getShape(String name) {
        for (ShapeData data : shapes) {
            if (data.name.equals(name)) {
                return data;
            }
        }

        return null;
    }

    /**
     * Returns the {@link Vector2} point of the given name.
     * 
     * @param name the name of the point
     * 
     * @return the point as a Vector2, or null if not found
     */
    public Vector2 getPoint(String name) {
        return this.points.get(name);
    }
}
