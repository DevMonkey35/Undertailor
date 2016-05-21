package me.scarlet.undertailor.gfx.spritesheet;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.json.JSONConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.exception.BadAssetException;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.gfx.spritesheet.Sprite.SpriteMeta;
import me.scarlet.undertailor.resource.Resource;
import me.scarlet.undertailor.util.StreamUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipFile;

/**
 * The default format for spritesheets, where the sheet and
 * the accompanying data is packaged inside a zip file.
 * 
 * <p>
 */
public class PackagedSpriteSheet extends Resource<Texture> implements SpriteSheet {

    static Logger log = LoggerFactory.getLogger(PackagedSpriteSheet.class);

    public static final String ENTRY_SPRITESHEET = "spritesheet.png";
    public static final String ENTRY_SHEETCONFIG = "spritesheet.json";

    private String name;
    private ZipFile sourceFile;
    private MultiRenderer renderer;
    private List<Sprite> sprites;
    private ConfigurationNode sheetConfig;

    public PackagedSpriteSheet(MultiRenderer renderer, String name, ZipFile sourceFile) {
        this.name = name;
        this.renderer = renderer;
        this.sourceFile = sourceFile;
        this.sprites = new ArrayList<>();

        InputStream configStream = null; // load the configuration json inside the archive for later reading
        try {
            configStream = sourceFile.getInputStream(sourceFile.getEntry(ENTRY_SHEETCONFIG));
            BufferedReader reader = new BufferedReader(new InputStreamReader(configStream));
            JSONConfigurationLoader loader = JSONConfigurationLoader.builder().setSource(() -> {
                return reader;
            }).build();

            sheetConfig = loader.load();
        } catch (IOException e) {
            log.error("Failed to load packaged spritesheet data", e);
        } finally {
            StreamUtil.closeQuietly(configStream);
        }
    }

    // ---------------- abstract method implementation ----------------

    @Override
    protected void onDispose() {
        this.sprites.clear();
    }

    @Override
    protected Texture newReference() {
        InputStream textureStream = null;
        try { // load the spritesheet here
            textureStream = sourceFile.getInputStream(sourceFile.getEntry(ENTRY_SPRITESHEET));
            Gdx2DPixmap readMap = new Gdx2DPixmap(textureStream, Gdx2DPixmap.GDX2D_FORMAT_RGBA8888);
            Texture texture = new Texture(new Pixmap(readMap));
            readMap.dispose();

            this.loadSheet(texture);

            return texture;
        } catch (Exception e) {
            log.error("Failed to load packaged spritesheet", e);
        } finally {
            StreamUtil.closeQuietly(textureStream);
        }

        return null;
    }

    @Override
    protected Class<Texture> getResourceClass() {
        return Texture.class;
    }

    @Override
    public String getSheetName() {
        return this.name;
    }

    @Override
    public Sprite getSprite(int index) {
        this.getReference();
        return this.sprites.get(index);
    }

    @Override
    public Collection<Sprite> getSprites() {
        this.getReference();
        return this.sprites;
    }

    // ---------------- configuration loader ----------------

    private static Object[] KEY_VERSION = {"version"};

    private static Object[] KEY_GRID_SIZE_X = {"sprites", "grid", "sizeX"};
    private static Object[] KEY_GRID_SIZE_Y = {"sprites", "grid", "sizeY"};

    private static Object[] KEY_META_LIST = {"sprites", "meta", null};
    // inside a meta block
    private static Object[] KEY_META_ORIGINX = {"originX"};
    private static Object[] KEY_META_ORIGINY = {"originY"};
    private static Object[] KEY_META_WRAPX = {"wrapX"};
    private static Object[] KEY_META_WRAPY = {"wrapY"};
    private static Object[] KEY_META_OFFX = {"offX"};
    private static Object[] KEY_META_OFFY = {"offY"};

    /**
     * Internal method.
     * 
     * <p>Responsible for loading the texture, converting
     * them into usable {@link Sprite}s.</p>
     * 
     * <p>Implementing configuration version 0.</p>
     */
    private void loadSheet(Texture texture) throws BadAssetException { // implementing version 0
        int version = this.sheetConfig.getNode(KEY_VERSION).getInt(-1);
        if (version != 0) {
            String message = version == -1 ?
                "Cannot continue with an unknown PackagedSpriteSheet configuration version"
                : "Current version does not support PackagedSpriteSheet configuration version " + version;
            throw new UnsupportedOperationException(message);
        }

        int gridX = this.sheetConfig.getNode(KEY_GRID_SIZE_X).getInt(0);
        int gridY = this.sheetConfig.getNode(KEY_GRID_SIZE_Y).getInt(0);

        if (texture.getWidth() % gridX != 0 || texture.getHeight() % gridY != 0) {
            throw new BadAssetException("Incorrectly configured spritesheet with bad grid size");
        }

        int spriteWidth = texture.getWidth() / gridX;
        int spriteHeight = texture.getHeight() / gridY;

        for (int y = 0; y < gridY; y++) {
            for (int x = 0; x < gridX; x++) {
                int pos = (y * gridX) + x;
                int wrapX = 0;
                int wrapY = 0;

                SpriteMeta meta = null;
                KEY_META_LIST[2] = pos;
                ConfigurationNode metaNode = this.sheetConfig.getNode(KEY_META_LIST);
                if (!metaNode.isVirtual()) {
                    meta = new SpriteMeta(metaNode.getNode(KEY_META_ORIGINX).getFloat(0),
                        metaNode.getNode(KEY_META_ORIGINY).getFloat(0),
                        metaNode.getNode(KEY_META_WRAPX).getInt(0),
                        metaNode.getNode(KEY_META_WRAPY).getInt(0),
                        metaNode.getNode(KEY_META_OFFX).getInt(0),
                        metaNode.getNode(KEY_META_OFFY).getInt(0));

                    wrapX = meta.wrapX;
                    wrapY = meta.wrapY;
                }

                Sprite added = new Sprite(
                    this.renderer, new TextureRegion(texture, x * spriteWidth,
                        y * spriteHeight, spriteWidth - wrapX, spriteHeight - wrapY),
                    meta);
                added.sourceSheet = this; // prevents weakly-reachable state if in use
                this.sprites.add(added);
            }
        }
    }
}
