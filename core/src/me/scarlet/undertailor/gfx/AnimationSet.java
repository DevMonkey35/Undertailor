package me.scarlet.undertailor.gfx;

import com.badlogic.gdx.utils.Disposable;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.ConfigurationException;
import me.scarlet.undertailor.manager.AnimationManager;
import me.scarlet.undertailor.util.ConfigurateUtil;
import me.scarlet.undertailor.wrappers.SpriteSheetWrapper;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AnimationSet implements Disposable {

    public static final String DEFAULT_SPRITESET = "default";
    
    public static AnimationSet fromConfig(String name, ConfigurationNode node) {
        try {
            String[] sheets = ConfigurateUtil.processStringArray(node.getNode("meta", "usedSheets"), null);
            Map<String, String[]> spriteSets = new HashMap<>();
            Map<String, Animation<?>> animations = new HashMap<>();
            for(Entry<Object, ? extends ConfigurationNode> entry : node.getNode("meta", "spriteSets").getChildrenMap().entrySet()) {
                spriteSets.put(entry.getKey().toString(), ConfigurateUtil.processStringArray(entry.getValue(), null));
            }
            
            for(Entry<Object, ? extends ConfigurationNode> entry : node.getNode("animation").getChildrenMap().entrySet()) {
                String key = entry.getKey().toString();
                ConfigurationNode animationNode = entry.getValue();
                if(ConfigurateUtil.processInt(animationNode.getNode("type"), null) == 0) {
                    animations.put(key, SimpleAnimation.fromConfig(animationNode));
                }
            }
            
            return new AnimationSet(name, sheets, spriteSets, animations);
        } catch(ConfigurationException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    private String name;
    private String currentSpriteset;
    private Map<String, Sprite[]> spritesets;
    private Map<String, Animation<?>> animations;
    private Map<String, SpriteSheetWrapper> sheets;
    public AnimationSet(String name, String[] sheetNames, Map<String, String[]> spriteSets, Map<String, Animation<?>> animations) {
        this.name = name;
        this.animations = animations;
        this.sheets = new HashMap<>();
        this.spritesets = new HashMap<>();
        this.currentSpriteset = DEFAULT_SPRITESET;
        
        for(String str : sheetNames) {
            SpriteSheetWrapper sheet = Undertailor.getSheetManager().getRoomObject(str);
            sheet.getReference(this);
            sheets.put(str, sheet);
        }
        
        for(Entry<String, String[]> entry : spriteSets.entrySet()) {
            String[] strset = entry.getValue();
            Sprite[] set = new Sprite[strset.length];
            Undertailor.instance.debug(AnimationManager.MANAGER_TAG, "loading spriteset " + entry.getKey());
            for(int i = 0; i < set.length; i++) {
                String[] spriteData = strset[i].split(":");
                if(spriteData.length < 2) {
                    Undertailor.instance.error(AnimationManager.MANAGER_TAG, "failed to load sprite in spriteset " + spriteData[0] + ": bad configuration");
                } else {
                    try {
                        set[i] = sheets.get(spriteData[0]).getReference().getSprites()[Integer.parseInt(spriteData[1])];
                    } catch(NumberFormatException e) {
                        Undertailor.instance.error(AnimationManager.MANAGER_TAG, "failed to load sprite in spriteset " + spriteData[0] + ": bad configuration");
                    }
                }
            }
            
            this.spritesets.put(entry.getKey(), set);
        }
        
        for(Animation<?> animation : animations.values()) {
            animation.animSet = this;
        }
    }
    
    public String getName() {
        return name;
    }
    
    public Sprite[] getCurrentSpriteset() {
        return spritesets.get(currentSpriteset);
    }
    
    public void setCurrentSpriteset(String set) {
        this.currentSpriteset = set;
    }
    
    public Animation<?> getAnimation(String name) {
        return animations.get(name);
    }

    @Override
    public void dispose() {
        for(SpriteSheetWrapper wrapper : sheets.values()) {
            wrapper.removeReference(this);
        }
        
        this.spritesets = null;
        this.animations = null;
        this.sheets = null;
    }
}
