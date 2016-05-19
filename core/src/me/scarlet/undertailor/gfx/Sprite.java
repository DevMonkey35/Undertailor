package me.scarlet.undertailor.gfx;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A pre-made texture that can be drawn on-screen.
 */
public class Sprite {
    
    /**
     * Metadata for sprite rendering offsets.
     */
    public static class SpriteMeta {
        
        public static final String[] META_VALUES = {"originX", "originY", "wrapX", "wrapY", "offX", "offY"};
        
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
            return "[" + originX + ", " + originY + ", " + wrapX + ", " + wrapY + ", " + offX + ", " + offY + "]";
        }
    }
    
    private SpriteMeta meta;
    private TextureRegion region;
    private MultiRenderer renderer;
    public Sprite(MultiRenderer renderer, TextureRegion sprite, SpriteMeta meta) {
        this.renderer = renderer;
        this.region = sprite;
        this.meta = meta;
    }
    
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
    
    public void draw(float posX, float posY) {
        this.draw(posX, posY, 1.0F);
    }
    
    public void draw(float posX, float posY, float scale) {
        this.draw(posX, posY, scale, scale);
    }
    
    public void draw( float posX, float posY, float scaleX, float scaleY) {
        this.draw(posX, posY, scaleX, scaleY, 0F);
    }
    
    public void draw(float posX, float posY, float scaleX, float scaleY, float rotation) {
        this.draw(posX, posY, scaleX, scaleY, rotation, false);
    }
    
    public void draw(float posX, float posY, float scaleX, float scaleY, float rotation, boolean flipX) {
        this.draw(posX, posY, scaleX, scaleY, rotation, flipX, false);
    }
    
    public void draw(float posX, float posY, float scaleX, float scaleY, float rotation, boolean flipX, boolean flipY) {
        this.draw(posX, posY, scaleX, scaleY, rotation, flipX, flipY, region.getRegionWidth(), region.getRegionHeight());
    }
    
    public void draw(float posX, float posY, float scaleX, float scaleY, float rotation, boolean flipX, boolean flipY, int sizeX, int sizeY) {
        this.draw(posX, posY, scaleX, scaleY, rotation, flipX, flipY, sizeX, sizeY, false);
    }
    
    public void draw(float posX, float posY, float scaleX, float scaleY, float rotation, boolean flipX, boolean flipY, int sizeX, int sizeY, boolean ensureBottomLeft) { // for texts
        float originX = 0, originY = 0;
        int offX = 0, offY = 0;
        if(meta != null) {
            originX = meta.originX;
            originY = meta.originY;
            offX = meta.offX;
            offY = meta.offY;
        }
        
        float x = posX + (offX * scaleX);
        float y = posY + (offY * scaleY);
        if(!ensureBottomLeft) {
            x -= originX;
            y -= originY;
        } else {
            x += originX;
            y += originY;
        }
        
        region.flip(flipX, flipY);
        renderer.draw(region, x, y, originX, originY, sizeX, sizeY, scaleX, scaleY, rotation);
        region.flip(flipX, flipY);
    }
}
