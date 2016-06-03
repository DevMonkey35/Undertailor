package me.scarlet.undertailor.gfx;

public class Transform implements Cloneable {

    public static final Transform DUMMY = new Transform();

    private float scaleX; // flip can be done by negative scale
    private float scaleY;
    private float skewX; // is skew even possible
    private float skewY;
    private float rotation;
    private boolean flipX;
    private boolean flipY;

    public Transform() {
        this.scaleX = 1F;
        this.scaleY = 1F;
        this.skewX = 0F;
        this.skewY = 0F;
        this.rotation = 0F;
        this.flipX = false;
        this.flipY = false;
    }

    public float getScaleX() {
        return this.scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public void addScaleX(float additive) {
        this.scaleX += additive;
    }

    public float getScaleY() {
        return this.scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public void addScaleY(float additive) {
        this.scaleY += additive;
    }

    public void setScale(float scale) {
        this.scaleX = scale;
        this.scaleY = scale;
    }

    public float getSkewX() {
        return this.skewX;
    }

    public void setSkewX(float skewX) {
        this.skewX = skewX;
    }

    public void addSkewX(float additive) {
        this.skewX += additive;
    }

    public float getSkewY() {
        return this.skewY;
    }

    public void setSkewY(float skewY) {
        this.skewY = skewY;
    }

    public void addSkewY(float additive) {
        this.skewY += additive;
    }

    public float getRotation() {
        return this.rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void addRotation(float additive) {
        this.rotation += additive;
    }

    public boolean getFlipX() {
        return this.flipX;
    }

    public void setFlipX(boolean state) {
        this.flipX = state;
    }

    public void flipX() {
        this.flipX = !this.flipX;
    }

    public boolean getFlipY() {
        return this.flipY;
    }

    public void setFlipY(boolean state) {
        this.flipY = state;
    }

    public void flipY() {
        this.flipY = !this.flipY;
    }

    @Override
    public Transform clone() {
        Transform returned = new Transform();
        return this.copy(returned);
    }
    
    public Transform copy(Transform to) {
        to.scaleX = this.scaleX;
        to.scaleY = this.scaleY;
        to.skewX = this.skewX;
        to.skewY = this.skewY;
        to.rotation = this.rotation;
        to.flipX = this.flipX;
        to.flipY = this.flipY;
        return to;
    }
}
