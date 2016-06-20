package me.scarlet.undertailor.engine.overworld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import me.scarlet.undertailor.engine.Collider;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.util.Pair;

public class CollisionHandler {

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
                        || (pair.getA().getGroupId() < 0
                            && pair.getA().getGroupId() == pair.getB().getGroupId())) {
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
                    pair.getA().endCollision(pair.getB());
                    pair.getB().endCollision(pair.getA());
                }
            }

            @Override
            public void beginContact(Contact contact) {
                Pair<Collider> pair = checkObjects(contact);
                if (pair != null) {
                    pair.getA().startCollision(pair.getB());
                    pair.getB().startCollision(pair.getA());
                }
            }
        };
    }

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
    private float timeAccumulator;
    private MultiRenderer mRenderer;
    private Box2DDebugRenderer renderer;

    public CollisionHandler(MultiRenderer mRenderer, boolean renderCollision) {
        this.mRenderer = mRenderer;
        this.reset();

        this.renderer = new Box2DDebugRenderer(true, true, true, true, true, false);
    }

    public void reset() {
        this.timeAccumulator = 0F;
        if (this.world != null) {
            this.world.dispose();
        }

        this.world = new World(new Vector2(0F, 0F), true);
        this.world.setContactListener(CollisionHandler.LISTENER);
    }

    public World getWorld() {
        return this.world;
    }

    public void step(float delta) {
        this.timeAccumulator += delta;
        while (this.timeAccumulator > PHYSICS_STEP) {
            this.world.step(PHYSICS_STEP, 6, 2);
            this.timeAccumulator -= PHYSICS_STEP;
        }
    }

    public void render() {
        this.renderer.render(world, mRenderer.getBatchProjectionMatrix());
    }
}
