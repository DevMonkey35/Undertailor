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

import com.badlogic.gdx.graphics.Color;

import me.scarlet.undertailor.audio.SoundFactory.Sound;

import java.util.ArrayList;
import java.util.List;

/**
 * A segment of text with a number of properties within a
 * parent {@link Text} object.
 */
public class TextComponent {

    /**
     * Builder-type class for building {@link TextComponent}
     * s.
     */
    public static class Builder {

        TextComponent component;

        public Builder() {
            this(new TextComponent());
        }

        protected Builder(TextComponent target) {
            this.component = target;
        }

        /**
         * Sets the text held by the {@link TextComponent}.
         * 
         * @param text the text for the component
         * 
         * @return this Builder
         */
        public Builder setText(String text) {
            this.component.text = text;
            return this;
        }

        /**
         * Sets the font held by the {@link TextComponent}.
         * 
         * @param font the font for the component
         * 
         * @return this Builder
         */
        public Builder setFont(Font font) {
            this.component.font = font;
            return this;
        }

        /**
         * Sets the color used by the {@link TextComponent}.
         * 
         * @param color the color for the component
         * 
         * @return this Builder
         */
        public Builder setColor(Color color) {
            this.component.color = color;
            return this;
        }

        /**
         * Sets the sound associated with the
         * {@link TextComponent}.
         * 
         * @param sound the sound for the component
         * 
         * @return this Builder
         */
        public Builder setSound(Sound sound) {
            this.component.sound = sound;
            return this;
        }

        /**
         * Add {@link TextStyle}s altering how the text of
         * the {@link TextComponent} is rendered.
         * 
         * @param styles TextStyles to add
         * 
         * @return this Builder
         */
        public Builder addStyles(TextStyle... styles) {
            for (TextStyle style : styles) {
                this.component.styles.add(style);
            }

            return this;
        }

        /**
         * Sets the speed value associated with the
         * {@link TextComponent}.
         * 
         * @param speed the speed value of the component
         * 
         * @return this Builder
         */
        public Builder setSpeed(float speed) {
            this.component.speed = speed;
            return this;
        }

        /**
         * Sets the segment size value associated with the
         * {@link TextComponent}.
         * 
         * @param size the segment size value of the
         *        component
         *
         * @return this Builder
         */
        public Builder setSegmentSize(int size) {
            this.component.segmentSize = size;
            return this;
        }

        /**
         * Sets the delay value associated with the
         * {@link TextComponent}.
         * 
         * @param delay the delay value of the component
         * 
         * @return this Builder
         */
        public Builder setDelay(float delay) {
            this.component.delay = delay;
            return this;
        }

        /**
         * Builds the {@link TextComponent} with the
         * parameters assigned to this {@link Builder} and
         * returns it.
         * 
         * @return a new TextComponent
         */
        public TextComponent build() {
            if (this.component.text == null || this.component.text.isEmpty()) {
                throw new IllegalArgumentException("Component text cannot be null or empty");
            }

            if (this.component.delay < 0F)
                this.component.delay = 0F;

            return this.component;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    protected Text parent;

    String text;
    Font font;
    Color color;
    Sound sound;
    List<TextStyle> styles;
    float speed; // characters per second
    int segmentSize; // "flame" < size 1 = "f", size 2 = "fl"
    float delay; // delay before the component is played

    protected TextComponent() {
        this.text = "";
        this.font = null;
        this.color = null;
        this.sound = null;
        this.styles = new ArrayList<>();
        this.speed = -1;
        this.segmentSize = -1;
        this.delay = 0F;
    }

    /**
     * Returns the text associated with this
     * {@link TextComponent}.
     * 
     * @return this component's text
     */
    public String getText() {
        return this.text;
    }

    /**
     * Returns the font associated with this
     * {@link TextComponent}, or the font of its parent
     * {@link Text} object if one was not assigned.
     * 
     * @return the Font for this component
     */
    public Font getFont() {
        if (this.font == null && parent != null) {
            return this.parent.getFont();
        }

        return this.font;
    }

    /**
     * Returns the color associated with this
     * {@link TextComponent}, or the color of its parent
     * {@link Text} object if one was not assigned.
     * 
     * @return the Color for this component
     */
    public Color getColor() {
        if (this.color == null && parent != null) {
            return this.parent.getColor();
        }

        return this.color;
    }

    /**
     * Returns the sound associated with this
     * {@link TextComponent}, or the sound of its parent
     * {@link Text} object if one was not assigned.
     * 
     * @return the Sound for this component
     */
    public Sound getSound() {
        if (this.sound == null && parent != null) {
            return this.parent.getSound();
        }

        return this.sound;
    }

    /**
     * Returns the list of {@link TextStyle}s in the order
     * of applicance assigned to this {@link TextComponent},
     * or the list of its parent {@link Text} object if no
     * styles were assigned.
     * 
     * @return the TextStyles for this component
     */
    public List<TextStyle> getStyles() {
        if (this.styles.isEmpty() && parent != null) {
            return parent.getStyles();
        }

        return this.styles;
    }

    /**
     * Returns the text speed value, in characters per
     * second, associated with this {@link TextComponent},
     * or the value of its parent {@link Text} object if one
     * was not assigned.
     * 
     * @return the text speed for this component
     */
    public float getTextSpeed() {
        if (this.speed <= -1 && parent != null) {
            return this.parent.getTextSpeed();
        }

        return this.speed;
    }

    /**
     * Returns the segment size value associated with this
     * {@link TextComponent}, or the value of its parent
     * {@link Text} object if one was not assigned.
     * 
     * @return the segment size value for this component
     */
    public int getSegmentSize() {
        if (this.segmentSize <= -1 && parent != null) {
            return this.parent.getSegmentSize();
        }

        return this.segmentSize;
    }

    /**
     * Returns the delay value, in seconds, associated with
     * this {@link TextComponent}, or the value of its
     * parent {@link Text} object if one was not assigned.
     * 
     * @return the delay value for this component
     */
    public float getDelay() {
        return this.delay;
    }
}
