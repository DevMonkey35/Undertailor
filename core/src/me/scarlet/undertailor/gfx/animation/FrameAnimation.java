package me.scarlet.undertailor.gfx.animation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.animation.FrameAnimation.KeyFrame.KeyFrameData;
import me.scarlet.undertailor.util.NumberUtil;
import me.scarlet.undertailor.util.NumberUtil.Interpolator;
import me.scarlet.undertailor.util.Pair;

import java.util.TreeMap;

public class FrameAnimation extends Animation {

    /**
     * Holds data for key frames making up a
     * {@link FrameAnimation}.
     */
    public static class KeyFrame {

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
         * @return a KeyFrameData
         */
        public KeyFrameData getFrameData() {
            return this.data;
        }
    }

    private long length;
    private TreeMap<Long, KeyFrame> frames;
    private Pair<KeyFrame> returnBuffer;

    public FrameAnimation(KeyFrame... frames) {
        this.returnBuffer = new Pair<>();
        this.frames = new TreeMap<>((time1, time2) -> {
            return Long.compare(time2, time1);
        });

        for (KeyFrame frame : frames) {
            this.frames.put(frame.time, frame);
        }

        this.length = this.frames.firstKey();
    }

    public long getRealRuntime() {
        long runtime = this.getRuntime();
        if (this.isLooping()) {
            if (runtime > this.length)
                runtime -= this.length * Math.floor((runtime / this.length));
        } else {
            if (runtime > this.length)
                runtime = this.length;
        }

        return runtime;
    }

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
            if (this.getRealRuntime() >= key) {
                returnBuffer.setItems(frame, previous);
                break;
            }

            previous = frame;
        }

        return returnBuffer;
    }

    static Logger log = LoggerFactory.getLogger(FrameAnimation.class);

    @Override
    public void draw(float x, float y, float scaleX, float scaleY, boolean flipX, boolean flipY,
        float rotation) {

        log.info("Runtime: " + this.getRuntime());

        Pair<KeyFrame> frames = this.getCurrentFrame();
        KeyFrame drawn = frames.getFirst();
        if (frames.getSecond() == null) { // last frame? just draw it
            log.info("Last frame.");
            KeyFrameData data = frames.getFirst().getFrameData();

            x += data.x;
            y += data.y;
            scaleX += data.scaleX;
            scaleY += data.scaleY;
            flipX = flipX && data.flipX;
            flipY = flipY && data.flipY;
            rotation += data.rotation;
        } else {
            // interpolation
            KeyFrameData first = drawn.getFrameData();
            KeyFrameData second = frames.getSecond().getFrameData();
            long realRuntime = this.getRealRuntime();
            float currentFrameProgress = (float) (realRuntime - frames.getFirst().getKeyTime())
                / (frames.getSecond().getKeyTime() - frames.getFirst().getKeyTime());

            log.info("Interpolation progress: " + currentFrameProgress);
            float[] interpolation = first.interpolateValues(second, currentFrameProgress);
            x += interpolation[0];
            y += interpolation[1];
            scaleX += interpolation[2];
            scaleY += interpolation[3];
            rotation += interpolation[4];

            flipX = flipX && first.flipX;
            flipY = flipY && first.flipY;
        }

        drawn.getFrame().draw(x, y, scaleX, scaleY, flipX, flipY, rotation);
    }
}
