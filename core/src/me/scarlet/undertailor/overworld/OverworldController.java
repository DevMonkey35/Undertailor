package me.scarlet.undertailor.overworld;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.LuaRoom;
import me.scarlet.undertailor.test.CharacterFrisk;
import me.scarlet.undertailor.util.ParameterizedRunnable;

public class OverworldController {
    
    public static final int RENDER_WIDTH = 320;
    public static final int RENDER_HEIGHT = 240;
    
    private Viewport port;
    private OrthographicCamera camera;
    private boolean isRendering;
    private boolean isProcessing;
    private WorldRoom currentRoom;
    private ParameterizedRunnable<?> luaTransition;
    
    public OverworldController(Viewport port) {
        this.camera = new OrthographicCamera(RENDER_WIDTH, RENDER_HEIGHT);
        this.setViewport(port);
        
        this.isRendering = true;
        this.isProcessing = true;
        
        try {
            this.currentRoom = new LuaRoom(Undertailor.getRoomManager().getRoom("room1")).getRoom();
        } catch(LuaScriptException e) {
            e.printStackTrace();
        }
        
        this.currentRoom.registerObject(new CharacterFrisk());
        setCameraZoom(1.0f);
    }
    
    public OrthographicCamera getCamera() {
        return camera;
    }
    
    public WorldRoom getCurrentRoom() {
        return currentRoom;
    }
    
    public void setCurrentRoom(WorldRoom room) {
        this.currentRoom = room;
    }
    
    public Vector2 getCameraPosition() {
        return new Vector2(camera.position.x, camera.position.y);
    }
    
    public void setCameraPosition(float x, float y) {
        camera.position.set(x, y, 0);
        camera.update();
    }
    
    public float getCameraZoom() {
        return camera.zoom;
    }
    
    public void setCameraZoom(float zoom) {
        camera.zoom = zoom;
        camera.update();
    }
    
    public boolean isRendering() {
        return isRendering;
    }
    
    public void setRendering(boolean flag) {
        this.isRendering = flag;
    }
    
    public boolean isProcessing() {
        return isProcessing;
    }
    
    public void setProcessing(boolean flag) {
        this.isProcessing = flag;
    }
    
    public void render() {
        if(!isRendering) {
            return;
        }
        
        if(currentRoom != null) {
            Undertailor.getRenderer().setProjectionMatrix(camera.combined);
            currentRoom.render();
        }
    }
    
    public void process(float delta) {
        if(!isProcessing) {
            return;
        }
        
        if(currentRoom != null) {
            currentRoom.process(delta);
        }
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
