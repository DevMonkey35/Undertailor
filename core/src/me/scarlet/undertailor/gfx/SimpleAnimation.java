package me.scarlet.undertailor.gfx;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.KeyFrame.FrameObjectMeta;
import me.scarlet.undertailor.gfx.KeyFrame.SimpleKeyFrame;
import me.scarlet.undertailor.manager.AnimationManager;
import me.scarlet.undertailor.util.ConfigurateUtil;
import me.scarlet.undertailor.util.MapUtil;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleAnimation extends Animation<SimpleKeyFrame>{
    
    public static final int TYPE_ID = 0;
    
    public static SimpleAnimation fromConfig(ConfigurationNode node) {
        String name = node.getKey().toString();
        Undertailor.instance.debug(AnimationManager.MANAGER_TAG, "loading simpleanimation " + name);
        
        float frameTime = ConfigurateUtil.processFloat(node.getNode("frameTime"), 0.5F);
        int[] frames = ConfigurateUtil.processIntArray(node.getNode("frames"), null);
        boolean looping = ConfigurateUtil.processBoolean(node.getNode("looping"), false);
        
        FrameObjectMeta meta = new FrameObjectMeta();
        meta.flipX = ConfigurateUtil.processBoolean(node.getNode("flipX"), false);
        SimpleKeyFrame[] keyFrames = new SimpleKeyFrame[frames.length];
        for(int i = 0; i < frames.length; i++) {
            keyFrames[i] = new SimpleKeyFrame(frames[i], (long) (1000.0 * frameTime), meta);
        }
        
        return new SimpleAnimation(name, 0, looping, keyFrames);
    }
    
    private Map<Long, SimpleKeyFrame> frames;
    public SimpleAnimation(String name, long startTime, boolean loop, SimpleKeyFrame... frames) {
        super(name, startTime, loop);
        this.frames = new LinkedHashMap<>();
        long lastTime = 0;
        for(SimpleKeyFrame frame : frames) {
            if(lastTime <= 0) {
                lastTime = frame.getFrameTime();
            } else {
                lastTime += frame.getFrameTime();
            }
            
            this.frames.put(lastTime, frame);
        }
    }

    @Override
    public Map<Long, SimpleKeyFrame> getFrames() {
        return new LinkedHashMap<>(frames);
    }

    @Override
    public SimpleKeyFrame getFrame(long stateTime) {
        long time = stateTime - this.getStartTime();
        
        Entry<Long, SimpleKeyFrame> last = MapUtil.getLastEntry(frames);
        if(time > last.getKey()) {
            if(this.isLooping()) {
                time = (long) (time - (last.getKey() * (Math.floor(time / last.getKey()))));
            } else {
                return last.getValue();
            }
        }
        
        Entry<Long, SimpleKeyFrame> current = null;
        Entry<Long, SimpleKeyFrame> previous = null;
        Iterator<Entry<Long, SimpleKeyFrame>> iterator = this.frames.entrySet().iterator();
        while(iterator.hasNext()) {
            previous = current;
            current = iterator.next();
            if(time <= current.getKey()) {
                if(previous == null) {
                    return current.getValue();
                } else {
                    if(time > previous.getKey()) {
                        return current.getValue();
                    }
                }
            }
        }
        
        return null;
    }
    
    @Override
    public void drawCurrentFrame(long stateTime, float posX, float posY, float scale, float rotation) {
        SimpleKeyFrame frame = this.getFrame(stateTime);
        if(frame == null) {
            return;
        }
        
        Sprite sprite = this.getParentSet().getCurrentSpriteset()[frame.getSpriteIndex()];
        FrameObjectMeta meta = frame.getMeta() == null ? new FrameObjectMeta() : frame.getMeta();
        float offX = meta.offX * meta.scaleX;
        float offY = meta.offY * meta.scaleY;
        sprite.draw(posX + offX, posY + offY, meta.scaleX * scale, meta.scaleY * scale, meta.rotation, meta.flipX, meta.flipY, sprite.getTextureRegion().getRegionWidth(), sprite.getTextureRegion().getRegionHeight(), false);
    }
}
