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
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.manager.EnvironmentManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CollisionHandler {
    
    public static final Map<Collider, Set<Collider>> RETURN_MAP;
    public static final float PHYSICS_STEP = 1F/60F;
    
    static {
        RETURN_MAP = new HashMap<>();
    }
    
    private World world;
    private float timeAccumulator;
    private Box2DDebugRenderer renderer;
    
    public CollisionHandler() {
        this.reset();
        renderer = new Box2DDebugRenderer();
    }
    
    public void reset() {
        this.timeAccumulator = 0F;
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
                if(uda instanceof Collider && udb instanceof Collider) {
                    ((Collider) uda).getContacts().remove(udb);
                    ((Collider) udb).getContacts().remove(uda);
                }
            }
            
            @Override
            public void beginContact(Contact contact) {
                Object uda = contact.getFixtureA().getBody().getUserData();
                Object udb = contact.getFixtureB().getBody().getUserData();
                if(uda instanceof Collider && udb instanceof Collider) {
                    ((Collider) uda).getContacts().add((Collider) udb);
                    ((Collider) udb).getContacts().add((Collider) uda);
                }
            }
        });
    }
    
    public World getWorld() {
        return this.world;
    }
    
    public void step(float delta) {
        this.timeAccumulator += delta;
        while(this.timeAccumulator > PHYSICS_STEP) {
            this.world.step(PHYSICS_STEP, 6, 2);
            this.timeAccumulator -= PHYSICS_STEP;
        }
        
        EnvironmentManager envMan = Undertailor.getEnvironmentManager();
        if(envMan.getActiveEnvironment() != null && envMan.isRenderingHitboxes()) {
            this.renderer.render(world, Undertailor.getEnvironmentManager().getActiveEnvironment().getOverworldController().getCamera().combined);
        }
    }
}
