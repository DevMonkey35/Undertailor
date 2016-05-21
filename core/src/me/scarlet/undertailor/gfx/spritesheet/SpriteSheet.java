package me.scarlet.undertailor.gfx.spritesheet;

import java.util.Collection;

/**
 * Skeleton implementation of a spritesheet.
 */
public interface SpriteSheet {
    
    /**
     * Returns the name assigned to this {@link SpriteSheet}
     * .
     * 
     * @return the name of this SpriteSheet
     */
    public String getSheetName();
    
    /**
     * Returns the sprite stored at the given index of this
     * {@link SpriteSheet}.
     * 
     * @param index the index to check
     * 
     * @return a sprite stored in the provided index, or
     *         null if not found
     */
    public Sprite getSprite(int index);
    
    /**
     * Returns a read-only collection of all the sprites
     * held by this {@link SpriteSheet}.
     * 
     * @return a Collection of Sprites
     */
    public Collection<Sprite> getSprites();
}
