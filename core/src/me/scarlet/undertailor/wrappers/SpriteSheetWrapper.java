package me.scarlet.undertailor.wrappers;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.TextureTilingException;
import me.scarlet.undertailor.gfx.SpriteSheet;
import me.scarlet.undertailor.manager.SpriteSheetManager;
import ninja.leaping.configurate.ConfigurationNode;

import java.io.File;
import java.io.FileNotFoundException;

public class SpriteSheetWrapper extends DisposableWrapper<SpriteSheet> {

    public static final long MAX_LIFETIME = 60000; // 1 minute
    
    private File configDir;
    private ConfigurationNode config;
    public SpriteSheetWrapper(File configDir, ConfigurationNode config) {
        super(null);
        this.config = config;
        this.configDir = configDir;
    }

    @Override
    public SpriteSheet newReference() {
        try {
            return SpriteSheet.fromConfig(configDir, config);
        } catch(FileNotFoundException | TextureTilingException e) {
            Undertailor.instance.error(SpriteSheetManager.MANAGER_TAG, e.getMessage(), e.getStackTrace());
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
