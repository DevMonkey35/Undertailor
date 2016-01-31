package me.scarlet.undertailor.collision.bbshapes;

public abstract class AbstractBoundingBox implements BoundingBox {

    private boolean sensor;
    private boolean canCollide;
    
    public AbstractBoundingBox() {
        this.sensor = false;
    }
    
    @Override
    public boolean canCollide() {
        return this.canCollide;
    }

    @Override
    public void setCanCollide(boolean flag) {
        this.canCollide = flag;
    }

    @Override
    public boolean isSensor() {
        return this.sensor;
    }

    @Override
    public void setSensor(boolean flag) {
        this.sensor = flag;
    }
}
