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

package me.scarlet.undertailor.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.ConfigurationException;
import me.scarlet.undertailor.exception.TextureTilingException;
import me.scarlet.undertailor.gfx.Sprite.SpriteMeta;
import me.scarlet.undertailor.util.ConfigurateUtil;
import ninja.leaping.configurate.ConfigurationNode;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureSpriteSheet implements SpriteSheet {
    
    public static class TextureSpriteSheetMeta {
        
        public int gridX, gridY;
        public SpriteMeta[] spriteMeta; // metadata per frame
        
        public TextureSpriteSheetMeta() {
            this.gridX = 1;
            this.gridY = 1;
            this.spriteMeta = null;
        }
    }
    
    public static SpriteSheet fromConfig(String name, Texture texture, ConfigurationNode node) throws FileNotFoundException, TextureTilingException {
        TextureSpriteSheetMeta meta = new TextureSpriteSheetMeta();
        try {
            meta.gridX = ConfigurateUtil.processInt(node.getNode("gridSizeX"), null);
            meta.gridY = ConfigurateUtil.processInt(node.getNode("gridSizeY"), null);
            ConfigurationNode metaRoot = node.getNode("spritemeta");
            if(!metaRoot.isVirtual()) {
                int spriteCount = meta.gridX * meta.gridY;
                meta.spriteMeta = new SpriteMeta[spriteCount];
                for(int i = 0; i < spriteCount; i++) {
                    for(ConfigurationNode metaNode : metaRoot.getChildrenMap().values()) {
                        List<String> metakeys = Arrays.asList(metaNode.getKey().toString().split(","));
                        if(metakeys.contains("" + i)) {
                            if(meta.spriteMeta[i] == null) {
                                meta.spriteMeta[i] = new SpriteMeta();
                            }
                            
                            SpriteMeta cmeta = meta.spriteMeta[i];
                            try {
                                cmeta.offX = ConfigurateUtil.processInt(metaNode.getNode("offX"), cmeta.offX);
                                cmeta.offY = ConfigurateUtil.processInt(metaNode.getNode("offY"), cmeta.offY);
                                cmeta.wrapX = ConfigurateUtil.processInt(metaNode.getNode("wrapX"), cmeta.wrapX);
                                cmeta.wrapY = ConfigurateUtil.processInt(metaNode.getNode("wrapY"), cmeta.wrapY);
                                cmeta.originX = ConfigurateUtil.processFloat(metaNode.getNode("originX"), cmeta.originX);
                                cmeta.originY = ConfigurateUtil.processFloat(metaNode.getNode("originY"), cmeta.originY);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                            
                            Undertailor.instance.debug("sheetman", "sheet " + name + " queued sprite at index " + i + " with meta "
                                    + meta.spriteMeta[i].toString());
                        }
                    }
                }
            }
        } catch(ConfigurationException e) {
            e.printStackTrace();
            throw e;
        }
        
        return new TextureSpriteSheet(name, texture, meta);
    }
    
    private Texture texture;
    private String sheetName;
    private Map<Integer, Sprite> sprites;
    public TextureSpriteSheet(String sheetName, Texture texture, TextureSpriteSheetMeta meta) throws TextureTilingException {
        this.texture = texture;
        this.sheetName = sheetName;
        checkTexture(texture, meta.gridX, meta.gridY);
        sprites = new HashMap<>();
        int spriteHeight = texture.getHeight() / meta.gridY;
        int spriteWidth = texture.getWidth() / meta.gridX;
        for(int iY = 0; iY < meta.gridY; iY++) {
            for(int iX = 0; iX < meta.gridX; iX++) {
                int pos = (iY * meta.gridX) + iX;
                SpriteMeta smeta = null;
                if(meta.spriteMeta != null && pos < meta.spriteMeta.length && meta.spriteMeta[pos] != null) {
                    smeta = meta.spriteMeta[pos];
                }
                
                TextureRegion region = new TextureRegion(texture);
                int height = spriteHeight;
                int width = spriteWidth;
                int wrapY = 0;
                if(smeta != null) {
                    width -= smeta.wrapX;
                    height -= smeta.wrapY;
                    wrapY = smeta.wrapY;
                }
                
                region.setRegion(iX * spriteWidth, (iY * spriteHeight) + wrapY, width, height);
                sprites.put(pos, new Sprite(region, smeta));
            }
        }
    }
    
    public Collection<Sprite> getSprites() {
        return sprites.values();
    }
    
    public Sprite getSprite(String id) {
        try {
            return this.getSprite(Integer.parseInt(id));
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("bad identifier \"" + id + "\" for sprite in spritesheet " + this.getSheetName());
        }
    }
    
    public Sprite getSprite(int index) {
        return this.sprites.get(index);
    }
    
    public String getSheetName() {
        return sheetName;
    }
    
    public Texture getTexture() {
        return texture;
    }
    
    private void checkTexture(Texture image, int width, int height) throws TextureTilingException {
        if(image.getWidth() % width != 0) {
            throw new TextureTilingException("Texture width is not divisible by defined width");
        }
        
        if(image.getHeight() % height != 0) {
            throw new TextureTilingException("Texture width is not divisible by defined height");
        }
    }
    
    public void sheetTest(Batch batch) {
        int i = 0;
        int i2 = 0;
        for(Sprite sprite : sprites.values()) {
            int y = 400 - (i2 * 35);
            sprite.draw(20 * i + 16, y);
            i++;
            if(i == 16) {
                i = 0;
                i2++;
            }
        }
    }
    
    @Override
    public void dispose() {
        Gdx.app.postRunnable(() -> texture.dispose());
    }
}
