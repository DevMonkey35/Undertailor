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

import com.badlogic.gdx.utils.Array;

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
    public Sprite getSprite(String index);

    /**
     * Returns a read-only collection of all the sprites
     * held by this {@link SpriteSheet}.
     * 
     * @return a Collection of Sprites
     */
    public Array<Sprite> getSprites();

    /**
     * Returns the number of {@link Sprite}s held by this
     * {@link SpriteSheet}.
     * 
     * <p>For factory-generated SpriteSheets, this method
     * proves more efficient than simply calling
     * {@link Array#size} on the value returned by
     * {@link #getSprites()}, since the sheet can simply
     * return the count of sprites held by its factory.</p>
     * 
     * @return the number of Sprites this SpriteSheet
     *         contains
     */
    public int getSpriteCount();
}
