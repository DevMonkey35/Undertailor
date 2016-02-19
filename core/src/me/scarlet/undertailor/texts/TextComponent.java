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
    
    protected TextComponent parent;
    protected SoundWrapper textSound;
    private String text;
    protected Color color;
    protected Style style;
    protected Font font;
    protected Integer speed;       // segments per second?
    protected Integer segmentSize; // how many characters to play at a time
    protected Float wait;          // delay between text components
    
    public static final int DEFAULT_SPEED = 35;
    
    public static TextComponent of(String text, Font font) {
        return new TextComponent(text, font);
    }
    
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
    }
    
    public String getText() {
        return text;
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
    
    public SoundWrapper getSound() {
        if(textSound == null && parent != null) {
            return parent.textSound;
        }
        
        return textSound;
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
    
    public Style getStyle() {
        if(style == null && parent != null) {
            return parent.style;
        }
        
        return style;
    }
    
    public Font getFont() {
        if(font == null && parent != null) {
            return parent.font;
        }
        
        return font;
    }
    
    public float getDelay() {
        if(wait == null) {
            if(parent != null && parent.wait != null) {
                return parent.wait;
            }
            
            return 0F;
        }
        
        return wait;
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
    
    public TextComponent substring(int start) {
        return substring(start, this.text.length());
    }
    
    public TextComponent substring(int start, int end) {
        return new TextComponent(text.substring(start, end), font, style, color, textSound, speed, segmentSize, wait);
    }
}
