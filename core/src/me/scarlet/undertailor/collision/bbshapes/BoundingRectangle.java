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

package me.scarlet.undertailor.collision.bbshapes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Oriented bounding box.
 */
public class BoundingRectangle extends AbstractBoundingBox {
    
    private Body targetBody;
    private Vector2 dimensions;
    private Fixture lastFixture;
    
    public BoundingRectangle() {
        this.dimensions = new Vector2(1, 1);
        this.lastFixture = null;
        this.targetBody = null;
    }
    
    public Vector2 getDimensions() {
        return this.dimensions;
    }
    
    public void setDimensions(float x, float y) {
        this.dimensions.set(x, y);
    }
    
    @Override
    public boolean hasTarget() {
        return this.targetBody != null;
    }
    
    @Override
    public void applyFixture(Body body) {
        if(this.targetBody == body || !this.hasTarget()) {
            this.targetBody = body;
            if(lastFixture != null && body.getFixtureList().contains(lastFixture, true)) {
                body.destroyFixture(lastFixture);
            }
            
            if(this.canCollide()) {
                Vector2 offset = this.getOffset();
                PolygonShape polygon = new PolygonShape();
                FixtureDef fixDef = new FixtureDef();
                polygon.setAsBox((dimensions.x * this.getScale()) / 2, (dimensions.y * this.getScale()) / 2, new Vector2(offset.x * this.getScale(), offset.y * this.getScale()), this.getRotation());
                fixDef.isSensor = this.isSensor();
                fixDef.shape = polygon;
                fixDef.friction = 0.0F;
                fixDef.density = 1F;
                
                this.lastFixture = body.createFixture(fixDef);
                polygon.dispose();
            }
        } else {
            throw new IllegalArgumentException("cannot reuse bounding object on another body");
        }
    }
    
    public void destroyFixture(Body body) {
        if(lastFixture != null && body.getFixtureList().contains(lastFixture, true)) {
            body.destroyFixture(lastFixture);
            this.lastFixture = null;
        }
    }
    
    public float[] getVertices() {
        if(lastFixture != null && lastFixture.getShape() != null) {
            PolygonShape shape = (PolygonShape) lastFixture.getShape();
            float[] vertices = new float[shape.getVertexCount() * 2];
            Vector2 vertex = new Vector2();
            for(int i = 0; i < shape.getVertexCount(); i++) {
                shape.getVertex(i, vertex);
                vertices[i * 2] =  vertex.x;
                vertices[i * 2 + 1] = vertex.y;
            }
            
            return vertices;
        }
        
        return null;
    }
}
