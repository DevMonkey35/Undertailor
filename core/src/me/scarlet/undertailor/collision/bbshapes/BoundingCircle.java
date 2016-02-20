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

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Radial bounding box.
 */
public class BoundingCircle extends AbstractBoundingBox {
    
    private float radius;
    private Body targetBody;
    private boolean fixedRotation;
    
    private Fixture lastFixture;
    
    public BoundingCircle() {
        this.fixedRotation = true;
        this.targetBody = null;
        this.radius = 5F;
    }
    
    public boolean isFixedRotation() {
        return this.fixedRotation;
    }
    
    public void setFixedRotation(boolean flag) {
        this.fixedRotation = flag;
    }
    
    public float getRadius() {
        return this.radius;
    }
    
    public void setRadius(float radius) {
        this.radius = radius;
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
                body.setFixedRotation(fixedRotation);
                CircleShape circle = new CircleShape();
                circle.setPosition(this.getOffset());
                circle.setRadius(radius * this.getScale());
                body.createFixture(circle, 0.5F);
                circle.dispose();
            }
        }
    }
    
    @Override
    public void destroyFixture(Body body) {
        if(this.lastFixture != null && body.getFixtureList().contains(lastFixture, true)) {
            body.destroyFixture(lastFixture);
            this.lastFixture = null;
        }
    }
}
