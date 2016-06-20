package me.scarlet.undertailor.engine.overworld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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

import java.util.Map;

/**
 * An entity within an Overworld.
 */
public abstract class WorldObject implements Renderable, Layerable, Processable, Positionable,
    EventListener, Identifiable, Destructible, Modular<WorldRoom>, Collider {

    private static long nextId = 0;

    private long id;
    private WorldRoom room;

    private Body body;
    private BodyDef def; // acts as a proxy object for position
    private short groupId;
    private boolean canCollide;

    private short layer;
    private float height;
    private Transform transform; // The modifiable transform.
    private Transform proxyTransform; // The actual transform used to render. Used to ensure the object scales right when rendered in Overworld.
    private Renderable actor;

    public WorldObject() {
        this.id = nextId++;
        this.transform = new Transform();
        this.proxyTransform = new Transform();
        this.def = new BodyDef();

        this.def.active = true;
        this.def.awake = true;
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
            this.body.getPosition().set(x * OverworldController.PIXELS_TO_METERS,
                y * OverworldController.PIXELS_TO_METERS);
        }

        this.def.position.set(x, y);
    }

    @Override
    public float getHeight() {
        return this.height * OverworldController.METERS_TO_PIXELS;
    }

    @Override
    public void setHeight(float height) {
        this.height = height * OverworldController.PIXELS_TO_METERS;
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
    // Intended to draw at overworld scale.
    @Override
    public void draw(float x, float y, Transform transform) {
        transform.copyInto(proxyTransform);
        proxyTransform.setScaleX(proxyTransform.getScaleX() * OverworldController.PIXELS_TO_METERS);
        proxyTransform.setScaleY(proxyTransform.getScaleY() * OverworldController.PIXELS_TO_METERS);
        if (this.body != null) {
            proxyTransform.addRotation((float) Math.toDegrees(this.body.getAngle()));
        }

        float drawX;
        float drawY;
        if (this.body == null) {
            Vector2 pos = this.getPosition();
            drawX = pos.x * OverworldController.PIXELS_TO_METERS;
            drawY = pos.y * OverworldController.PIXELS_TO_METERS;
        } else {
            Vector2 pos = this.body.getPosition();
            drawX = pos.x;
            drawY = pos.y;
        }

        this.actor.draw(drawX, drawY + height, proxyTransform);
    }

    // -------- destructible --------

    @Override
    public void destroy() {
        this.def = null;
        this.actor = null;
        if (this.body != null) {
            this.body.getWorld().destroyBody(this.body);
            this.body = null;
        }
    }

    // -------- modular --------

    /**
     * Allows the {@link WorldRoom} to claim this
     * {@link WorldObject}.
     * 
     * @param room the room claiming the object
     * 
     * @return if the object was successfully claimed
     */
    @Override
    public final boolean claim(WorldRoom room) {
        if (this.room == null) {
            this.room = room;
            this.room.requestBody(this);
            return true;
        }

        return false;
    }

    /**
     * Releases this {@link WorldObject} from the provided
     * {@link WorldRoom}.
     * 
     * @param room the room currently owning the object
     * 
     * @return if the object was successfully released
     */
    @Override
    public final boolean release(WorldRoom room) {
        if (this.room == room) {
            this.room = null;
            this.getPosition(); // update the definition position
            if (this.body != null) {
                this.body.getWorld().destroyBody(this.body);
                this.body = null;
            }

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
    public void applyForce(float x, float y, float forceX, float forceY) {
        if (this.body != null) {
            this.body.applyForce(forceX, forceY, x, y, true);
        }
    }

    @Override
    public void applyImpulse(float x, float y, float impX, float impY) {
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
    public short getGroupId() {
        return this.groupId;
    }

    @Override
    public void setGroupId(short id) {
        this.groupId = id;
    }

    // -------------------------------- object --------------------------------

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

        // back to holding the pixel pos
        this.def.position.set(pxX, pxY);
    }

    // ---------------- abstract definitions ----------------

    @Override
    public abstract boolean process(Object... params);

    @Override
    public abstract boolean catchEvent(String eventName, Map<String, Object> data);

    @Override
    public abstract void startCollision(Collider collider);

    @Override
    public abstract void endCollision(Collider collider);
}