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

package me.scarlet.undertailor.gfx.text;

/**
 * Skeleton implementation for objects used to decorate the
 * appearance of characters within {@link TextComponent}s.
 */
public interface TextStyle {

    /**
     * Data bag containing the visual modifications to a
     * specific character in a {@link TextComponent}.
     */
    public static class DisplayMeta {

        public float offX, offY, scaleX, scaleY, rotation;

        public DisplayMeta() {
            this.reset();
        }

        /**
         * Resets the values within this {@link DisplayMeta}
         * to their defaults.
         */
        public void reset() {
            this.offX = 0;
            this.offY = 0;
            this.scaleX = 1;
            this.scaleY = 1;
            this.rotation = 0;
        }
    }

    /**
     * Modifies a {@link DisplayMeta} instance in order to
     * provide visual effects to a specific character in a
     * {@link TextComponent}.
     * 
     * <p>Implementations of this method should consider the
     * idea that they are <strong>modifying</strong> a
     * DisplayMeta instance, and should not assume its
     * current values are 0 and/or that it is the only style
     * modifying the appearance of the target character.</p>
     * 
     * @param meta the DisplayMeta instance to modify
     * @param time an unspecified time value, in
     *        milliseconds
     * @param character the current target character
     * @param charIndex the index of the current target
     *        character in the scope of the entire
     *        {@link Text} the owning component belongs to
     * @param textLength the length of the entire Text that
     *        the owning component belongs to
     */
    void apply(DisplayMeta meta, long time, char character, int charIndex, int textLength);
}
