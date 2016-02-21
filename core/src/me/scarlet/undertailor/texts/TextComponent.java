/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor.texts;

import com.badlogic.gdx.graphics.Color;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.audio.SoundWrapper;
import me.scarlet.undertailor.texts.parse.ParsedText;
import me.scarlet.undertailor.texts.parse.TextParam;
import me.scarlet.undertailor.texts.parse.TextPiece;

import java.util.Map;

public class TextComponent {
    
    public static class DisplayMeta {
        
        public static DisplayMeta defaults() {
            return new DisplayMeta();
        }
        
        public float offX, offY, scaleX, scaleY;
        public Color color;
        
        public DisplayMeta() {
            this(0, 0, 1.0F, 1.0F, null);
        }
        
        public DisplayMeta(float offX, float offY, float scaleX, float scaleY, Color color) {
            this.offX = offX;
            this.offY = offY;
            this.scaleX = scaleX;
            this.scaleY = scaleY;
            this.color = color;
        }
        
        public String toString() {
            return "[" + offX + ", " + offY + ", " + scaleX + ", " + scaleY + "]";
        }
    }
    
    public static class Builder {
        
        protected Font font;
        protected float delay;
        protected String text;
        protected Color color;
        protected Style style;
        protected SoundWrapper textSound;
        protected int speed, segmentSize;
        
        public Builder() {
            this.text = null;
            this.font = null;
            this.style = null;
            this.textSound = null;
            
            this.speed = TextComponent.DEFAULT_SPEED;
            this.delay = 0F;
            this.segmentSize = 1;
            this.color = Color.WHITE;
        }
        
        public Builder setText(String text) {
            this.text = text;
            return this;
        }
        
        public Builder setColor(Color color) {
            this.color = color;
            if(this.color == null) {
                this.color = Color.WHITE;
            } else {
                this.color.a = 1F;
            }
            
            return this;
        }
        
        public Builder setStyle(Style style) {
            this.style = style;
            return this;
        }
        
        public Builder setFont(Font font) {
            this.font = font;
            return this;
        }
        
        public Builder setSpeed(int speed) {
            this.speed = speed;
            return this;
        }
        
        public Builder setSegmentSize(int segmentSize) {
            this.segmentSize = segmentSize;
            return this;
        }
        
        public Builder setDelay(float delay) {
            this.delay = delay;
            return this;
        }
        
        public Builder setTextSound(SoundWrapper textSound) {
            this.textSound = textSound;
            return this;
        }
        
        public TextComponent build() {
            TextComponent component = new TextComponent();
            component.text = this.text;
            component.font = this.font;
            component.color = this.color;
            component.style = this.style;
            component.speed = this.speed;
            component.delay = this.delay;
            component.textSound = this.textSound;
            component.segmentSize = this.segmentSize;
            
            return component;
        }
    }
    
    public static final String PARAM_ERROR_MESSAGE = "bad parameter value \"%s\" for key %s";
    
    public static TextComponent.Builder applyParameters(TextComponent.Builder builder, Map<TextParam, String> mapping) {
        mapping.keySet().forEach(param -> {
            String valueString = mapping.get(param);
            if(valueString.trim().isEmpty()) {
                switch(param) {
                    case COLOR:
                        builder.setColor(null);
                        break;
                    case DELAY:
                        builder.setDelay(-1F);
                        break;
                    case FONT:
                        builder.setFont(null);
                        break;
                    case SEGMENTSIZE:
                        builder.setSegmentSize(-1);
                        break;
                    case SOUND:
                        builder.setTextSound(null);
                        break;
                    case SPEED:
                        builder.setSpeed(-1);
                        break;
                    case STYLE:
                        builder.setStyle(null);
                        break;
                    default:
                        break;
                    
                }
            } else {
                switch(param) {
                    case COLOR:
                        String[] split = valueString.split(",");
                        Color color;
                        if(split.length == 1) { // hex
                            try {
                                color = Color.valueOf(split[0]);
                            } catch(Exception e) {
                                throw new IllegalArgumentException(String.format(PARAM_ERROR_MESSAGE, valueString, "color") + " (bad hex string)");
                            }
                        } else if(split.length == 3) { // r, g, b
                            try {
                                color = new Color(
                                        Integer.parseInt(split[0]) / 255.0F,
                                        Integer.parseInt(split[1]) / 255.0F,
                                        Integer.parseInt(split[2]) / 255.0F,
                                        1.0F);
                            } catch(Exception e) {
                                throw new IllegalArgumentException(String.format(PARAM_ERROR_MESSAGE, valueString, "color") + " (bad rgb values)");
                            }
                        } else {
                            throw new IllegalArgumentException(String.format(PARAM_ERROR_MESSAGE, valueString, "color"));
                        }
                        
                        builder.setColor(color);
                        break;
                    case DELAY:
                        try {
                            float time = Float.parseFloat(valueString);
                            builder.setDelay(time);
                        } catch(Exception e) {
                            throw new IllegalArgumentException(String.format(PARAM_ERROR_MESSAGE, valueString, "delay") + " (bad number value)");
                        }
                        
                        break;
                    case FONT:
                        Font font = Undertailor.getFontManager().getFont(valueString);
                        if(font == null) {
                            throw new IllegalArgumentException(String.format(PARAM_ERROR_MESSAGE, valueString, "font") + " (non-existing font)");
                        }
                        
                        builder.setFont(font);
                        break;
                    case SEGMENTSIZE:
                        try {
                            int segsize = Integer.parseInt(valueString);
                            builder.setSegmentSize(segsize);
                        } catch(Exception e) {
                            throw new IllegalArgumentException(String.format(PARAM_ERROR_MESSAGE, valueString, "segmentsize") + " (bad integer value)");
                        }
                        
                        break;
                    case SOUND:
                        SoundWrapper sound = Undertailor.getAudioManager().getSoundManager().getResource(valueString);
                        if(sound == null) {
                            throw new IllegalArgumentException(String.format(PARAM_ERROR_MESSAGE, valueString, "sound") + " (non-existing sound)");
                        }
                        
                        builder.setTextSound(sound);
                        break;
                    case SPEED:
                        try {
                            int speed = Integer.parseInt(valueString);
                            builder.setSpeed(speed);
                        } catch(Exception e) {
                            throw new IllegalArgumentException(String.format(PARAM_ERROR_MESSAGE, valueString, "speed") + " (bad integer value)");
                        }
                        
                        break;
                    case STYLE:
                        Style style = Undertailor.getStyleManager().getStyle(valueString);
                        if(style == null) {
                            throw new IllegalArgumentException(String.format(PARAM_ERROR_MESSAGE, valueString, "style") + " (non-existing style)");
                        }
                        
                        builder.setStyle(style);
                        break;
                    case UNDEFINED:
                    default:
                        break;
                }
            }
        });
        
        return builder;
    }
    
    public static TextComponent fromString(String str) {
        if(str.trim().length() == 0) {
            throw new IllegalArgumentException("cannot parse empty string");
        }
        
        ParsedText components = ParsedText.of(str);
        
        if(components.getPieces().isEmpty()) {
            throw new IllegalArgumentException("cannot parse empty string");
        }
        
        return TextComponent.fromPiece(components.getPieces().get(0)); // only the first
    }
    
    public static TextComponent fromPiece(TextPiece piece) {
        TextComponent.Builder builder = TextComponent.builder();
        TextComponent.applyParameters(builder, piece.getParams());
        builder.setText(piece.getMessage());
        return builder.build();
    }
    
    protected TextComponent parent;
    protected SoundWrapper textSound;
    private String text;
    protected Color color;
    protected Style style;
    protected Font font;
    protected Integer speed;       // how many characters to play in a second
    protected Integer segmentSize; // characters in one segment?
    protected Float delay;         // delay between text components
    
    public static final int DEFAULT_SPEED = 30;
    
    public static Builder builder() {
        return new Builder();
    }
    
    protected TextComponent() {}
    
    /*
    public TextComponent(String text, Font font) {
        this(text, font, null);
    }
    
    public TextComponent(String text, Font font, Style style) {
        this(text, font, style, null);
    }
    public TextComponent(String text, Font font, Style style, Color color) {
        this(text, font, style, color, null);
    }
    
    public TextComponent(String text, Font font, Style style, Color color, SoundWrapper textSound) {
        this(text, font, style, color, textSound, DEFAULT_SPEED);
    }
    
    public TextComponent(String text, Font font, Style style, Color color, SoundWrapper textSound, Integer speed) {
        this(text, font, style, color, textSound, speed, 1);
    }
    
    public TextComponent(String text, Font font, Style style, Color color, SoundWrapper textSound, Integer speed, Integer segmentSize) {
        this(text, font, style, color, textSound, speed, segmentSize, 0F);
    }
    
    public TextComponent(String text, Font font, Style style, Color color, SoundWrapper textSound, Integer speed, Integer segmentSize, Float wait) {
        this.text = text;
        this.textSound =  textSound;
        this.color = color;
        this.speed = speed;
        this.wait = wait;
        this.style = style;
        this.font = font;
        this.segmentSize = segmentSize;
        
        if(speed != null && speed <= 0) {
            this.speed = DEFAULT_SPEED;
        }
        
        if(segmentSize != null && segmentSize <= 0) {
            this.segmentSize = 1;
        }
        
        if(wait != null && wait < 0) {
            this.wait = 0F;
        }
    }*/
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Color getColor() {
        if(color == null) {
            if(parent != null && parent.color != null) {
                return parent.color;
            }
            
            return Color.WHITE;
        }
        
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public SoundWrapper getSound() {
        if(textSound == null && parent != null) {
            return parent.textSound;
        }
        
        return textSound;
    }
    
    public void setSound(SoundWrapper textSound) {
        this.textSound = textSound;
    }
    
    public int getSpeed() {
        if(speed == null || speed <= 0) {
            if(parent != null && parent.speed != null) {
                return parent.speed;
            }
            
            return DEFAULT_SPEED;
        }
        
        return speed;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed < 1 ? 1 : speed;
    }
    
    public Style getStyle() {
        if(style == null && parent != null) {
            return parent.style;
        }
        
        return style;
    }
    
    public void setStyle(Style style) {
        this.style = style;
    }
    
    public Font getFont() {
        if(font == null && parent != null) {
            return parent.font;
        }
        
        return font;
    }
    
    public void setFont(Font font) {
        this.font = font;
    }
    
    public float getDelay() {
        if(delay == null || delay < 0) {
            if(parent != null && parent.delay != null) {
                return parent.delay;
            }
            
            return 0F;
        }
        
        return delay;
    }
    
    public void setDelay(float delay) {
        this.delay = delay < 0 ? 0 : delay;
    }
    
    public int getSegmentSize() {
        if(segmentSize == null || segmentSize <= 0) {
            if(parent != null && parent.segmentSize != null) {
                return parent.segmentSize;
            }
            
            return 1;
        }
        
        return segmentSize;
    }
    
    public void setSegmentSize(int segmentSize) {
        this.segmentSize = segmentSize < 1 ? 1 : segmentSize;
    }
    
    public TextComponent substring(int start) {
        return substring(start, this.text.length());
    }
    
    public TextComponent substring(int start, int end) {
        return TextComponent.builder()
                .setText(text.substring(start, end))
                .setFont(font)
                .setStyle(style)
                .setColor(color)
                .setTextSound(textSound)
                .setSpeed(speed)
                .setSegmentSize(segmentSize)
                .setDelay(delay)
                .build();
    }
}
