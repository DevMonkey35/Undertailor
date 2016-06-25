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
import com.badlogic.gdx.physics.box2d.Shape;

import me.scarlet.undertailor.engine.Collider;
import me.scarlet.undertailor.engine.Modular;
import me.scarlet.undertailor.engine.overworld.map.ObjectLayer.ShapeData;

import java.util.function.Supplier;

public class Entrypoint implements Collider, Modular<WorldRoom> {

    private Body body;
    private WorldRoom parent;
    private ShapeData bodyData;
    private Vector2 spawnPoint;

    private String name;
    private String[] defPoint;
    private String[] defShape;
    private boolean used;
    private String targetEntrypoint;
    private Supplier<WorldRoom> targetRoom;

    public Entrypoint(String name, String defSpawnPoint) {
        this(name, defSpawnPoint, null, null, null);
    }

    public Entrypoint(String name, String defPoint, String defShape, Supplier<WorldRoom> targetRoom,
        String targetEntrypoint) {
        this.name = name;
        this.used = false;
        this.parent = null;
        this.defPoint = defPoint.split(":");
        this.defShape = defShape.split(":");
        this.targetRoom = targetRoom;
        this.targetEntrypoint = targetEntrypoint;

        if (defPoint.length() < 2) {
            throw new IllegalArgumentException("point must be in form \"defLayerName:pointName\"");
        }

        if (defShape.length() < 2) {
            throw new IllegalArgumentException("shape must be in form \"defLayerName:shapeName\"");
        }
    }

    /**
     * Returns the name of this {@link Entrypoint}.
     * 
     * @return the name of this Entrypoint
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the target location for the character object
     * to spawn at when using this {@link Entrypoint}.
     * 
     * @return the spawn location for this entrypoint
     */
    public Vector2 getTargetSpawnpoint() {
        return this.spawnPoint;
    }

    // ---------------- modular ----------------

    @Override
    public boolean claim(WorldRoom parent) {
        if (this.parent == null) {
            this.parent = parent;
            this.bodyData = this.parent.getMap().getDefinedShape(defShape[0], defShape[1]);
            this.spawnPoint = this.parent.getMap().getDefinedPoint(defPoint[0], defPoint[1]);
            if (this.bodyData != null) {
                float ptm = OverworldController.PIXELS_TO_METERS;
                BodyDef def = new BodyDef();
                def.type = BodyType.StaticBody;
                def.position.set(bodyData.getPosition());
                def.position.x = def.position.x * ptm;
                def.position.y = def.position.y * ptm;

                Shape shape = bodyData.generateShape();
                this.body = parent.getOverworld().getCollisionHandler().getWorld().createBody(def);
                this.body.createFixture(shape, 0F);
                this.body.setUserData(this);
                shape.dispose();
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean release(WorldRoom parent) {
        if (this.body != null) {
            this.body.getWorld().destroyBody(this.body);
        }

        return true;
    }

    @Override
    public Body getBody() {
        return this.body;
    }

    // ---------------- collider ----------------

    @Override
    public boolean canCollide() {
        return false;
    }

    @Override
    public void startCollision(Collider collider) {
        if (this.parent == null) {
            return;
        }

        if (this.used) {
            return;
        }

        if (collider instanceof WorldObject) {
            if (this.parent.getOverworld().isCharacter((WorldObject) collider)) {
                this.parent.getOverworld().setRoom(this.targetRoom.get(), this.targetEntrypoint);
                this.used = true;
            }
        }
    }

    @Override
    public void endCollision(Collider collider) {}

    // ---------------- unnecessary methods ----------------

    @Override
    public BodyType getColliderType() {
        return null;
    }

    @Override
    public void setColliderType(BodyType type) {}

    @Override
    public Vector2 getVelocity() {
        return null;
    }

    @Override
    public void setVelocity(float xVel, float yVel) {}

    @Override
    public void applyForce(float forceX, float forceY, float x, float y) {}

    @Override
    public void applyImpulse(float impX, float impY, float x, float y) {}

    @Override
    public void applyTorque(float torque) {}

    @Override
    public void setCanCollide(boolean canCollide) {}

    @Override
    public boolean isRotationFixed() {
        return false;
    }

    @Override
    public void setRotationFixed(boolean rotationFixed) {}

    @Override
    public short getGroupId() {
        return 0;
    }

    @Override
    public void setGroupId(short id) {}
}
