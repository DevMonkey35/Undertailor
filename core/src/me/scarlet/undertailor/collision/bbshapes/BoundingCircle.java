package me.scarlet.undertailor.collision.bbshapes;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import me.scarlet.undertailor.Undertailor;

/**
 * Radial bounding box.
 */
public class BoundingCircle extends AbstractBoundingBox {
    
    private boolean fixedRotation;
    private float radius;
    private float scale;
    
    private Fixture lastFixture;
    
    public BoundingCircle() {
        this.fixedRotation = true;
        this.radius = 5F;
        this.scale = 1F;
    }
    
    public boolean isFixedRotation() {
        return this.fixedRotation;
    }
    
    public void setFixedRotation(boolean flag) {
        this.fixedRotation = flag;
    }
    
    public float getRadius() {
        return this.radius;
    }
    
    public void setRadius(float radius) {
        this.radius = radius;
    }
    
    public float getScale() {
        return this.scale;
    }
    
    public void setScale(float scale) {
        this.scale = scale;
    }
    
    @Override
    public void applyFixture(Body body) {
        if(lastFixture != null && body.getFixtureList().contains(lastFixture, true)) {
            body.destroyFixture(lastFixture);
        }

        body.setFixedRotation(fixedRotation);
        CircleShape circle = new CircleShape();
        circle.setRadius(radius * scale);
        body.createFixture(circle, 0.5F);
        circle.dispose();
    }
    
    @Override
    public void renderBox(Body body) {
        if(lastFixture != null && lastFixture.getShape() != null) {
            CircleShape shape = (CircleShape) lastFixture.getShape();
            Undertailor.getRenderer().drawCircle(body.getPosition().x, body.getPosition().y, shape.getRadius() * scale);
        }
    }
}
