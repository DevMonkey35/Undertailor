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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.scarlet.undertailor.gfx.Sprite.SpriteMeta;
import me.scarlet.undertailor.util.ConfigurateUtil;
import me.scarlet.undertailor.util.NumberUtil;
import ninja.leaping.configurate.ConfigurationNode;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BatchSpriteSheet implements SpriteSheet {
    
    public static class BatchSpriteSheetMeta {
        
        // percentage values
        public float originX, originY;
        
        public BatchSpriteSheetMeta() {
            this.originX = 0;
            this.originY = 0;
        }
    }
    
    public static BatchSpriteSheet fromConfig(String sheetName, File[] textureFiles, ConfigurationNode config) {
        BatchSpriteSheetMeta meta = new BatchSpriteSheetMeta();
        if(config != null) {
            meta.originX = ConfigurateUtil.processFloat(config.getNode("originX"), 0F);
            meta.originY = ConfigurateUtil.processFloat(config.getNode("originY"), 0F);
            
            meta.originX = NumberUtil.boundFloat(meta.originX, 0.0F, 1.0F);
            meta.originY = NumberUtil.boundFloat(meta.originY, 0.0F, 1.0F);
        }
        
        return new BatchSpriteSheet(sheetName, textureFiles, meta);
    }
    
    private String sheetName;
    private Map<String, Sprite> sprites;
    public BatchSpriteSheet(String sheetName, File[] textureFiles, BatchSpriteSheetMeta meta) {
        this.sheetName = sheetName;
        this.sprites = new HashMap<>();
        for(File file : textureFiles) {
            Texture texture = new Texture(Gdx.files.absolute(file.getAbsolutePath()));
            SpriteMeta sprMeta = new SpriteMeta();
            
            sprMeta.originX = texture.getWidth() * meta.originX;
            sprMeta.originY = texture.getHeight() * meta.originY;
            sprites.put(file.getName().substring(0, file.getName().length() - 4), new Sprite(new TextureRegion(texture), sprMeta));
        }
    }
    
    @Override
    public Collection<Sprite> getSprites() {
        return this.sprites.values();
    }
    
    @Override
    public Sprite getSprite(String id) {
        return this.sprites.get(id);
    }
    
    @Override
    public String getSheetName() {
        return this.sheetName;
    }
    
    @Override
    public void dispose() {
        for(Sprite sprite : this.getSprites()) {
            sprite.getTextureRegion().getTexture().dispose();
        }
    }
}
