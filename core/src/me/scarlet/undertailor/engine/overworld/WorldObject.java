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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import me.scarlet.undertailor.engine.Collider;
import me.scarlet.undertailor.engine.Destructible;
import me.scarlet.undertailor.engine.EventListener;
import me.scarlet.undertailor.engine.Identifiable;
import me.scarlet.undertailor.engine.Layerable;
import me.scarlet.undertailor.engine.Modular;
import me.scarlet.undertailor.engine.Positionable;
import me.scarlet.undertailor.engine.Processable;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

import java.util.HashSet;
import java.util.Set;

/**
 * An entity within an Overworld.
 */
public abstract class WorldObject implements Renderable, Layerable, Processable, Positionable,
    EventListener, Identifiable, Destructible, Modular<WorldRoom>, Collider {

    private static long nextId = 0;

    private long id;
    private WorldRoom room;
    private boolean visible;
    private boolean destroyed;

    private Body body;
    private BodyDef def; // acts as a proxy object for position
    private short groupId;
    private boolean canCollide;
    private Set<Shape> boundingQueue;

    private short layer;
    private float height;
    private Transform transform; // The modifiable transform.
    private Transform proxyTransform;
    private Renderable actor;

    public WorldObject() {
        this.id = nextId++;
        this.destroyed = false;
        this.transform = new Transform();
        this.proxyTransform = new Transform();
        this.boundingQueue = new HashSet<>();
        this.def = new BodyDef();
        this.visible = true;

        this.def.active = true;
        this.def.awake = true;
        this.def.fixedRotation = true;
        this.def.type = BodyType.DynamicBody;

        this.groupId = -1;
        this.canCollide = true;
    }

    // ---------------- g/s object params / a whole lot of abstract method implementation god damnit ----------------
    // -------- identifiable --------

    @Override
    public long getId() {
        return this.id;
    }

    // -------- positionable --------

    @Override
    public Vector2 getPosition() {
        if (this.body != null) {
            this.def.position.set(this.body.getPosition());
            this.def.position.x = this.def.position.x * OverworldController.METERS_TO_PIXELS;
            this.def.position.y = this.def.position.y * OverworldController.METERS_TO_PIXELS;
        }

        // bodydef always holds pixel-based position
        return this.def.position;
    }

    @Override
    public void setPosition(float x, float y) {
        if (this.body != null) {
            this.body.setTransform(x * OverworldController.PIXELS_TO_METERS,
                y * OverworldController.PIXELS_TO_METERS, this.body.getAngle());
        }

        this.def.position.set(x, y);
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    @Override
    public void setHeight(float height) {
        this.height = height;
    }

    // -------- layerable --------

    @Override
    public short getLayer() {
        return this.layer;
    }

    @Override
    public void setLayer(short layer) {
        this.layer = layer;
    }

    // -------- renderable --------

    @Override
    public Transform getTransform() {
        return this.transform;
    }

    @Override
    public void setTransform(Transform transform) {
        Transform.setOrDefault(this.transform, transform);
    }

    // ---------------- functional ----------------
    // -------- renderable --------

    // Ignores provided positions.
    @Override
    public void draw(float x, float y, Transform transform) {
        if (!this.visible || this.destroyed || this.actor == null) {
            return;
        }

        transform.copyInto(proxyTransform);
        if (this.body != null) {
            proxyTransform.addRotation((float) Math.toDegrees(this.body.getAngle()));
        }

        float drawX;
        float drawY;
        if (this.body == null) {
            Vector2 pos = this.getPosition();
            drawX = pos.x;
            drawY = pos.y;
        } else {
            Vector2 pos = this.body.getPosition();
            drawX = pos.x * OverworldController.METERS_TO_PIXELS;
            drawY = pos.y * OverworldController.METERS_TO_PIXELS;
        }

        this.actor.draw(drawX, drawY + height, proxyTransform);
    }

    // -------- processable --------

    @Override
    public final boolean process(Object... params) {
        if (this.destroyed) {
            return false;
        }

        return this.processObject(params);
    }

    // -------- destructible --------

    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }

    @Override
    public void destroy() {
        if(this.destroyed) {
            return;
        }

        this.def = null;
        this.actor = null;
        if (this.body != null) {
            this.body.getWorld().destroyBody(this.body);
            this.body = null;
        }

        this.destroyed = true;
    }

    // -------- modular --------

    @Override
    public final boolean claim(WorldRoom room) {
        if (this.room == null) {
            this.room = room;
            this.room.requestBody(this);
            return true;
        }

        return false;
    }

    @Override
    public final boolean release(WorldRoom room) {
        if (this.room == room) {
            this.room = null;
            return true;
        }

        return false;
    }

    // -------- collider --------

    @Override
    public Body getBody() {
        return this.body;
    }

    @Override
    public BodyType getColliderType() {
        return this.def.type;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Changing the body type of a {@link WorldObject}
     * will have no effect once it has been registered with
     * a {@link WorldRoom}.</p>
     */
    @Override
    public void setColliderType(BodyType type) {
        this.def.type = type;
    }

    @Override
    public Vector2 getVelocity() {
        if (this.body != null) {
            return this.body.getLinearVelocity();
        } else {
            return this.def.linearVelocity;
        }
    }

    @Override
    public void setVelocity(float xVel, float yVel) {
        if (this.body != null) {
            this.body.setLinearVelocity(xVel, yVel);
        } else {
            this.def.linearVelocity.set(xVel, yVel);
        }
    }

    @Override
    public void applyForce(float forceX, float forceY, float x, float y) {
        if (this.body != null) {
            this.body.applyForce(forceX, forceY, x, y, true);
        }
    }

    @Override
    public void applyImpulse(float impX, float impY, float x, float y) {
        if (this.body != null) {
            this.body.applyLinearImpulse(impX, impY, x, y, true);
        }
    }

    @Override
    public void applyTorque(float torque) {
        if (this.body != null) {
            this.body.applyTorque(torque, true);
        }
    }

    @Override
    public boolean canCollide() {
        return this.canCollide;
    }

    @Override
    public void setCanCollide(boolean canCollide) {
        this.canCollide = canCollide;
    }

    @Override
    public boolean isRotationFixed() {
        if (this.body != null) {
            return this.body.isFixedRotation();
        }

        return this.def.fixedRotation;
    }

    @Override
    public void setRotationFixed(boolean rotationFixed) {
        if (this.body != null) {
            this.body.setFixedRotation(rotationFixed);
        }

        this.def.fixedRotation = rotationFixed;
    }

    @Override
    public short getGroupId() {
        return this.groupId;
    }

    @Override
    public void setGroupId(short id) {
        this.groupId = id;
    }

    // -------------------------------- object --------------------------------

    /**
     * Returns the {@link WorldRoom} this
     * {@link WorldObject} currently resides in.
     * 
     * @return this WorldObject's current WorldRoom
     */
    public WorldRoom getRoom() {
        return this.room;
    }

    /**
     * Returns the {@link Renderable} serving as the graphic
     * used to display this {@link WorldObject}.
     * 
     * <p>While it is possible to access the
     * {@link Transform} of the provided Renderable, its own
     * transform is ignored in favor of applying the
     * Transform set on this {@link WorldObject}.</p>
     * 
     * @return the Renderable actor of this WorldObject
     */
    public Renderable getActor() {
        return this.actor;
    }

    /**
     * Sets the {@link Renderable} serving as the graphic
     * used to display this {@link WorldObject}.
     * 
     * <p>Providing a WorldObject to serve as a
     * WorldObject's actor will raise an
     * IllegalArgumentException.</p>
     * 
     * @param actor the Renderable to use as this
     *        WorldObject's actor
     */
    public void setActor(Renderable actor) {
        if (actor instanceof WorldObject) {
            throw new IllegalArgumentException("Cannot use a WorldObject as a WorldObject's actor");
        }

        this.actor = actor;
    }

    /**
     * Queues a {@link Shape} to be set as a {@link Fixture}
     * on this {@link WorldObject}'s body.
     * 
     * <p>If the body is ready, this method immediately
     * applies the Shape as a Fixture.</p>
     * 
     * <p>Note: Do <strong>NOT</strong> dispose of the Shape
     * after sending it to this method. This method
     * <strong>will automatically dispose of the
     * Shape</strong> after it has been applied to this
     * WorldObject's body. Disposing of the shape after
     * passing to this method will crash the Java Virtual
     * Machine, as Shapes are written in C++ JNI.</p>
     * 
     * @param shape the shape to apply as a bounding shape,
     *        do not dispose after passing to this method
     */
    public void queueBoundingShape(Shape shape) {
        if (this.body != null) {
            this.body.createFixture(shape, 0F);
            shape.dispose();
        } else if (this.boundingQueue != null) {
            this.boundingQueue.add(shape);
        }
    }

    /**
     * Returns whether or not this {@link WorldObject}'s
     * actor will be rendered.
     * 
     * @return if this WorldObject is visible
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Sets whether or not this {@link WorldObject}'s actor
     * will be rendered.
     * 
     * @param visible if this WorldObject will be visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    // ---------------- internal ----------------

    /**
     * Called once the parent room is ready to create a body
     * for this {@link WorldObject}.
     * 
     * @param world the World to create a body with
     */
    void createBody(World world) {
        // hold the pixel-based positions
        float pxX = this.def.position.x;
        float pxY = this.def.position.y;

        // set to meters
        this.def.position.set(pxX * OverworldController.PIXELS_TO_METERS,
            pxY * OverworldController.PIXELS_TO_METERS);
        this.body = world.createBody(this.def);
        this.body.setUserData(this);

        if (this.boundingQueue != null) {
            this.boundingQueue.forEach(shape -> {
                this.body.createFixture(shape, 0F);
                shape.dispose();
            });

            this.boundingQueue = null;
        }

        // back to holding the pixel pos
        this.def.position.set(pxX, pxY);
    }

    // ---------------- abstract definitions ----------------

    /**
     * Executes the routine processing of this
     * {@link WorldObject}.
     * 
     * @param params generic parameters
     * 
     * @return generic return value
     */
    public abstract boolean processObject(Object... params);

    @Override
    public abstract boolean catchEvent(String eventName, Object... data);

    @Override
    public abstract void startCollision(Collider collider);

    @Override
    public abstract void endCollision(Collider collider);
}
