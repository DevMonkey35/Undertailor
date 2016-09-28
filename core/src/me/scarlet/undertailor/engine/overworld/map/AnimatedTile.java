/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without
 * limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

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
