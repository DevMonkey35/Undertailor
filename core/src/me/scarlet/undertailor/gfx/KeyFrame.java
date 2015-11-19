package me.scarlet.undertailor.gfx;

public class KeyFrame {
    
    public static class FrameObjectMeta {
        public float offX, offY, scaleX, scaleY, rotation;
        public boolean smooth, flipX, flipY;
        public int smoothingType;
        
        public FrameObjectMeta() {
            this.offX = 0F;
            this.offY = 0F;
            this.scaleX = 1.0F;
            this.scaleY = 1.0F;
            this.rotation = 0F;
            this.flipX = false;
            this.flipY = false;
            this.smooth = false;
            // smooth tells animator to smoothly transition to this meta's values from the last frame
            this.smoothingType = 0; // 0 = linear, -1 = fast->slow, 1 = slow->fast
            // smoothingType tells how smoothing should occur; linearly or exponentially
        }
    }
    
    public static class SimpleKeyFrame extends KeyFrame {
        private Sprite sprite;
        private FrameObjectMeta meta;
        
        public SimpleKeyFrame(Sprite sprite, long frameTime) {
            this(sprite, frameTime, new FrameObjectMeta());
        }
        
        public SimpleKeyFrame(Sprite sprite, long frameTime, FrameObjectMeta meta) {
            super(frameTime);
            this.sprite = sprite;
            this.meta = meta;
        }
        
        public Sprite getSprite() {
            return sprite;
        }
        
        public FrameObjectMeta getMeta() {
            return meta;
        }
    }
    
    private long frameTime; // how long this frame takes to complete
    protected KeyFrame(long frameTime) {
        this.frameTime = frameTime;
    }
    
    public long getFrameTime() {
        return frameTime;
    }
}
