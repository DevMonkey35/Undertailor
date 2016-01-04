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
