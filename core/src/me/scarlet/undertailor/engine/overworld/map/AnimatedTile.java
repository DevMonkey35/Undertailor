package me.scarlet.undertailor.engine.overworld.map;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.spritesheet.Sprite;
import me.scarlet.undertailor.util.Tuple;

/**
 * An animated tile.
 */
public class AnimatedTile implements Renderable, Cloneable {

    private static long origin;

    static {
        origin = TimeUtils.millis();
    }

    private Array<Tuple<Long, Sprite>> frames;
    private long length;

    public AnimatedTile() {
        this.frames = new Array<>(true, 8);
        this.length = 0;
    }

    /**
     * Adds a new frame to this {@link AnimatedTile}.
     * 
     * @param duration how long the new frame is displayed
     * @param tile the frame
     */
    public void addFrame(long duration, Sprite tile) {
        this.frames.add(new Tuple<>(duration, tile));
        this.length = 0;
        for (int i = 0; i < this.frames.size; i++) {
            this.length += this.frames.get(i).getA();
        }
    }

    /**
     * Returns an array of all frames registered with this
     * {@link AnimatedTile}.
     * 
     * @return an array of this AnimatedTile's frames
     */
    public Array<Tuple<Long, Sprite>> getFrames() {
        return this.frames;
    }

    @Override
    public void render(float x, float y) {
        long runtime = TimeUtils.timeSinceMillis(AnimatedTile.origin) % length;
        long duration = 0;
        Sprite frame = null;
        for (int i = 0; i < this.frames.size; i++) {
            Tuple<Long, Sprite> key = this.frames.get(i);
            duration += key.getA();
            frame = key.getB();
            if (duration > runtime) {
                break;
            }
        }

        if (frame != null) {
            frame.render(x, y);
        }
    }

    /**
     * Clones this {@link AnimatedTile}.
     * 
     * <p>The underlying {@link Sprite}s are also
     * cloned.</p>
     * 
     * @return a copy of this AnimatedTile
     */
    public AnimatedTile clone() {
        AnimatedTile clone = new AnimatedTile();
        clone.frames = new Array<>(true, 8);
        clone.length = this.length;

        this.frames.forEach(entry -> {
            clone.frames.add(new Tuple<>(entry.getA(), entry.getB().clone()));
        });

        return clone;
    }
}
