package me.scarlet.undertailor.texts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.scarlet.undertailor.exception.ConfigurationException;
import me.scarlet.undertailor.texts.Font.FontData.CharMeta;
import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;
import me.scarlet.undertailor.texts.TextComponent.Text;
import me.scarlet.undertailor.util.ConfigurateUtil;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Font {
    
    // private static final String CHARLIST = "";
    
    public static class FontData {
        
        public static class CharMeta {
            
            private Integer offX, offY, boundWrapX, boundWrapY;
            
            public CharMeta() {
                this.setValues(new Integer[] {null, null, null, null});
            }
            
            public static CharMeta fromConfig(ConfigurationNode root) {
                try {
                    CharMeta meta = new CharMeta(ConfigurateUtil.processIntegerArray(root, new Integer[] {null, null, null, null}));
                    return meta;
                } catch(ConfigurationException e) {
                    throw e;
                }
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
                    data.charMeta.put(entry.getKey().toString(), CharMeta.fromConfig(entry.getValue()));
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
        private int x = 1, y = 1; // grid size, letter size
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
    private Map<Character, TextureRegion> font;
    public Font(Texture spriteSheet, FontData data) {
        this.font = new HashMap<>();
        this.data = data;
        
        int current = 0;
        int charHeight = (int) (spriteSheet.getHeight() / data.y);
        int charWidth = (int) (spriteSheet.getWidth() / data.x);
        for(int iy = 0; iy < data.y; iy++) {
            for(int ix = 0; ix < data.x; ix++) {
                char currentChar = data.characterList.charAt(current);
                CharMeta meta = data.getCharacterMeta(currentChar);
                TextureRegion region = new TextureRegion(spriteSheet);
                int height = charHeight;
                int width = charWidth;
                int wrapY = 0;
                if(meta != null) {
                    width = width - meta.getBoundWrapX();
                    wrapY = meta.getBoundWrapY();
                    height = height - wrapY;
                }
                
                region.setRegion(ix * charWidth, (iy * charHeight) + wrapY, width, height);
                font.put(currentChar, region);
                current++;
                
                if(current == data.characterList.length()) {
                    break;
                }
            }
        }
    }
    
    public FontData getFontData() {
        return data;
    }
    
    public TextureRegion getChar(char ch) {
        return font.get(ch);
    }
    
    public void write(Batch batch, Text text, int posX, int posY) {
        write(batch, text.getText(), text.getStyle(), text.getColor(), posX, posY);
    }
    
    public void write(Batch batch, String text, Style style, Color color, int posX, int posY) {
        write(batch, text, style, color, posX, posY, 1);
    }
    
    public void write(Batch batch, String text, Style style, Color color, int posX, int posY, int scale) {
        write(batch, text, style, color, posX, posY, scale, 1.0F);
    }
    
    public void write(Batch batch, String text, Style style, Color color, int posX, int posY, int scale, float alpha) {
        if(text.trim().isEmpty()) {
            return;
        }
        
        char[] chars = new char[text.length()];
        text.getChars(0, text.length(), chars, 0);
        int pos = 0;
        
        //for(char chara : chars) {
        if(style != null) {
            style.onNextTextRender(Gdx.graphics.getDeltaTime());
        }
        
        for(int i = 0; i < chars.length; i++) {
            char chara = chars[i];
            if(Character.valueOf(' ').compareTo(chara) == 0) {
                pos += (this.getFontData().getSpaceSize() * scale);
                continue;
            }
            
            CharMeta meta = this.getFontData().getCharacterMeta(chara);
            TextureRegion region = new TextureRegion(this.getChar(chara));
            Color used = color == null ? Color.WHITE : new Color(color);
            //System.out.println(color == null ? "null" : color.toString());
            used.a = alpha;
            batch.setColor(used);
            float aX = 0F, aY = 0F, aScaleX = 1.0F, aScaleY = 1.0F;
            if(style != null) {
                DisplayMeta dmeta = style.applyCharacter(i, text.replaceAll(" ", "").length());
                if(dmeta != null) {
                    aX = dmeta.offX;
                    aY = dmeta.offY;
                    aScaleX = dmeta.scaleX;
                    aScaleY = dmeta.scaleY;
                }
            }
            
            float scaleX = scale * aScaleX;
            float scaleY = scale * aScaleY;
            float originX = ((region.getRegionWidth()) / 2.0F) * scaleX;
            float originY = ((region.getRegionHeight()) / 2.0F) * scaleY;
            float drawPosX = posX + pos + ((meta.getOffsetX() + aX) * scaleX) + originX;
            float drawPosY = posY + ((meta.getOffsetY() + aY) * scaleY) + originY;
            
            batch.draw(region, drawPosX, drawPosY, originX, originY, region.getRegionWidth(), region.getRegionHeight(), scaleX, scaleY, 0);
            pos += ((region.getRegionWidth() + this.getFontData().getLetterSpacing()) * scaleX);
        }
    }
    
    public void sheetTest(Batch batch) {
        int i = 0;
        int i2 = 0;
        for(TextureRegion region : font.values()) {
            int y = 85 + (i2 * 35);
            batch.draw(region, (20 * i) + 15, y, 0, 0, region.getRegionWidth(), region.getRegionHeight(), 2.0F, 2.0F, 0F);
            i++;
            if(i == 15) {
                i = 0;
                i2++;
            }
        }
    }
}
