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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.ConfigurationException;
import me.scarlet.undertailor.exception.TextureTilingException;
import me.scarlet.undertailor.gfx.Sprite;
import me.scarlet.undertailor.gfx.Sprite.SpriteMeta;
import me.scarlet.undertailor.gfx.SpriteSheet;
import me.scarlet.undertailor.gfx.SpriteSheet.SpriteSheetMeta;
import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;
import me.scarlet.undertailor.util.ConfigurateUtil;
import me.scarlet.undertailor.util.MultiRenderer;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Font {
    
    public static class FontData {
        
        public static class CharMeta {
            
            private Integer offX, offY, boundWrapX, boundWrapY;
            
            public CharMeta() {
                this.setValues(new Integer[] {null, null, null, null});
            }
            
            public static CharMeta fromConfig(ConfigurationNode root) {
                return new CharMeta(ConfigurateUtil.processIntegerArray(root, new Integer[] {null, null, null, null}));
            }
            
            public CharMeta(Integer[] values) {
                this.setValues(values);
            }
            
            public CharMeta(Integer offX, Integer offY) {
                this(offX, offY, null, null);
            }
            
            public CharMeta(Integer offX, Integer offY, Integer boundWrapX, Integer boundWrapY) {
                this.offX = offX;
                this.offY = offY;
                this.boundWrapX = boundWrapX;
                this.boundWrapY = boundWrapY;
            }
            
            public int getOffsetX() {
                return offX == null ? 0 : offX;
            }
            
            public int getOffsetY() {
                return offY == null ? 0 : offY;
            }
            
            public int getBoundWrapX() {
                return boundWrapX == null ? 0 : boundWrapX;
            }
            
            public int getBoundWrapY() {
                return boundWrapY == null ? 0 : boundWrapY;
            }
            
            public int[] values() {
                return new int[] {getOffsetX(), getOffsetY(), getBoundWrapX(), getBoundWrapY()};
            }
            
            public Integer[] rawValues() {
                return new Integer[] {offX, offY, boundWrapX, boundWrapY};
            }
            
            public void setValues(Integer[] values) {
                this.offX = values[0];
                this.offY = values[1];
                this.boundWrapX = values[2];
                this.boundWrapY = values[3];
            }
            
            public CharMeta merge(CharMeta otherMeta) {
                Integer[] values = otherMeta.rawValues();
                Integer[] set = new Integer[values.length];
                for(int i = 0; i < set.length; i++) {
                    if(values[i] != null) {
                        set[i] = values[i];
                    } else {
                        set[i] = this.values()[i];
                    }
                }
                
                return new CharMeta(set);
            }
            
            public SpriteMeta asSpriteMeta(int ySize) {
                return new SpriteMeta(0, -1 * ySize, getOffsetX(), getOffsetY() + ySize, getBoundWrapX(), getBoundWrapY());
            }
            
            @Override
            public String toString() {
                return "[" + getOffsetX() + ", " + getOffsetY() + ", " + getBoundWrapX() + ", " + getBoundWrapY() + "]";
            }
        }
        
        public static FontData fromConfig(String name, ConfigurationNode node) {
            FontData data = new FontData();
            ConfigurationNode root = node.getNode("font");
            try {
                data.fontName = name;
                data.x = ConfigurateUtil.processInt(root.getNode("gridSizeX"), null);
                data.y = ConfigurateUtil.processInt(root.getNode("gridSizeY"), null);
                data.space = ConfigurateUtil.processInt(root.getNode("spaceSize"), null);
                data.spacing = ConfigurateUtil.processInt(root.getNode("letterSpacing"), null);
                data.characterList = ConfigurateUtil.processString(root.getNode("charList"), null);
                
                if(!root.getNode("globalMeta").isVirtual()) {
                    data.globalMeta = CharMeta.fromConfig(root.getNode("globalMeta"));
                } else {
                    data.globalMeta = new CharMeta();
                }
                
                data.charMeta = new HashMap<>();
                for(Entry<Object, ? extends ConfigurationNode> entry: root.getNode("meta").getChildrenMap().entrySet()) {
                    if(data.charMeta.containsKey(entry.getKey())) {
                        data.charMeta.put(entry.getKey().toString(), data.charMeta.get(entry.getKey().toString()).merge(CharMeta.fromConfig(entry.getValue())));
                    } else {
                        data.charMeta.put(entry.getKey().toString(), CharMeta.fromConfig(entry.getValue()));
                    }
                }
                
                return data;
            } catch(RuntimeException e) {
                e.printStackTrace();
                return null;
            }
        }
        
        private String fontName;
        private CharMeta globalMeta;
        private String characterList;
        private Map<String, CharMeta> charMeta;
        private int x = 1, y = 1;
        private int spacing = -1; // pixels between letters
        private int space = -1; // pixels that count as a space
        
        private FontData() {}
        
        public String getName() {
            return fontName;
        }
        
        public CharMeta getFontMeta() {
            return globalMeta;
        }
        
        public CharMeta getCharacterMeta(char ch) {
            Set<CharMeta> collectedMeta = new HashSet<CharMeta>();
            for(Entry<String, CharMeta> entry : charMeta.entrySet()) {
                if(entry.getKey().indexOf(ch) != -1) {
                   collectedMeta.add(entry.getValue());
                }
            }
            
            if(!collectedMeta.isEmpty()) {
                CharMeta compiled = new CharMeta();
                for(CharMeta meta : collectedMeta) {
                    compiled = compiled.merge(meta);
                }
                
                return compiled;
            }
            
            return getFontMeta();
        }
        
        public int getLetterSpacing() {
            return spacing;
        }
        
        public int getSpaceSize() {
            return space;
        }
    }
    
    private FontData data;
    private SpriteSheet sheet;
    public Font(Texture spriteSheet, FontData data) throws TextureTilingException {
        this.data = data;
        SpriteSheetMeta sheetMeta = new SpriteSheetMeta();
        sheetMeta.gridX = data.x;
        sheetMeta.gridY = data.y;
        sheetMeta.spriteMeta = new SpriteMeta[data.characterList.length()];
        int ySize = spriteSheet.getHeight() / data.y;
        for(int i = 0; i < data.characterList.length(); i++) {
            sheetMeta.spriteMeta[i] = data.getCharacterMeta(data.characterList.charAt(i)).asSpriteMeta(ySize);
        }
        
        try {
            this.sheet = new SpriteSheet("font-" + data.fontName, spriteSheet, sheetMeta);
        } catch(TextureTilingException e) {
            throw e;
        }
    }
    
    public FontData getFontData() {
        return data;
    }
    
    public Sprite getChar(char ch) {
        return sheet.getSprite(data.characterList.indexOf(ch));
    }
    
    public int write(String text, Style style, Color color, float posX, float posY) {
        return write(text, style, color, posX, posY, 1);
    }
    
    public int write(String text, Style style, Color color, float posX, float posY, float scale) {
        return write(text, style, color, posX, posY, scale, scale);
    }
    
    public int write(String text, Style style, Color color, float posX, float posY, float scaleX, float scaleY) {
        return write(text, style, color, posX, posY, scaleX, scaleY, 1.0F);
    }
    
    public int write(String text, Style style, Color color, float posX, float posY, float scaleX, float scaleY, float alpha) {
        if(text.trim().isEmpty()) {
            return 0;
        }
        
        char[] chars = new char[text.length()];
        text.getChars(0, text.length(), chars, 0);
        int pos = 0;
        int textLength = text.replaceAll(" ", "").length();
        
        //for(char chara : chars) {
        if(style != null) {
            style.onNextTextRender(Gdx.graphics.getDeltaTime());
        }
        
        for(int i = 0; i < chars.length; i++) {
            char chara = chars[i];
            if(Character.valueOf(' ').compareTo(chara) == 0) {
                pos += (this.getFontData().getSpaceSize() * scaleX);
                continue;
            }
            
            float aX = 0F, aY = 0F, aScaleX = 1.0F, aScaleY = 1.0F;
            Color usedColor = color;
            if(style != null) {
                DisplayMeta dmeta = style.applyCharacter(i, textLength);
                if(dmeta != null) {
                    aX = dmeta.offX;
                    aY = dmeta.offY;
                    aScaleX = dmeta.scaleX;
                    aScaleY = dmeta.scaleY;
                    usedColor = dmeta.color == null ? color : dmeta.color;
                }
            }
            
            float iScaleX = scaleX * aScaleX;
            float iScaleY = scaleY * aScaleY;
            float offsetX = aX * (iScaleX);
            float offsetY = aY * (iScaleY);
            float drawPosX = posX + pos + offsetX;
            float drawPosY = posY + offsetY;
            
            this.writeCharacter(chara, usedColor, drawPosX, drawPosY, iScaleX, iScaleY, alpha);
            pos += ((this.getChar(chara).getTextureRegion().getRegionWidth() + this.getFontData().getLetterSpacing()) * scaleX);
        }
        
        return pos;
    }
    
    public void writeCharacter(char character, Color color, float posX, float posY, float scaleX, float scaleY, float alpha) {
        if(Character.valueOf(' ').compareTo(character) == 0) {
            return; // ignore spaces
        }
        
        writeCharacter(this.getChar(character), color, posX, posY, scaleX, scaleY, alpha);
    }
    
    private void writeCharacter(Sprite charSprite, Color color, float posX, float posY, float scaleX, float scaleY, float alpha) {
        MultiRenderer renderer = Undertailor.getRenderer();
        Color used = color == null ? Color.YELLOW : color;
        Color old = renderer.getBatchColor();
        renderer.setBatchColor(used, alpha);
        charSprite.draw(posX, posY, scaleX, scaleY, 0F, false, false, charSprite.getTextureRegion().getRegionWidth(), charSprite.getTextureRegion().getRegionHeight(), true);
        renderer.setBatchColor(old, old.a);
    }
    
    public void fontTest(int posX, int posY, int scale) {
        String charList = this.getFontData().characterList;
        Set<Integer> yPos = new HashSet<Integer>();
        yPos.add(posY);
        
        for(int i = 0; i < Math.ceil(charList.length()/13) + 1; i++) {
            int y = posY - (i * 15 * scale);
            yPos.add(y);
        }
        
        MultiRenderer renderer = Undertailor.getRenderer();
        renderer.setShapeColor(new Color(1F, 0F, 0F, 1F), 1F);
        yPos.forEach(i -> {
            float y = i + (scale / 2.0F);
            renderer.drawLine(new Vector2(0, y), new Vector2(Gdx.graphics.getWidth(), y), scale);
        });
        
        for(int i = 0; i < Math.ceil(charList.length()/13) + 1; i++) {
            int chars = (i + 1) * 13;
            int y = posY - (i * 15 * scale);
            this.write(charList.substring(i * 13, chars > charList.length() ? charList.length() : chars), null, null, posX, y, scale);
        }
    }
}
