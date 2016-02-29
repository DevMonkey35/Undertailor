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

package me.scarlet.undertailor.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.scarlet.undertailor.environment.Environment;
import me.scarlet.undertailor.environment.Scheduler;
import me.scarlet.undertailor.environment.event.EventData;
import me.scarlet.undertailor.environment.event.EventListener;
import me.scarlet.undertailor.environment.event.EventReceiver;
import me.scarlet.undertailor.environment.overworld.WorldObjectLoader;
import me.scarlet.undertailor.environment.overworld.map.RoomLoader;
import me.scarlet.undertailor.environment.ui.UIComponentLoader;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentManager implements EventReceiver, EventListener {
    
    public enum ViewportType {
        STRETCH,
        FIT
    }
    
    private boolean isFullscreen;
    private int oldWidth, oldHeight;
    private String activeEnvironment;
    private Scheduler globalScheduler;
    private Map<String, Environment> environments;
    
    private ViewportType currentViewportType;

    private boolean renderHitboxes;
    private UIComponentLoader uiLoader;
    private WorldObjectLoader objLoader;
    private RoomLoader roomLoader;
    private Map<String, EventReceiver> listeners;
    
    public EnvironmentManager() {
        this.isFullscreen = false;
        this.renderHitboxes = true;
        this.environments = new HashMap<>();
        this.currentViewportType = ViewportType.FIT;
        
        this.listeners = new HashMap<>();
        this.uiLoader = new UIComponentLoader();
        this.objLoader = new WorldObjectLoader();
        this.roomLoader = new RoomLoader();
        this.globalScheduler = new Scheduler(null);
    }
    
    public boolean isFullscreen() {
        return this.isFullscreen;
    }
    
    public void setFullscreen(boolean flag) {
        if(flag != isFullscreen) {
            if(flag) {
                this.oldWidth = Gdx.graphics.getWidth();
                this.oldHeight = Gdx.graphics.getHeight();
                
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
                DisplayMode mode = Gdx.graphics.getDesktopDisplayMode();
                Gdx.graphics.setDisplayMode(mode.width, mode.height, false);
            } else {
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
                Gdx.graphics.setDisplayMode(oldWidth, oldHeight, false);
            }
            
            this.isFullscreen = flag;
        }
    }
    
    @Override
    public void addListener(String id, EventReceiver receiver) {
        this.listeners.put(id, receiver);
    }
    
    @Override
    public EventReceiver getListener(String id) {
        return this.listeners.get(id);
    }
    
    @Override
    public void removeListener(String id) {
        this.listeners.remove(id);
    }
    
    @Override
    public void clearListeners() {
        this.listeners.clear();
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
    
    public Scheduler getGlobalScheduler() {
        return this.globalScheduler;
    }
    
    public ViewportType getCurrentViewportType() {
        return this.currentViewportType;
    }
    
    public void setCurrentViewportType(ViewportType type) {
        this.currentViewportType = type;

        environments.values().forEach(Environment::updateViewport);
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
    
    @Override
    public void pushEvent(EventData data) {
        this.listeners.values().forEach(receiver -> receiver.pushEvent(data));
        if(this.getActiveEnvironment() != null) {
            this.getActiveEnvironment().pushEvent(data);
        }
    }
    
    public void resize(int width, int height) {
        for(Environment env : environments.values()) {
            env.resize(width, height);
        }
    }
}
