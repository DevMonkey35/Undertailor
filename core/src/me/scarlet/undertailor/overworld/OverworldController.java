package me.scarlet.undertailor.overworld;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.collision.CollisionHandler;
import me.scarlet.undertailor.overworld.WorldRoom.Entrypoint;
import me.scarlet.undertailor.scheduler.Task;
import me.scarlet.undertailor.util.InputRetriever.InputData;

import java.util.Iterator;
import java.util.Set;

public class OverworldController {
    
    public static final int RENDER_WIDTH = 320;
    public static final int RENDER_HEIGHT = 240;
    public static final String MANAGER_TAG = "overworld";
    
    private float zoom;
    private long charId;
    private Viewport port;
    private Rectangle scissor;
    private boolean isRendering;
    private boolean isProcessing, oldIsProcessing;
    private boolean cameraFixing;
    private WorldRoom currentRoom;
    private boolean renderHitboxes;
    private OrthographicCamera camera;
    private CollisionHandler collision;
    private Task entryTransition, exitTransition;
    
    private WorldObjectLoader loader;
    
    public OverworldController(Viewport port) {
        this.loader = new WorldObjectLoader();
        this.camera = new OrthographicCamera(RENDER_WIDTH, RENDER_HEIGHT);
        this.setViewport(port);
        this.charId = -1;
        
        this.zoom = 1.0F;
        this.isRendering = true;
        this.oldIsProcessing = true;
        this.isProcessing = true;
        this.cameraFixing = true;
        this.renderHitboxes = true;
        this.entryTransition = null;
        this.exitTransition = null;
        this.collision = new CollisionHandler();
        this.scissor = new Rectangle();
        ScissorStack.calculateScissors(camera, Undertailor.getRenderer().getSpriteBatch().getTransformMatrix(), Undertailor.RENDER_AREA, scissor);
    }
    
    public WorldObjectLoader getObjectLoader() {
        return loader;
    }
    
    public OrthographicCamera getCamera() {
        return camera;
    }
    
    public void setEntryTransition(Task transition) {
        this.entryTransition = transition;
    }
    
    public void setExitTransition(Task transition) {
        this.exitTransition = transition;
    }
    
    public long getCharacterID() {
        return charId;
    }
    
    public void setCharacterID(long id) {
        this.charId = id;
    }
    
    public WorldRoom getCurrentRoom() {
        return currentRoom;
    }
    
    public void setCurrentRoom(WorldRoom room, boolean transitions, String entrypointExit, String entrypointEnter) {
        if(transitions) {
            if(exitTransition != null) {
                Undertailor.getScheduler().registerTask(exitTransition, true);
            }
            
            Undertailor.getScheduler().registerTask(new Task() {
                @Override
                public String getName() {
                    return "_tailor-setroom";
                }
                
                @Override
                public boolean process(float delta, InputData input) {
                    setCurrentRoom(room, entrypointExit, entrypointEnter);
                    return true;
                }

                @Override
                public void onFinish(boolean forced) {} // don't care
            }, true);
            
            if(entryTransition != null) {
                Undertailor.getScheduler().registerTask(entryTransition, true);
            }
        } else {
            setCurrentRoom(room, entrypointExit, entrypointEnter);
        }
    }
    
    private void setCurrentRoom(WorldRoom room, String entrypointExit, String entrypointEnter) {
        Set<WorldObject> persisted = null;
        if(this.currentRoom != null) {
            persisted = this.currentRoom.prepareExit();
            this.currentRoom.onExit(currentRoom.getEntrypoint(entrypointExit));
        }
        
        this.currentRoom = room;
        Entrypoint enterpoint = room.getEntrypoint(entrypointEnter);
        if(persisted != null && !persisted.isEmpty()) {
            Iterator<WorldObject> iterator = persisted.iterator();
            iterator.forEachRemaining(object -> {
                this.currentRoom.registerPersistentObject(object);
                try {
                    object.onPersist(room, enterpoint);
                } catch(Exception e) {
                    Undertailor.instance.warn(MANAGER_TAG, "persist method for object " + object.getObjectName() + " with id " + object.getId() + " threw an error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                }
            });
        }
        
        room.onEnter(enterpoint);
        float rmX = currentRoom.getMap().getSizeX() * 20;
        float rmY = currentRoom.getMap().getSizeY() * 20;
        this.setCameraPosition(rmX / 2.0F, rmY / 2.0F);
    }
    
    public Vector2 getCameraPosition() {
        return new Vector2(camera.position.x, camera.position.y);
    }
    
    public void setCameraPosition(float x, float y) {
        camera.position.set(x, y, 0);
        fixPosition();
        camera.update();
    }
    
    public float getCameraZoom() {
        return zoom;
    }
    
    public void setCameraZoom(float zoom) {
        this.zoom = zoom;
        camera.zoom = 1 / zoom;
        fixPosition();
        camera.update();
    }
    
    private void fixPosition() {
        if(this.currentRoom == null) {
            return;
        }
        
        if(cameraFixing) {
            float rmX = currentRoom.getMap().getSizeX() * 20;       // room's width
            float rmY = currentRoom.getMap().getSizeY() * 20;       // room's height
            float cvX = Math.abs(camera.zoom) * camera.viewportWidth / 2.0F;  // half of camera's view width
            float cvY = Math.abs(camera.zoom) * camera.viewportHeight / 2.0F; // half of camera's view height
            float xPos = camera.position.x;
            float yPos = camera.position.y;
            
            if(cvX * 2 >= rmX) {
                xPos = rmX / 2.0F;
            } else {
                if(xPos < cvX) {
                    xPos = cvX;
                } else if(xPos > rmX - cvX) {
                    xPos = rmX - cvX;
                }
            }
            
            if(cvY * 2 >= rmY) {
                yPos = rmY / 2.0F;
            } else {
                if(yPos < cvY) {
                    yPos = cvY;
                } else if(yPos > rmY - cvY) {
                    yPos = rmY - cvY;
                }
            }
            
            camera.position.set(xPos, yPos, 0);
        }
    }
    
    public boolean isRenderingHitboxes() {
        return renderHitboxes;
    }
    
    public void setRenderingHitboxes(boolean flag) {
        this.renderHitboxes = flag;
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
        this.oldIsProcessing = isProcessing;
        this.isProcessing = flag;
    }
    
    public boolean isCameraFixing() {
        return cameraFixing;
    }
    
    public void setCameraFixing(boolean flag) {
        this.cameraFixing = flag;
    }
    
    public void render() {
        if(!isRendering) {
            return;
        }
        
        if(currentRoom != null) {
            ScissorStack.pushScissors(scissor);
            Undertailor.getRenderer().setProjectionMatrix(camera.combined);
            currentRoom.render();
            ScissorStack.popScissors();
        }
    }
    
    public void process(float delta, InputData input) {
        if(this.isProcessing != this.oldIsProcessing && currentRoom != null) {
            this.oldIsProcessing = this.isProcessing;
            if(isProcessing) {
                currentRoom.resume();
            } else {
                currentRoom.pause();
            }
        }
        
        if(currentRoom != null) {
            currentRoom.forceProcess();
            if(isProcessing) currentRoom.process(collision, delta, input);
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
