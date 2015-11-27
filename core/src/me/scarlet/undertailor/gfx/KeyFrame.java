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
        private int spriteIndex;
        private FrameObjectMeta meta;
        
        public SimpleKeyFrame(int spriteIndex, long frameTime) {
            this(spriteIndex, frameTime, new FrameObjectMeta());
        }
        
        public SimpleKeyFrame(int spriteIndex, long frameTime, FrameObjectMeta meta) {
            super(frameTime);
            this.spriteIndex = spriteIndex;
            this.meta = meta;
        }
        
        public int getSpriteIndex() {
            return spriteIndex;
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
