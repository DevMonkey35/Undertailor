package me.scarlet.undertailor.texts;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.common.base.Preconditions;
import me.scarlet.undertailor.texts.Font.FontData.CharMeta;
import me.scarlet.undertailor.util.ConfigurateUtil;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Font {
    
    private static final String CHARLIST = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_?!.,:;\"|'`^()[]{}<>+~=-@%#&$*/\\";
    
    public static class FontData {
        
        public static class Builder {
            
            private FontData data;
            public Builder() {
                data = new FontData();
                data.fontName = null;
                data.charMeta = new HashMap<>();
                data.globalMeta = null;
                data.x = 0;
                data.y = 0;
                data.pX = 0;
                data.pY = 0;
                data.space = 0;
                data.spacing = 0;
            }
            
            public Builder name(String name) {
                data.fontName = name;
                return this;
            }
            
            public Builder gridSizeX(int x) {
                data.x = x;
                return this;
            }
            
            public Builder gridSizeY(int y) {
                data.y = y;
                return this;
            }
            
            public Builder characterSizeX(int x) {
                data.pX = x;
                return this;
            }
            
            public Builder characterSizeY(int y) {
                data.pY = y;
                return this;
            }
            
            public Builder letterSpacing(int spacing) {
                data.spacing = spacing;
                return this;
            }
            
            public Builder spaceSize(int space) {
                data.space = space;
                return this;
            }
            
            public Builder characterMeta(String characters, CharMeta meta) {
                data.charMeta.put(characters, meta);
                return this;
            }
            
            public Builder globalMeta(CharMeta meta) {
                data.globalMeta = meta;
                return this;
            }
            
            public FontData build() {
                Preconditions.checkNotNull(data.fontName, "font needs a name");
                Preconditions.checkArgument(data.x > 0, "grid x must be at least 1");
                Preconditions.checkArgument(data.y > 0, "grid y must be at least 1");
                Preconditions.checkArgument(data.pX > 0, "char x must be at least 1");
                Preconditions.checkArgument(data.pY > 0, "char y must be at least 1");
                Preconditions.checkArgument(data.spacing > 0, "letter spacing must be at least 1");
                Preconditions.checkArgument(data.space > 0, "space size must be at least 1");
                return data;
            }
        }
        
        public static class CharMeta {
            
            public int offX, offY, boundWrapX, boundWrapY;
            
            public CharMeta() {}
            
            public static CharMeta fromConfig(ConfigurationNode root) {
                try {
                    CharMeta meta = new CharMeta(ConfigurateUtil.processIntArray(root, "fontloader"));
                    return meta;
                } catch(RuntimeException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            
            public CharMeta(int[] values) {
                this.setValues(values);
            }
            
            public CharMeta(int offX, int offY) {
                this(offX, offY, 0, 0);
            }
            
            public CharMeta(int offX, int offY, int boundWrapX, int boundWrapY) {
                this.offX = offX;
                this.offY = offY;
                this.boundWrapX = boundWrapX;
                this.boundWrapY = boundWrapY;
            }
            
            public int[] values() {
                return new int[] {offX, offY, boundWrapX, boundWrapY};
            }
            
            public void setValues(int[] values) {
                this.offX = values[0];
                this.offY = values[1];
                this.boundWrapX = values[2];
                this.boundWrapY = values[3];
            }
            
            public CharMeta merge(CharMeta otherMeta) {
                int[] set = new int[4];
                int[] values = otherMeta.values();
                for(int i = 0; i < 4; i++) {
                    if(values[i] != 0) {
                        set[i] = values[i];
                    }
                }
                
                return new CharMeta(set);
            }
            
            @Override
            public String toString() {
                return "[" + offX + ", " + offY + ", " + boundWrapX + ", " + boundWrapY + "]";
            }
        }
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static FontData fromConfig(String name, ConfigurationNode node) {
            FontData data = new FontData();
            ConfigurationNode root = node.getNode("font");
            try {
                data.fontName = name;
                data.x = ConfigurateUtil.processInt(root.getNode("gridSizeX"), "fontloader");
                data.y = ConfigurateUtil.processInt(root.getNode("gridSizeY"), "fontloader");
                data.pX = ConfigurateUtil.processInt(root.getNode("charSizeX"), "fontloader");
                data.pY = ConfigurateUtil.processInt(root.getNode("charSizeY"), "fontloader");
                data.space = ConfigurateUtil.processInt(root.getNode("spaceSize"), "fontloader");
                data.spacing = ConfigurateUtil.processInt(root.getNode("letterSpacing"), "fontloader");
                
                data.globalMeta = CharMeta.fromConfig(root.getNode("globalMeta"));
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
        private Map<String, CharMeta> charMeta;
        private int x, y, pX, pY = -1; // grid size, letter size
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
        
        public int getLetterSizeX() {
            return pX;
        }
        
        public int getLetterSizeY() {
            return pY;
        }
    }
    
    private FontData data;
    private Map<Character, TextureRegion> font;
    public Font(Texture spriteSheet, FontData data) {
        this.font = new HashMap<>();
        this.data = data;
        
        int current = 0;
        for(int iy = 0; iy < data.y; iy++) {
            for(int ix = 0; ix < data.x; ix++) {
                char currentChar = Font.CHARLIST.charAt(current);
                CharMeta meta = data.getCharacterMeta(currentChar);
                TextureRegion region = new TextureRegion(spriteSheet);
                int height = data.pY;
                int width = data.pX;
                if(meta != null) {
                    width = width + meta.boundWrapX;
                    height = height + meta.boundWrapY;
                }
                
                region.setRegion(ix * data.pX, iy * data.pY, width, height);
                font.put(currentChar, region);
                current++;
                
                if(current == Font.CHARLIST.length()) {
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
}
