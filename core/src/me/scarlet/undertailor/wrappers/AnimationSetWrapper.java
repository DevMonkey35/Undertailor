package me.scarlet.undertailor.wrappers;

import me.scarlet.undertailor.gfx.AnimationSet;
import ninja.leaping.configurate.ConfigurationNode;

public class AnimationSetWrapper extends DisposableWrapper<AnimationSet> {

    public static final long MAX_LIFETIME = 120000; // 2 minutes 
    
    private ConfigurationNode node;
    public AnimationSetWrapper(ConfigurationNode node) {
        super(null);
        this.node = node;
    }
    
    @Override
    public AnimationSet newReference() {
        return AnimationSet.fromConfig(node);
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
