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

package me.scarlet.undertailor.wrappers;

import com.badlogic.gdx.graphics.Texture;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.TextureTilingException;
import me.scarlet.undertailor.gfx.SpriteSheet;
import me.scarlet.undertailor.manager.SpriteSheetManager;
import me.scarlet.undertailor.util.LuaUtil;
import ninja.leaping.configurate.ConfigurationNode;

import java.io.FileNotFoundException;

public class SpriteSheetWrapper extends DisposableWrapper<SpriteSheet> {

    public static final long MAX_LIFETIME = 60000; // 1 minute
    
    private String name;
    private Texture texture;
    private ConfigurationNode config;
    public SpriteSheetWrapper(String name, Texture texture, ConfigurationNode config) {
        super(null);
        this.name = name;
        this.config = config;
        this.texture = texture;
    }

    @Override
    public SpriteSheet newReference() {
        try {
            return SpriteSheet.fromConfig(name, texture, config);
        } catch(FileNotFoundException | TextureTilingException e) {
            Undertailor.instance.error(SpriteSheetManager.MANAGER_TAG, LuaUtil.formatJavaException(e), e);
            return null;
        }
    }
    
    @Override
    public long getMaximumLifetime() {
        return MAX_LIFETIME;
    }

    @Override
    public boolean allowDispose() {
        if(!this.getReferrers().isEmpty()) {
            return false;
        }
        
        return true;
    }
}
