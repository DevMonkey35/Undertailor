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
    private boolean used;
    private WorldRoom parent;
    private ShapeData bodyData;
    private Vector2 spawnPoint;

    private String name;
    private String mapEntrypoint;
    private String targetEntrypoint;
    private Supplier<WorldRoom> targetRoom;

    private Entrypoint(String mapEntrypoint) {
        this.used = false;
        this.parent = null;

        this.name = null;
        this.mapEntrypoint = mapEntrypoint;
        this.targetEntrypoint = null;
        this.targetRoom = null;

        if (this.mapEntrypoint.split(":").length < 2) {
            throw new IllegalArgumentException("Entrypoint name must be in layer:point format");
        }
    }

    /**
     * 
     * @param name the name of this Entrypoint
     * @param mapEntrypoint the name of the shape defined
     *        for this Entrypoint
     */
    public Entrypoint(String name, String mapEntrypoint) {
        this(mapEntrypoint);

        this.name = name;
    }

    /**
     * 
     * @param name the name of this Entrypoint
     * @param mapEntrypoint the name of the shape defined
     *        for this Entrypoint
     * @param targetRoom the supplier giving an instance of
     *        the target room
     * @param spawnX the x-coordinate of the position to
     *        spawn at in the target room
     * @param spawnY the y-coordinate of the position to
     *        spawn at in the target room
     */
    public Entrypoint(String name, String mapEntrypoint, Supplier<WorldRoom> targetRoom,
        float spawnX, float spawnY) {
        this(mapEntrypoint);

        this.name = name;
        this.targetRoom = targetRoom;
        this.spawnPoint = new Vector2(spawnX, spawnY);
    }

    /**
     * 
     * @param name the name of this Entrypoint
     * @param mapEntrypoint the name of the shape defined
     *        for this Entrypoint
     * @param targetRoom the supplier giving an instance of
     *        the target room
     * @param targetEntrypoint the name of the entrypoint to
     *        spawn at in the target room
     */
    public Entrypoint(String name, String mapEntrypoint, Supplier<WorldRoom> targetRoom,
        String targetEntrypoint) {
        this(mapEntrypoint);

        this.name = name;
        this.targetRoom = targetRoom;
        this.targetEntrypoint = targetEntrypoint;
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
        return this.spawnPoint == null ? this.bodyData.getPosition() : this.spawnPoint;
    }

    // ---------------- modular ----------------

    @Override
    public WorldRoom getParent() {
        return this.parent;
    }

    @Override
    public boolean claim(WorldRoom parent) {
        if (this.parent == null) {
            this.parent = parent;

            String[] defShape = this.mapEntrypoint.split(":");
            this.bodyData = this.parent.getMap().getDefinedShape(defShape[0], defShape[1]);
            if (this.spawnPoint == null) {
                String rawPoint = this.parent.getMap().getEntrypointSpawn(this.mapEntrypoint);

                if (rawPoint != null && !rawPoint.trim().isEmpty()) {
                    String[] defPoint = rawPoint.split(":");
                    this.spawnPoint =
                        this.parent.getMap().getDefinedPoint(defPoint[0], defPoint[1]);
                }
            }

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
        if (this.targetRoom == null) {
            return;
        }

        if (this.parent == null) {
            return;
        }

        if (this.used) {
            return;
        }

        if (collider instanceof WorldObject) {
            if (this.parent.getOverworld().isCharacter((WorldObject) collider)) {
                this.used = true;
                this.parent.getOverworld().setRoom(this.targetRoom.get(), this.targetEntrypoint);
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
    public float getRotation() {
        return 0;
    }

    @Override
    public void setRotation(float rotation) {}

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
