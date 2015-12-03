package me.scarlet.undertailor.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.scarlet.undertailor.Undertailor;

public class TextureRenderer {
    private float aliasing;
    private SpriteBatch batch;
    private FrameBuffer buffer;
    private TextureRegion texture;
    private Camera camera;
    
    private Viewport port;
    private int screenWidth, screenHeight;
    
    public TextureRenderer() {
        this.batch = Undertailor.getRenderer().getSpriteBatch();
        this.aliasing = 1.0F;
        this.texture = null;
        this.buffer = null;
        
        this.resize();
    }
    
    public float getAliasing() {
        return aliasing;
    }
    
    public void setAliasing(float aliasing) {
        this.aliasing = aliasing;
    }
    
    public Viewport getViewport() {
        return port;
    }
    
    public void setViewport(Viewport port) {
        this.port = port;
    }
    
    public void begin() {
        if(buffer == null) {
            buffer = new FrameBuffer(Format.RGB565, (int) (screenWidth * aliasing), (int) (screenHeight * aliasing), false);
            texture = new TextureRegion(buffer.getColorBufferTexture());
            texture.flip(false, true);
        }
        
        buffer.begin();
    }
    
    public void end() {
        if(buffer != null) {
            buffer.end();
            
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            batch.draw(texture, 0, 0, screenWidth, screenHeight);
            batch.end();
        }
    }
    
    public void resize() {
        buffer = null;
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.position.set(this.camera.viewportWidth/2.0F, this.camera.viewportHeight/2.0F, 0.0F);
        this.camera.update();
        this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();
    }
}
