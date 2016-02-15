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

public abstract class AbstractBoundingBox implements BoundingBox {

    private float scale;
    private float rotation;
    private boolean sensor;
    private boolean canCollide;
    private Vector2 offset;
    
    public AbstractBoundingBox() {
        this.canCollide = true;
        this.sensor = false;
        this.rotation = 0F;
        this.scale = 1F;
        this.offset = new Vector2(0, 0);
    }
    
    @Override
    public float getRotation() {
        return this.rotation;
    }
    
    @Override
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
    
    @Override
    public boolean canCollide() {
        return this.canCollide;
    }

    @Override
    public void setCanCollide(boolean flag) {
        this.canCollide = flag;
    }

    @Override
    public boolean isSensor() {
        return this.sensor;
    }

    @Override
    public void setSensor(boolean flag) {
        this.sensor = flag;
    }
    
    @Override
    public float getScale() {
        return this.scale;
    }
    
    @Override
    public void setScale(float scale) {
        this.scale = scale;
    }
    
    @Override
    public Vector2 getOffset() {
        return this.offset;
    }
    
    @Override
    public void setOffset(float x, float y) {
        this.offset.set(x, y);
    }
}
