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

package me.scarlet.undertailor.collision;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CollisionHandler {
    
    public static final Map<Collider, Set<Collider>> RETURN_MAP;
    
    static {
        RETURN_MAP = new HashMap<>();
    }
    
    public CollisionHandler() {}
    
    public Map<Collider, Set<Collider>> process(Set<Collider> targets, Set<Collider> colliders) {
        RETURN_MAP.clear();
        for(Collider target : targets) {
            RETURN_MAP.put(target, new HashSet<>());
            for(Collider collider : colliders) {
                if(!collider.canCollide()) {
                    continue;
                }
                
                Polygon targetBox = target.getBoundingBox().getPolygon();
                Polygon colliderBox = collider.getBoundingBox().getPolygon();
                if(Intersector.overlapConvexPolygons(targetBox, colliderBox)) {
                    RETURN_MAP.get(target).add(collider);
                }
            }
        }
        
        return RETURN_MAP;
    }
}
