package me.scarlet.undertailor.gfx;

/**
 * Skeleton implementation of something that can be drawn.
 */
public interface Renderable {

    /**
     * @see #draw(float, float, float, float, boolean,
     *      boolean, float)
     */
    public default void draw(float x, float y) {
        this.draw(x, y, 1F);
    }

    /**
     * @see #draw(float, float, float, float, boolean,
     *      boolean, float)
     */
    public default void draw(float x, float y, float scale) {
        this.draw(x, y, scale, scale);
    }

    /**
     * @see #draw(float, float, float, float, boolean,
     *      boolean, float)
     */
    public default void draw(float x, float y, float scaleX, float scaleY) {
        this.draw(x, y, scaleX, scaleY, false, false);
    }

    /**
     * @see #draw(float, float, float, float, boolean,
     *      boolean, float)
     */
    public default void draw(float x, float y, float scaleX, float scaleY, boolean flipX,
        boolean flipY) {
        this.draw(x, y, scaleX, scaleY, flipX, flipY, 0F);
    }

    /**
     * Draws this {@link Renderable} with the given
     * parameters.
     * 
     * @param x the x position of the sprite's anchor point
     * @param y the y position of the sprite's anchor point
     * @param scaleX the scaling of the sprite horizontally
     * @param scaleY the scaling of the sprite vertically
     * @param rotation the rotation of the sprite, anchored
     *        at the anchor point defined by the origin
     *        offset values
     */
    public void draw(float x, float y, float scaleX, float scaleY, boolean flipX, boolean flipY,
        float rotation);

}
