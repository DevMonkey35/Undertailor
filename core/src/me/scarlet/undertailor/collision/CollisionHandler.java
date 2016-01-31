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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CollisionHandler {
    
    public static final Map<Collider, Set<Collider>> RETURN_MAP;
    
    static {
        RETURN_MAP = new HashMap<>();
    }
    
    private World world;
    
    public CollisionHandler() {
        this.reset();
    }
    
    public void reset() {
        this.world = new World(new Vector2(0F, 0F), true);
        this.world.setContactListener(new ContactListener() {
            
            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}
            
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
            
            @Override
            public void endContact(Contact contact) {
                Object uda = contact.getFixtureA().getBody().getUserData();
                Object udb = contact.getFixtureB().getBody().getUserData();
                if(uda instanceof Collider && udb instanceof Collider); {
                    ((Collider) uda).getContacts().remove((Collider) udb);
                    ((Collider) udb).getContacts().remove((Collider) uda);
                }
            }
            
            @Override
            public void beginContact(Contact contact) {
                Object uda = contact.getFixtureA().getBody().getUserData();
                Object udb = contact.getFixtureB().getBody().getUserData();
                if(uda instanceof Collider && udb instanceof Collider); {
                    ((Collider) uda).getContacts().add((Collider) udb);
                    ((Collider) udb).getContacts().add((Collider) uda);
                }
            }
        });
    }
    
    public World getWorld() {
        return this.world;
    }
}
