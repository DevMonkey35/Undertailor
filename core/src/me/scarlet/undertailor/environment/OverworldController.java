/* 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.environment.overworld.WorldObject;
import me.scarlet.undertailor.environment.overworld.WorldRoom;
import me.scarlet.undertailor.environment.overworld.WorldRoom.Entrypoint;
import me.scarlet.undertailor.environment.overworld.map.RoomMap;
import me.scarlet.undertailor.environment.scheduler.Task;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.util.Renderable;

import java.util.Iterator;
import java.util.Set;

public class OverworldController implements Renderable, Disposable {
    
    public static final int RENDER_WIDTH = 640;
    public static final int RENDER_HEIGHT = 480;
    public static final String MANAGER_TAG = "overworld";
    
    private float zoom;
    private long charId;
    private Viewport port;
    private boolean isRendering;
    private boolean isProcessing, oldIsProcessing;
    private boolean cameraFixing;
    private WorldRoom currentRoom;
    private boolean renderHitboxes;
    private OrthographicCamera camera;
    private Task entryTransition, exitTransition;
    
    //private WorldObjectLoader objLoader;
    //private RoomLoader roomLoader;
    private Environment env;
    
    public OverworldController(Environment env, Viewport port) {
        this.camera = new OrthographicCamera(RENDER_WIDTH, RENDER_HEIGHT);
        this.env = env;
        this.setViewport(port);
        this.charId = -1;
        
        this.zoom = 2.0F;
        this.isRendering = true;
        this.oldIsProcessing = true;
        this.isProcessing = true;
        this.cameraFixing = true;
        this.renderHitboxes = true;
        this.entryTransition = null;
        this.exitTransition = null;
        
        this.setCameraZoom(zoom);
    }
    
    public OrthographicCamera getCamera() {
        return camera;
    }
    
    public Environment getEnvironment() {
        return this.env;
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
            Scheduler scheduler = env.getScheduler();
            
            if(exitTransition != null) {
                scheduler.registerTask(exitTransition, true);
            }
            
            scheduler.registerTask(new Task() {
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
                scheduler.registerTask(entryTransition, true);
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
            this.currentRoom.dispose();
        }
        
        room.claim(this);
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
        if(currentRoom.getMap() != null) {
            float rmX = currentRoom.getMap().getReference().getSizeX() * 20;
            float rmY = currentRoom.getMap().getReference().getSizeY() * 20;
            this.setCameraPosition(rmX / 2.0F, rmY / 2.0F);
        } else {
            this.setCameraPosition(0, 0);
        }
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
        
        if(cameraFixing && currentRoom.getMap() != null) {
            RoomMap map = currentRoom.getMap().getReference();
            float rmX = map.getSizeX() * 20;       // room's width
            float rmY = map.getSizeY() * 20;       // room's height
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
            Undertailor.getRenderer().setProjectionMatrix(camera.combined);
            currentRoom.render();
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
            if(isProcessing) currentRoom.process(delta, input);
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
    }

    @Override
    public void dispose() {
        if(this.currentRoom != null) {
            this.currentRoom.dispose();
        }
    }
}
