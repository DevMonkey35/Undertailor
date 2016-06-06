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
import com.badlogic.gdx.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;
import me.scarlet.undertailor.gfx.spritesheet.Sprite;
import me.scarlet.undertailor.gfx.spritesheet.Sprite.SpriteMeta;
import me.scarlet.undertailor.gfx.text.TextStyle.DisplayMeta;
import me.scarlet.undertailor.gfx.text.parse.ParsedText;
import me.scarlet.undertailor.util.Pair;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/**
 * A parent {@link TextComponent} component withholding a
 * number of child component modules to resolve to a full
 * batch of customizable segments.
 */
public class Text extends TextComponent implements Renderable {

    /**
     * Builder-type class for building {@link Text}s.
     */
    public static class Builder extends TextComponent.Builder {

        public Builder() {
            super(new Text());
        }

        /**
         * {@inheritDoc}
         * 
         * <p>For the {@link Text} builder, this method does
         * nothing, as the text for a Text object is a
         * combination of all of its child TextComponents'
         * texts.</p>
         */
        @Override
        public Builder setText(String text) {
            return this;
        } // no function

        /**
         * Adds the provided {@link TextComponent}s to the
         * parent {@link Text} object.
         * 
         * @param components the components to add
         * 
         * @return this Builder
         */
        public Builder addComponents(TextComponent... components) {
            for (TextComponent component : components) {
                this.component.text += component.text;
                component.parent = (Text) this.component;

                Text text = ((Text) this.component);
                if (text.components.isEmpty()) {
                    text.components.put(0, component);
                } else {
                    int last = text.components.lastKey();
                    int map = last + text.components.get(last).text.length();
                    text.components.put(map, component);
                }
            }

            return this;
        }

        /**
         * Builds the {@link Text} with the assigned
         * parameters and returns it.
         * 
         * <p>The parent Text object must have a
         * {@link Font} assigned to draw with, otherwise
         * this method resolves to an
         * IllegalArgumentException.</p>
         * 
         * @return a new Text object
         * 
         * @throws IllegalArgumentException if the Text
         *         object to build would contain no
         *         components or would not have a Font to
         *         draw with
         */
        public Text build() {
            if (((Text) this.component).components.isEmpty()) {
                throw new IllegalArgumentException("Cannot create text with no components");
            }

            if (this.component.font == null) {
                throw new IllegalArgumentException("Text must have a base font");
            }

            if (this.component.color == null) {
                this.component.color = Color.WHITE;
            }

            ((Text) this.component).refreshValues();
            return (Text) this.component;
        }
    }

    private static final DisplayMeta DISPLAY_META;
    static final Logger logger = LoggerFactory.getLogger(Text.class);

    static {
        DISPLAY_META = new DisplayMeta();
    }

    /**
     * Generates a clean {@link DisplayMeta} object to pass
     * to {@link TextStyle}s.
     * 
     * @return a clean DisplayMeta
     */
    static DisplayMeta generateDisplayMeta() {
        DISPLAY_META.reset();
        return DISPLAY_META;
    }

    /**
     * Returns a new {@link Builder} instance to build a new
     * {@link Text}.
     * 
     * @return a Text Builder
     */
    public static Text.Builder builder() {
        return new Text.Builder();
    }

    /**
     * Generates a new {@link Text} object based on the
     * provided parameterized strings.
     * 
     * @param tailor the current Undertailor instance
     * @param baseParams the parameterized string containing
     *        the properties of the base Text
     * @param components the parameterized string containing
     *        all components to add
     * 
     * @return a new Text object
     */
    public static Text of(Undertailor tailor, String baseParams, String components) {
        Builder builder = Text.builder();
        if (baseParams != null && !baseParams.trim().isEmpty()) {
            TextComponent.applyParameters(builder, tailor,
                ParsedText.of(baseParams).getPieces().get(0));
        }

        ParsedText.of(components).getPieces().forEach(piece -> {
            builder.addComponents(TextComponent.of(tailor, piece));
        });

        return builder.build();
    }

    // ---------------- object ----------------

    private int lineCount;
    private Transform transform;
    private Pair<Float> spaceTaken;
    private long instantiationTime;
    private Pair<Integer> stringBounds;
    private TreeMap<Integer, TextComponent> components; // integer marks start index of the component

    // objects held so we don't spam new objects
    private Pair<Integer> m_valuePair;
    private Transform m_drawnTransform;

    private Text() {
        this.components = new TreeMap<>((Integer i1, Integer i2) -> {
            return Integer.compare(i1, i2);
        });

        this.instantiationTime = TimeUtils.millis();
        this.stringBounds = new Pair<>(0, 0);
        this.spaceTaken = new Pair<>(-1F, -1F);
        this.transform = new Transform();

        this.m_valuePair = new Pair<>();
        this.m_drawnTransform = new Transform();
    }

    // ---------------- abstract method implementation ----------------

    @Override
    public Transform getTransform() {
        return this.transform;
    }

    @Override
    public void setTransform(Transform transform) {
        if (transform == null) {
            this.transform = Transform.DUMMY.copy(this.transform);
        } else {
            this.transform = transform;
        }
    }

    // method-specific variables because stupid scope rules
    float dX, dY, spacing;

    @Override
    public void draw(float x, float y, Transform transform) {
        dX = x;
        dY = y;

        this.processCharacters((localIndex, component) -> {
            char character = component.getText().charAt(localIndex.getSecond());

            if (character == ' ') {
                dX += font.getSpaceLength() * transform.getScaleX();
            } else if (character == '\n') {
                dX = x;
                dY -= font.getLineSize() * transform.getScaleY();
            } else {
                Sprite sprite = this.font.getCharacterSprite(character);
                SpriteMeta sMeta = sprite.getMeta();
                Pair<Float> letterSpacing = font.getLetterSpacing(character);
                this.m_drawnTransform = this.transform.copy(this.m_drawnTransform);

                DisplayMeta dMeta = Text.generateDisplayMeta();
                if (!component.getStyles().isEmpty()) {
                    component.getStyles().forEach(style -> {
                        style.apply(dMeta, this.instantiationTime, character,
                            localIndex.getFirst() + localIndex.getSecond(),
                            this.getText().length());
                    });

                    this.m_drawnTransform
                        .setScaleX(this.m_drawnTransform.getScaleX() * dMeta.scaleX);
                    this.m_drawnTransform
                        .setScaleY(this.m_drawnTransform.getScaleY() * dMeta.scaleY);
                    this.m_drawnTransform.addRotation(dMeta.rotation);
                }

                if (letterSpacing.getFirst() > spacing) {
                    dX += (letterSpacing.getFirst() - spacing) * m_drawnTransform.getScaleX();
                }

                font.getRenderer().setBatchColor(component.getColor());
                sprite.draw(dX + ((sMeta.originX + dMeta.offX) * (m_drawnTransform.getScaleX())),
                    dY + ((sMeta.originY + dMeta.offY) * (m_drawnTransform.getScaleY())),
                    m_drawnTransform);

                spacing = letterSpacing.getSecond();
                dX += (sprite.getTextureRegion().getRegionWidth() + spacing)
                    * m_drawnTransform.getScaleX();
            }
        });
    }

    // ---------------- getters: immutable/calculated ----------------

    /**
     * Returns the text of this {@link Text} after applying
     * its string bounds.
     * 
     * @return this Text's bounded text
     * 
     * @see #getStringBounds()
     */
    public String getBoundedText() {
        return this.getText().substring(
            this.getStringBounds().getFirst() == -1 ? 0 : this.getStringBounds().getFirst(),
            this.getStringBounds().getSecond() == -1 ? this.getText().length()
                : this.getStringBounds().getSecond());
    }

    /**
     * Returns all the child {@link TextComponent}
     * components contained within this {@link Text} object.
     * 
     * @return a Collection of this Text's TextComponents
     */
    public Collection<TextComponent> getComponents() {
        return this.components.values();
    }

    /**
     * Returns the space taken by this {@link Text} if it
     * were to be drawn with no transformations. The first
     * value of the pair is the width, the second being the
     * height.
     * 
     * <p>String bounds affect this value.</p>
     * 
     * @return the space taken by this Text with no
     *         transform
     */
    public Pair<Float> getSpaceTaken() {
        return this.spaceTaken;
    }

    /**
     * Returns the count of lines this {@link Text} has
     * <code>(text.trim().split("\n").length)</code>.
     * 
     * <p>String bounds affect this value.</p>
     * 
     * @return the total lines of text this Text object has
     */
    public int getLineCount() {
        return this.lineCount;
    }

    // ---------------- g/s text parameters ----------------

    /**
     * Returns the substring boundaries defining which
     * characters of this {@link Text} object should be
     * considered for drawing.
     * 
     * @return the substring boundaries for this Text
     * 
     * @see #substring(int, int)
     */
    public Pair<Integer> getStringBounds() {
        return this.stringBounds;
    }

    /**
     * Sets the substring boundaries defining which
     * characters of this {@link Text} object should be
     * considered for drawing.
     * 
     * <p>This method is functionally equivalent to
     * substringing the {@link Text} without creating a new
     * instance. If a new instance is needed, see
     * {@link #substring(int, int)}.</p>
     * 
     * @param first the first boundary
     * @param second the second boundary
     * 
     * @see #substring(int, int)
     */
    public void setStringBounds(int first, int second) {
        if (first > second) {
            throw new IllegalArgumentException("First bound cannot be greater than second bound");
        }

        this.stringBounds.setItems(first, second);
        this.refreshValues();
    }

    // ---------------- functional --------------------

    /**
     * Returns the {@link TextComponent} holding the given
     * character at the specified index.
     * 
     * @param index the index of the character in the scope
     *        of the full text
     * 
     * @return the TextComponent owning the character
     */
    public TextComponent getTextComponentAt(int index) {
        return this.components.get(this.components.lowerKey(index + 1));
    }

    /**
     * Substrings the contents of this {@link Text} object
     * into a new instance (NYI).
     * 
     * @param start the first boundary
     * @param end the second boundary
     * 
     * @return a duplicate Text object including only the
     *         characters bounded by the provided indices
     * 
     * @deprecated not yet implemented
     */
    @Deprecated
    public Text substring(int start, int end) {
        return null; // TODO
    }

    // ---------------- internal methods ----------------

    /**
     * Internal method.
     * 
     * <p>Responsible for DRYing the process of iterating
     * through each character within the string bounds.</p>
     * 
     * <p>The consumer takes a pair of integers and a
     * TextComponent. The first integer is the index of the
     * component's first character in the entire text. The
     * second integer is the index of the current character,
     * local to the scope of the current TextComponent.</p>
     * 
     * <p>Assume a component with the text content <code>
     * "Hello, world!"</code>. To point to the character
     * <code>o</code> at index 4, the consumer is provided
     * with 4 and the component holding the text
     * <code>(component.getText().charAt(4) =
     * 'o')</code></p>
     * 
     * @param consumer the consumer that processes each
     *        character
     */
    private void processCharacters(BiConsumer<Pair<Integer>, TextComponent> consumer) {
        int boundL = this.getStringBounds().getFirst();
        int boundR = this.getStringBounds().getSecond();

        if(boundL < 0) {
            boundL = this.getText().length() - Math.abs(boundL);
        }

        if(boundR < 0) {
            boundR = this.getText().length() - Math.abs(boundR);
        }

        TextComponent first = boundL == 0 ? null : this.getTextComponentAt(boundL);
        TextComponent last = boundR == 0 ? null : this.getTextComponentAt(boundR);
        for (Entry<Integer, TextComponent> entry : this.components.entrySet()) {
            if (first != null) { // if first is null, we've found the first component to iterate through
                if (entry.getValue() != first) {
                    continue;
                }

                first = null;
            }

            int localIndex = 0;
            if (boundL != -1 && entry.getKey() < boundL) {
                localIndex += boundL - entry.getKey();
            }

            this.m_valuePair.setFirst(entry.getKey());
            for (int ind = localIndex; ind < entry.getValue().getText().length(); ind++) {
                this.m_valuePair.setSecond(ind);
                consumer.accept(this.m_valuePair, entry.getValue());

                if (boundR != -1 && entry.getKey() + ind >= boundR) {
                    return;
                }
            }

            if (entry.getValue() == last) {
                return;
            }
        }
    }

    // method-specific variables cuz stupid scope rules
    // float dX, dY; (reused)

    /**
     * Internal method.
     * 
     * <p>Convenience method refreshing all pre-calculated
     * values.</p>
     */
    private void refreshValues() {
        this.calculateSpace();
        this.calculateLines();
    }

    /**
     * Internal method.
     * 
     * <p>Calculates the value returned by
     * {@link #getSpaceTaken()}.</p>
     */
    private void calculateSpace() {
        this.spaceTaken.setItems(0F, 0F);
        dX = 0;
        dY = font.getLineSize();

        this.processCharacters((localIndex, component) -> {
            char character = component.getText().charAt(localIndex.getSecond());

            if (character == ' ') {
                dX += font.getSpaceLength();
            } else if (character == '\n') {
                if (this.spaceTaken.getFirst() < dX)
                    this.spaceTaken.setFirst(dX);
                dX = 0;
                dY += font.getLineSize();
            } else {
                Sprite sprite = this.font.getCharacterSprite(character);
                Pair<Float> letterSpacing = font.getLetterSpacing(character);

                if (letterSpacing.getFirst() > spacing) {
                    dX += letterSpacing.getFirst() - spacing;
                }

                spacing = letterSpacing.getSecond();
                dX += (sprite.getTextureRegion().getRegionWidth() + spacing);
            }
        });

        if (this.spaceTaken.getFirst() < dX)
            this.spaceTaken.setFirst(dX);
        this.spaceTaken.setSecond(dY);
    }

    /**
     * Internal method.
     * 
     * <p>Calculates the value returned by
     * {@link #getLineCount()}.</p>
     */
    private void calculateLines() {
        if (this.getText().trim().isEmpty()) {
            this.lineCount = 0;
        } else {
            this.lineCount = this.getBoundedText().trim().split("\n").length;
        }
    }
}
