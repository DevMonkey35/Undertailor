package me.scarlet.undertailor.overworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.Animation;

import java.io.File;

public class OverworldController {
    
    public static final int RENDER_WIDTH = 320;
    public static final int RENDER_HEIGHT = 240;
    
    private Viewport port;
    private Camera camera;
    private Texture tmtest;
    private Animation<?> test;
    
    public OverworldController(Viewport port) {
        this.camera = new OrthographicCamera(RENDER_WIDTH, RENDER_HEIGHT);
        this.setViewport(port);
        tmtest = new Texture(Gdx.files.absolute(new File("tilemaps/tmtest.png").getAbsolutePath()));
        test = Undertailor.getAnimationManager().getAnimationSet("frisk").getReference(this).getAnimation("walk_down");
    }
    
    public void render() {
        //port.apply();
        Undertailor.getRenderer().setProjectionMatrix(camera.combined);
        
        Undertailor.getRenderer().draw(new TextureRegion(tmtest, tmtest.getWidth(), tmtest.getHeight()), 0, 0);
        test.drawCurrentFrame(TimeUtils.timeSinceMillis(test.getStartTime()), 160, 120);
    }
    
    public void setViewport(Viewport port) {
        this.port = port;
        this.port.setWorldHeight(RENDER_HEIGHT);
        this.port.setWorldWidth(RENDER_WIDTH);
        this.port.setCamera(camera);
        this.camera.position.set(this.camera.viewportWidth/2.0F, this.camera.viewportHeight/2.0F, 0.0F);
        this.camera.update();
    }
    
    public void resize(int width, int height) {
        this.port.update(width, height, false);
        this.camera.position.set(this.camera.viewportWidth/2.0F, this.camera.viewportHeight/2.0F, 0.0F);
        this.camera.update();
    }
}
