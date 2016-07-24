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
package me.scarlet.undertailor.gfx.spritesheet;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

/**
 * A pre-made texture that can be drawn on-screen.
 */
public class Sprite implements Renderable, Cloneable {

    // ---------------- static classes ----------------

    public static final SpriteMeta DEFAULT_META = new SpriteMeta();

    /**
     * Metadata for sprite rendering offsets.
     */
    public static class SpriteMeta {

        public static final String[] META_VALUES =
            {"originX", "originY", "wrapX", "wrapY", "offX", "offY"};

        public float originX, originY;
        public int wrapX, wrapY, offX, offY;

        public SpriteMeta() {
            this(0.0F, 0.0F, 0, 0, 0, 0);
        }

        public SpriteMeta(float originX, float originY, int offX, int offY, int wrapX, int wrapY) {
            this.originX = originX;
            this.originY = originY;
            this.wrapX = wrapX;
            this.wrapY = wrapY;
            this.offX = offX;
            this.offY = offY;
        }

        @Override
        public String toString() {
            return "[" + originX + ", " + originY + ", " + wrapX + ", " + wrapY + ", " + offX + ", "
                + offY + "]";
        }
    }

    // ---------------- object ----------------

    private SpriteMeta meta;
    private Transform transform;
    private TextureRegion region;
    private MultiRenderer renderer;
    public Object sourceObject;

    public Sprite(MultiRenderer renderer, TextureRegion sprite, SpriteMeta meta) {
        this.renderer = renderer;
        this.region = sprite;
        this.meta = meta == null ? DEFAULT_META : meta;
        this.transform = new Transform();
    }

    // ---------------- abstract method implementation ----------------

    @Override
    public Transform getTransform() {
        return this.transform;
    }

    @Override
    public void setTransform(Transform transform) {
        Transform.setOrDefault(this.transform, transform);
    }

    @Override
    public void draw(float posX, float posY, Transform transform) {
        float originX = 0, originY = 0;
        int offX = 0, offY = 0;

        if (meta != null) {
            originX = meta.originX;
            originY = meta.originY;
            offX = meta.offX;
            offY = meta.offY;
        }

        float x = posX + (offX * transform.getScaleX());
        float y = posY + (offY * transform.getScaleY());

        region.flip(transform.getFlipX(), transform.getFlipY());
        renderer.draw(this.region, x, y, transform.getScaleX(), transform.getScaleY(), originX,
            originY, transform.getRotation());
        region.flip(transform.getFlipX(), transform.getFlipY());
    }

    @Override
    public Sprite clone() {
        Sprite returned = new Sprite(this.renderer, this.region, this.meta);
        returned.sourceObject = this.sourceObject;
        returned.transform = this.transform.clone();

        return returned;
    }

    // ---------------- g/s ---------------- 

    /**
     * Returns the {@link TextureRegion} drawn by this
     * {@link Sprite}.
     * 
     * @return a TextureRegion
     */
    public TextureRegion getTextureRegion() {
        return region;
    }

    /**
     * Returns the {@link SpriteMeta} used to offset the
     * properties of the underlying {@link TextureRegion}
     * drawn by this {@link Sprite}.
     * 
     * @return a SpriteMeta
     */
    public SpriteMeta getMeta() {
        return meta;
    }
}
