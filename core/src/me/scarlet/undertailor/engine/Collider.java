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

package me.scarlet.undertailor.engine;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/**
 * Skeleton implementation for objects that can collide, and
 * thus be affected by physics.
 * 
 * <p>It is possible for {@link Collider}s to not have a
 * representative body as of yet, thus the methods are
 * required to be implemented should the body be missing
 * when called.</p>
 */
public interface Collider {

    /**
     * Returns the body of this {@link Collider}.
     * 
     * @return this Collider's body, or null if not found
     */
    Body getBody();

    /**
     * Returns the {@link BodyType} of this {@link Collider}
     * .
     * 
     * @return the BodyType of this Collider
     */
    BodyType getColliderType();

    /**
     * Sets the {@link BodyType} of this {@link Collider}.
     * 
     * @param type the new BodyType of this collider
     */
    void setColliderType(BodyType type);

    /**
     * Returns the velocity of this {@link Collider} as a
     * {@link Vector2}.
     * 
     * @return the current velocity of this Collider
     */
    Vector2 getVelocity();

    /**
     * Sets the velocity of this {@link Collider}.
     * 
     * @param xVel the new x velocity
     * @param yVel the new y velocity
     */
    void setVelocity(float xVel, float yVel);

    /**
     * Applies a force to this {@link Collider} at the
     * provided local point. 0, 0 is the center of the body.
     * 
     * <p>Forces will build up velocity when continuously
     * applied.</p>
     * 
     * <p>Applying a force anywhere other than at the body's
     * center will generate torque.</p>
     * 
     * @param x the x-coordinate of the local point to apply
     *        the force to
     * @param y the y-coordinate of the local point to apply
     *        the force to
     * @param forceX the force's power on the X axis, in
     *        newtons
     * @param forceY the force's power on the Y axis, in
     *        newtons
     */
    void applyForce(float forceX, float forceY, float x, float y);

    /**
     * Applies an impulse to this {@link Collider} at the
     * provided local point. 0, 0 is the center of the body.
     * 
     * <p>Impulses will instantly change the velocity rather
     * than build up when continuously applied.</p>
     * 
     * <p>Applying an impulse anywhere other than at the
     * body's center will generate torque.</p>
     * 
     * @param x the x-coordinate of the local point to apply
     *        the force to
     * @param y the y-coordinate of the local point to apply
     *        the force to
     * @param impX the impulse's power on the X axis, in
     *        newtons
     * @param impY the impulse's power on the Y axis, in
     *        newtons
     */
    void applyImpulse(float impX, float impY, float x, float y);

    /**
     * Applies a torque to this {@link Collider}.
     * 
     * @param torque the torque to apply
     */
    void applyTorque(float torque);

    /**
     * Notifies about a collision between this
     * {@link Collider} the provided Collider.
     * 
     * @param collider the collider to have collided with
     */
    void startCollision(Collider collider);

    /**
     * Notifies about the end of a collision between this
     * {@link Collider} and the provided Collider.
     * 
     * @param collider the collider to have ceased collision
     *        with
     */
    void endCollision(Collider collider);

    /**
     * Returns whether or not this {@link Collider} is able
     * to collide.
     * 
     * @return whether or not this Collider can collide
     */
    boolean canCollide();

    /**
     * Sets whether or not this {@link Collider} can
     * collide.
     * 
     * @param canCollide if this Collider can collide
     */
    void setCanCollide(boolean canCollide);

    /**
     * Returns the group ID of this {@link Collider}. A
     * negative group ID represents no group.
     * 
     * <p>Colliders with matching group IDs will not collide
     * with each other.</p>
     * 
     * @return the group ID of this Collider
     */
    short getGroupId();

    /**
     * Sets the group ID of this {@link Collider}. Use -1 to
     * represent no group.
     * 
     * @param id the new group ID of this collider.
     */
    void setGroupId(short id);

    // ---------------- default methods ----------------

    /**
     * Applies a force to the center of this
     * {@link Collider}.
     * 
     * @param forceX the force's power on the X axis, in
     *        newtons
     * @param forceY the force's power on the Y axis, in
     *        newtons
     */
    default void applyCenterForce(float forceX, float forceY) {
        this.applyForce(forceX, forceY, 0, 0);
    }

    /**
     * Applies an impulse to the center of this
     * {@link Collider}.
     * 
     * @param impulseX the impulse's power on the X axis, in
     *        newtons
     * @param impulseY the impulse's power on the Y axis, in
     *        newtons
     */
    default void applyCenterImpulse(float impX, float impY) {
        this.applyImpulse(impX, impY, 0, 0);
    }
}
