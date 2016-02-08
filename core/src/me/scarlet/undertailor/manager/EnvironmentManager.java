package me.scarlet.undertailor.manager;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.scarlet.undertailor.environment.Environment;
import me.scarlet.undertailor.environment.overworld.WorldObjectLoader;
import me.scarlet.undertailor.environment.overworld.map.RoomLoader;
import me.scarlet.undertailor.environment.ui.UIComponentLoader;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentManager {
    
    public enum ViewportType {
        STRETCH,
        FIT
    }
    
    private Map<String, Environment> environments;
    private String activeEnvironment;
    
    private ViewportType currentViewportType;

    private boolean renderHitboxes;
    private UIComponentLoader uiLoader;
    private WorldObjectLoader objLoader;
    private RoomLoader roomLoader;
    
    public EnvironmentManager() {
        this.renderHitboxes = true;
        this.environments = new HashMap<>();
        this.currentViewportType = ViewportType.FIT;
        
        this.uiLoader = new UIComponentLoader();
        this.objLoader = new WorldObjectLoader();
        this.roomLoader = new RoomLoader();
    }

    
    public boolean isRenderingHitboxes() {
        return this.renderHitboxes;
    }
    
    public Viewport generateViewport() {
        switch(currentViewportType) {
            case STRETCH:
                return new StretchViewport(0F, 0F);
            case FIT:
                return new FitViewport(0F, 0F);
            default:
                return null;
        }
    }
    
    public ViewportType getCurrentViewportType() {
        return this.currentViewportType;
    }
    
    public void setCurrentViewportType(ViewportType type) {
        this.currentViewportType = type;
        
        for(Environment env : environments.values()) {
            env.updateViewport();
        }
    }
    
    public Environment getActiveEnvironment() {
        return this.environments.get(activeEnvironment);
    }
    
    public void setActiveEnvironment(Environment environment) {
        this.activeEnvironment = environment.getName();
    }
    
    public UIComponentLoader getUIComponentLoader() {
        return this.uiLoader;
    }
    
    public WorldObjectLoader getWorldObjectLoader() {
        return this.objLoader;
    }
    
    public RoomLoader getRoomLoader() {
        return this.roomLoader;
    }
    
    public boolean hasEnvironment(String name) {
        return environments.containsKey(name);
    }
    
    public Environment getEnvironment(String name) {
        if(!environments.containsKey(name)) {
            this.environments.put(name, new Environment(this, name));
        }
        
        return environments.get(name);
    }
    
    public void destroyEnvironment(String name) {
        if(hasEnvironment(name)) {
            environments.get(name).dispose();
            environments.remove(name);
        }
    }
    
    public void resize(int width, int height) {
        for(Environment env : environments.values()) {
            env.resize(width, height);
        }
    }
}
