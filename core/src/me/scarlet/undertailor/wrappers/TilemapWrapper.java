package me.scarlet.undertailor.wrappers;

import com.badlogic.gdx.graphics.Texture;
import me.scarlet.undertailor.exception.TextureTilingException;
import me.scarlet.undertailor.gfx.SpriteSheet;
import me.scarlet.undertailor.gfx.SpriteSheet.SpriteSheetMeta;
import me.scarlet.undertailor.util.ConfigurateUtil;
import ninja.leaping.configurate.ConfigurationNode;

public class TilemapWrapper extends DisposableWrapper<SpriteSheet> {

    private Texture texture;
    private String tilemapName;
    private ConfigurationNode tilemapData;
    public TilemapWrapper(String tilemapName, Texture texture, ConfigurationNode tilemapData) {
        super(null);
        this.texture = texture;
        this.tilemapName = tilemapName;
        this.tilemapData = tilemapData;
    }

    @Override
    public SpriteSheet newReference() {
        SpriteSheetMeta meta = new SpriteSheetMeta();
        meta.gridX = ConfigurateUtil.processInt(tilemapData.getNode("sizeX"), null);
        meta.gridY = ConfigurateUtil.processInt(tilemapData.getNode("sizeY"), null);
        
        try {
            return new SpriteSheet(tilemapName, texture, meta);
        } catch(TextureTilingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public long getMaximumLifetime() {
        return SpriteSheetWrapper.MAX_LIFETIME;
    }

    @Override
    public boolean allowDispose() {
        if(!this.getReferrers().isEmpty()) {
            return false;
        }
        
        return true;
    }
}
