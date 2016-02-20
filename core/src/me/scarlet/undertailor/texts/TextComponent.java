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
import me.scarlet.undertailor.audio.SoundWrapper;

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
            
            this.speed = 35;
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
    
    protected TextComponent parent;
    protected SoundWrapper textSound;
    private String text;
    protected Color color;
    protected Style style;
    protected Font font;
    protected Integer speed;       // how many characters to play in a second
    protected Integer segmentSize; // characters in one segment?
    protected Float delay;         // delay between text components
    
    public static final int DEFAULT_SPEED = 35;
    
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
        if(speed == null) {
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
        if(delay == null) {
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
        if(segmentSize == null) {
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
