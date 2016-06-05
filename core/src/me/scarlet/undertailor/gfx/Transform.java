package me.scarlet.undertailor.gfx;

/**
 * Databag class, for holding values used to modify the
 * appearance of sprites and images to be drawn on the
 * screen.
 */
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

    // ---------------- g/s/m transform parameters ----------------

    /**
     * Returns the horizontal scale value applied to the
     * object drawn using this {@link Transform}.
     * 
     * @return the horizontal scale
     */
    public float getScaleX() {
        return this.scaleX;
    }

    /**
     * Sets the horizontal scale value applied to the object
     * drawn using this {@link Transform}.
     * 
     * @param scaleX the horizontal scale value to set
     */
    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    /**
     * Convenience method, adding the provided value to the
     * current horizontal scale value and setting it.
     * 
     * @param additive the value to add to the current
     *        horizontal scale value
     */
    public void addScaleX(float additive) {
        this.scaleX += additive;
    }

    /**
     * Returns the vertical scale value applied to the
     * object drawn using this {@link Transform}.
     * 
     * @return the vertical scale
     */
    public float getScaleY() {
        return this.scaleY;
    }

    /**
     * Sets the vertical scale value applied to the object
     * drawn using this {@link Transform}.
     * 
     * @param scaleY the vertical scale value to set
     */
    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    /**
     * Convenience method, adding the provided value to the
     * current vertical scale value and setting it.
     * 
     * @param additive the value to add to the current
     *        vertical scale value
     */
    public void addScaleY(float additive) {
        this.scaleY += additive;
    }

    /**
     * Sets the scale of the object draw using this
     * {@link Transform}, effectively setting both the
     * horizontal and vertical scale values to the same
     * value.
     * 
     * @param scale the value to set as both the horizontal
     *        and vertical scale values
     */
    public void setScale(float scale) {
        this.scaleX = scale;
        this.scaleY = scale;
    }

    /**
     * Returns the horizontal shear value applied to the
     * object drawn using this {@link Transform}.
     * 
     * @return the horizontal shear
     */
    public float getSkewX() {
        return this.skewX;
    }

    /**
     * Sets the horizontal shear value applied to the object
     * drawn using this {@link Transform}.
     * 
     * @param skewX the new horizontal shear value
     */
    public void setSkewX(float skewX) {
        this.skewX = skewX;
    }

    /**
     * Convenience method, adding the provided value to the
     * current horizontal shear value and setting it.
     * 
     * @param additive the value to add to the current
     *        horizontal shear value
     */
    public void addSkewX(float additive) {
        this.skewX += additive;
    }

    /**
     * Returns the vertical shear value applied to the
     * object drawn using this {@link Transform}.
     * 
     * @return the vertical shear
     */
    public float getSkewY() {
        return this.skewY;
    }

    /**
     * Sets the vertical shear value applied to the object
     * drawn using this {@link Transform}.
     * 
     * @param skewY the new vertical shear value
     */
    public void setSkewY(float skewY) {
        this.skewY = skewY;
    }

    /**
     * Convenience method, adding the provided value to the
     * current vertical shear value and setting it.
     * 
     * @param additive the value to add to the current
     *        vertical shear value
     */
    public void addSkewY(float additive) {
        this.skewY += additive;
    }

    /**
     * Returns the rotation value applied to the object
     * drawn using this {@link Transform}.
     * 
     * <p>The value is always bounded between 0 and 360.</p>
     * 
     * @return the rotation
     */
    public float getRotation() {
        return this.rotation;
    }

    /**
     * Sets the rotation value applied to the object drawn
     * using this {@link Transform}.
     * 
     * <p>If the new value exceeds or sits below the old
     * value, the new value will be changed to sit between 0
     * and 360 while still resolving to the same appearance
     * should the old value had been used instead (e.g. 376
     * -> 16, -280 -> 80, etc).</p>
     * 
     * @param rotation the new rotation value
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;

        if (this.rotation > 360F || this.rotation < 0F) {
            this.rotation = this.rotation % 360.0F;
        }
    }

    /**
     * Convenience method, adding the provided value to the
     * current rotational value and setting it.
     * 
     * @param additive the value to add to the current
     *        rotational value
     */
    public void addRotation(float additive) {
        this.setRotation(this.rotation + additive);
    }

    /**
     * Returns whether or not the object drawn using this
     * {@link Transform} is horizontally flipped.
     * 
     * <p>This does not provide the same result as simply
     * providing a negative horizontal scale value, as the
     * scale value will operate based on the object's point
     * of origin where as this method instructs the renderer
     * to flip the source texture for object to be drawn
     * (that is, it just mirrors it leaving it at the exact
     * same spot it would be if it were not mirrored).</p>
     * 
     * @return wheher or not the target object is
     *         horizontally flipped
     */
    public boolean getFlipX() {
        return this.flipX;
    }

    /**
     * Sets whether or not the object drawn using this
     * {@link Transform} is horizontally flipped.
     * 
     * @param state new state of horizontal flip
     */
    public void setFlipX(boolean state) {
        this.flipX = state;
    }

    /**
     * Flips the value of the current horizontal flip state,
     * resolving to false if currently true and vice-versa.
     */
    public void flipX() {
        this.flipX = !this.flipX;
    }

    /**
     * Returns whether or not the object drawn using this
     * {@link Transform} is vertically flipped.
     * 
     * <p>This does not provide the same result as simply
     * providing a negative vertical scale value, as the
     * scale value will operate based on the object's point
     * of origin where as this method instructs the renderer
     * to flip the source texture for object to be drawn
     * (that is, it just mirrors it leaving it at the exact
     * same spot it would be if it were not mirrored).</p>
     * 
     * @return wheher or not the target object is vertically
     *         flipped
     */
    public boolean getFlipY() {
        return this.flipY;
    }

    /**
     * Sets whether or not the object drawn using this
     * {@link Transform} is vertically flipped.
     * 
     * @param state new state of vertical flip
     */
    public void setFlipY(boolean state) {
        this.flipY = state;
    }

    /**
     * Flips the value of the current vertical flip state,
     * resolving to false if currently true and vice-versa.
     */
    public void flipY() {
        this.flipY = !this.flipY;
    }

    // ---------------- utility/functional ----------------

    @Override
    public Transform clone() {
        Transform returned = new Transform();
        return this.copy(returned);
    }

    /**
     * Replicates the values held by this {@link Transform}
     * object into the provided Transform object.
     * 
     * @param to the Transform to copy values over to
     * 
     * @return the provided Transform with its new values
     */
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
