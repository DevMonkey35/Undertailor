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

package me.scarlet.undertailor.gfx.animation;

import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;
import me.scarlet.undertailor.gfx.animation.FrameAnimation.KeyFrame.KeyFrameData;
import me.scarlet.undertailor.util.NumberUtil;
import me.scarlet.undertailor.util.NumberUtil.Interpolator;
import me.scarlet.undertailor.util.Pair;

import java.util.TreeMap;

/**
 * Implementation of an {@link Animation} for frame-by-frame
 * animations.
 */
public class FrameAnimation extends Animation {

    /**
     * Holds data for key frames making up a
     * {@link FrameAnimation}.
     */
    public static class KeyFrame {

        static final KeyFrameData DEFAULT_DATA = new KeyFrameData(0, 0);

        /**
         * Holds data of a {@link KeyFrame}, pertaining to
         * the transformation of the held {@link Renderable}
         * .
         */
        public static class KeyFrameData {

            public static enum Interpolation {
                LINEAR, EASE_IN, EASE_OUT
            }

            public float x;
            public float y;
            public float scaleX;
            public float scaleY;
            public boolean flipX;
            public boolean flipY;
            public float rotation;

            public KeyFrameData(float x, float y) {
                this(x, y, 1F);
            }

            public KeyFrameData(float x, float y, float scale) {
                this(x, y, scale, scale);
            }

            public KeyFrameData(float x, float y, float scaleX, float scaleY) {
                this(x, y, scaleX, scaleY, false, false);
            }

            public KeyFrameData(float x, float y, float scaleX, float scaleY, boolean flipX,
                boolean flipY) {
                this(x, y, scaleX, scaleY, flipX, flipY, 0F);
            }

            public KeyFrameData(float x, float y, float scaleX, float scaleY, boolean flipX,
                boolean flipY, float rotation) {
                this.x = x;
                this.y = y;
                this.scaleX = scaleX;
                this.scaleY = scaleY;
                this.flipX = flipX;
                this.flipY = flipY;
                this.rotation = rotation;
            }

            /**
             * Interpolates the values of this
             * {@link KeyFrameData} as the lower bounds
             * between the values of the provided
             * KeyFrameData, of which is treated as the
             * higher bounds.
             * 
             * @param to the KeyFrameData to interpolate to,
             *        treated as the higher bound
             * @param progress the percentage of time
             *        progressed between this frame and the
             *        frame to be
             * 
             * @return an array of floats containing the x,
             *         y, scaleX, scaleY, and rotation
             *         values in the order listed
             */
            public float[] interpolateValues(KeyFrameData to, float progress) {
                float[] returned = new float[5];

                Interpolator interpolator = NumberUtil.INTERPOLATOR_LINEAR;

                returned[0] = interpolator.interpolate(this.x, to.x, progress).floatValue();
                returned[1] = interpolator.interpolate(this.y, to.y, progress).floatValue();
                returned[2] =
                    interpolator.interpolate(this.scaleX, to.scaleX, progress).floatValue();
                returned[3] =
                    interpolator.interpolate(this.scaleY, to.scaleY, progress).floatValue();
                returned[4] =
                    interpolator.interpolate(this.rotation, to.rotation, progress).floatValue();
                return returned;
            }

            /**
             * Returns whether or not the values of this
             * {@link KeyFrameData} match that of the
             * provided.
             * 
             * @param other the KeyFrameData to compare to
             * 
             * @return if the other KeyFrameData has the
             *         same values
             */
            public boolean matches(KeyFrameData other) {
                return this.x == other.x && this.y == other.y && this.scaleX == other.scaleX
                    && this.scaleY == other.scaleY && this.rotation == other.rotation;
            }
        }

        private long time;
        private Renderable frame;
        private KeyFrameData data;

        public KeyFrame(long time, Renderable frame) {
            this(time, frame, null);
        }

        public KeyFrame(long time, Renderable frame, KeyFrameData data) {
            if (frame instanceof Animation) {
                throw new IllegalArgumentException("Cannot use another animation in a frame");
            }

            this.time = time;
            this.frame = frame;
            this.data = data;
        }

        /**
         * Returns the point in time in which this
         * {@link KeyFrame} becomes the active frame in a
         * {@link FrameAnimation}, in milliseconds.
         * 
         * @return the key time of this KeyFrame
         */
        public long getKeyTime() {
            return this.time;
        }

        /**
         * Returns the {@link Renderable} object assigned to
         * this {@link KeyFrame}.
         * 
         * @return a Renderable
         */
        public Renderable getFrame() {
            return this.frame;
        }

        /**
         * Returns the {@link KeyFrameData} associated with
         * this {@link KeyFrame}.
         * 
         * <p>If no frame data is found, a default frame
         * data object with default values is provided
         * instead.</p>
         * 
         * @return a KeyFrameData
         */
        public KeyFrameData getFrameData() {
            return this.data == null ? FrameAnimation.KeyFrame.DEFAULT_DATA : this.data;
        }
    }

    private static final Transform PROXY_TRANSFORM;

    static {
        PROXY_TRANSFORM = new Transform();
    }

    private long length;
    private TreeMap<Long, KeyFrame> frames;
    private Pair<KeyFrame> returnBuffer;
    private Transform transform;

    public FrameAnimation(KeyFrame... frames) {
        this.returnBuffer = new Pair<>();
        this.frames = new TreeMap<>((time1, time2) -> {
            return Long.compare(time2, time1);
        });

        for (KeyFrame frame : frames) {
            this.frames.put(frame.time, frame);
        }

        this.length = this.frames.firstKey();
        this.transform = new Transform();
    }

    // ---------------- abstract method implementation ----------------

    @Override
    public Transform getTransform() {
        return this.transform;
    }

    @Override
    public void setTransform(Transform transform) {
        if(transform == null) {
            this.transform = Transform.DUMMY.copy(this.transform);
        } else {
            this.transform = transform;
        }
    }

    @Override
    public void draw(float x, float y, Transform transform) {
        Transform drawnTransform = transform.copy(PROXY_TRANSFORM);
        Pair<KeyFrame> frames = this.getCurrentFrame();
        KeyFrame drawn = frames.getFirst();
        if (frames.getSecond() == null) { // last frame? just draw it
            KeyFrameData data = frames.getFirst().getFrameData();

            x += data.x;
            y += data.y;
            drawnTransform.addScaleX(data.scaleX);
            drawnTransform.addScaleY(data.scaleY);
            drawnTransform.setFlipX(drawnTransform.getFlipX() && data.flipX);
            drawnTransform.setFlipY(drawnTransform.getFlipY() && data.flipY);
            drawnTransform.addRotation(data.rotation);
        } else {
            // interpolation
            KeyFrameData first = drawn.getFrameData();
            KeyFrameData second = frames.getSecond().getFrameData();
            long realRuntime = this.getCycleRuntime();
            float currentFrameProgress = (float) (realRuntime - frames.getFirst().getKeyTime())
                / (frames.getSecond().getKeyTime() - frames.getFirst().getKeyTime());
            float[] interpolation = first.interpolateValues(second, currentFrameProgress);

            x += interpolation[0];
            y += interpolation[1];
            drawnTransform.addScaleX(interpolation[2]);
            drawnTransform.addScaleY(interpolation[3]);
            drawnTransform.addRotation(interpolation[4]);

            drawnTransform.setFlipX(drawnTransform.getFlipX() && first.flipX);
            drawnTransform.setFlipY(drawnTransform.getFlipY() && first.flipY);
        }

        drawn.getFrame().draw(x, y, drawnTransform);
    }

    @Override
    public long getLength() {
        return this.length;
    }

    // ---------------- methods ----------------

    /**
     * Returns the current {@link KeyFrame} to be rendered,
     * along with the KeyFrame after for interpolation
     * should the latter exist.
     * 
     * @return a Pair of KeyFrames, the first being the
     *         current KeyFrame, the second being the
     *         KeyFrame following the current one or null if
     *         the current is the last
     */
    public Pair<KeyFrame> getCurrentFrame() {
        KeyFrame previous = null;
        for (Long key : this.frames.keySet()) {
            KeyFrame frame = this.frames.get(key);
            if (this.getCycleRuntime() >= key) {
                returnBuffer.setItems(frame, previous);
                break;
            }

            previous = frame;
        }

        return returnBuffer;
    }
}
