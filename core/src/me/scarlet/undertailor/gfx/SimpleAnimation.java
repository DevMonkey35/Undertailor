package me.scarlet.undertailor.gfx;

import com.badlogic.gdx.graphics.g2d.Batch;
import me.scarlet.undertailor.gfx.KeyFrame.FrameObjectMeta;
import me.scarlet.undertailor.gfx.KeyFrame.SimpleKeyFrame;
import me.scarlet.undertailor.util.MapUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleAnimation extends Animation<SimpleKeyFrame>{
    
    private Map<Long, SimpleKeyFrame> frames;
    public SimpleAnimation(long startTime, boolean loop, SimpleKeyFrame... frames) {
        super(startTime, loop);
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
                System.out.println("time was set to " + time);
            } else {
                System.out.println("returned last value");
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
    public void drawCurrentFrame(Batch batch, long stateTime, float posX, float posY, float scale, float rotation) {
        SimpleKeyFrame frame = this.getFrame(stateTime);
        if(frame == null) {
            return;
        }
        
        Sprite sprite = this.getCurrentSpriteSet()[frame.getSpriteIndex()];
        FrameObjectMeta meta = frame.getMeta() == null ? new FrameObjectMeta() : frame.getMeta();
        float offX = meta.offX * meta.scaleX;
        float offY = meta.offY * meta.scaleY;
        sprite.draw(batch, posX + offX, posY + offY, meta.scaleX * scale, meta.scaleY * scale, meta.rotation, meta.flipX, meta.flipY);
    }
}
