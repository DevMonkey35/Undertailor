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

package me.scarlet.undertailor.engine.overworld;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import me.scarlet.undertailor.engine.Collider;
import me.scarlet.undertailor.engine.Destructible;
import me.scarlet.undertailor.util.Pair;

/**
 * Handles collision between {@link Collider}s within an
 * Overworld.
 */
public class CollisionHandler implements Destructible {

    public static final float PHYSICS_STEP = 1F / 60F;

    static final Pair<Collider> RETURN_PAIR;
    static final ContactListener LISTENER;

    static {
        RETURN_PAIR = new Pair<>();
        LISTENER = new ContactListener() {

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                Pair<Collider> pair = checkObjects(contact);
                if (pair != null) {
                    if (!pair.getA().canCollide() || !pair.getB().canCollide()
                        || (pair.getA().getGroupId() >= 0 && pair.getA().getGroupId() == pair.getB().getGroupId())) {
                        contact.setEnabled(false);
                    }
                }
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}

            @Override
            public void endContact(Contact contact) {
                Pair<Collider> pair = checkObjects(contact);
                if (pair != null) {
                    if(pair.getA() instanceof WorldObject && pair.getB() instanceof WorldObject) {
                        pair.getA().endCollision(pair.getB());
                        pair.getB().endCollision(pair.getA());
                    } else {
                        if(pair.getA() instanceof Entrypoint) {
                            pair.getA().endCollision(pair.getB());
                        } else if(pair.getB() instanceof Entrypoint) {
                            pair.getB().endCollision(pair.getA());;
                        }
                    }
                }
            }

            @Override
            public void beginContact(Contact contact) {
                Pair<Collider> pair = checkObjects(contact);
                if (pair != null) {
                    if(pair.getA() instanceof WorldObject && pair.getB() instanceof WorldObject) {
                        pair.getA().startCollision(pair.getB());
                        pair.getB().startCollision(pair.getA());
                    } else {
                        if(pair.getA() instanceof Entrypoint) {
                            pair.getA().startCollision(pair.getB());
                        } else if(pair.getB() instanceof Entrypoint) {
                            pair.getB().startCollision(pair.getA());;
                        }
                    }
                }
            }
        };
    }

    /**
     * Internal method.
     * 
     * <p>Quick check method to ensure eligibility for
     * intereference by this {@link CollisionHandler}.</p>
     */
    private static Pair<Collider> checkObjects(Contact contact) {
        Object a = contact.getFixtureA().getBody().getUserData();
        Object b = contact.getFixtureB().getBody().getUserData();
        if (a instanceof Collider && b instanceof Collider) {
            CollisionHandler.RETURN_PAIR.setItems((Collider) a, (Collider) b);
            return CollisionHandler.RETURN_PAIR;
        }

        return null;
    }

    // ---------------- object ----------------

    private World world;
    private boolean destroyed;
    private float timeAccumulator;
    private Box2DDebugRenderer renderer;
    private OverworldCamera overworldCam;
    private OrthographicCamera rendererCam;

    public CollisionHandler(OverworldCamera ovwCam, boolean renderCollision) {
        this.overworldCam = ovwCam;
        this.rendererCam = new OrthographicCamera(640, 480);
        this.rendererCam.zoom = 1 / OverworldController.METERS_TO_PIXELS;
        this.destroyed = false;
        this.reset();

        this.renderer = new Box2DDebugRenderer(true, true, false, true, false, true);
    }

    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }

    @Override
    public void destroy() {
        if(this.destroyed) {
            return;
        }

        this.world.dispose();
        this.renderer.dispose();

        this.world = null;
        this.renderer = null;
        this.destroyed = true;
    }

    /**
     * Resets this {@link CollisionHandler}, disposing of
     * the old {@link World} and creating a new one.
     */
    public void reset() {
        this.timeAccumulator = 0F;
        if (this.world != null) {
            this.world.dispose();
        }

        this.world = new World(new Vector2(0F, 0F), true);
        this.world.setContactListener(CollisionHandler.LISTENER);
    }

    /**
     * Returns the {@link World} currently being used by
     * this {@link CollisionHandler}.
     * 
     * @return za warudo
     */
    public World getWorld() {
        return this.world;
    }

    /**
     * Steps the physics simulation by the provided amount
     * of time.
     * 
     * @param delta the time since the last frame
     */
    public void step(float delta) {
        this.timeAccumulator += delta;
        while (this.timeAccumulator > PHYSICS_STEP) {
            this.world.step(PHYSICS_STEP, 6, 2);
            this.timeAccumulator -= PHYSICS_STEP;
        }
    }

    /**
     * Draws the debug renderer, showing debug data for
     * physics objects currently in the owning Overworld.
     */
    public void render() {
        float ptm = OverworldController.PIXELS_TO_METERS;
        this.rendererCam.position.set(this.overworldCam.getPosition(), 0);
        this.rendererCam.position.x = this.rendererCam.position.x * ptm;
        this.rendererCam.position.y = this.rendererCam.position.y * ptm;
        this.rendererCam.zoom = 1 / (this.overworldCam.getZoom() * OverworldController.METERS_TO_PIXELS);
        this.rendererCam.update();
        
        this.renderer.render(world, this.rendererCam.combined);
    }
}
