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

import com.badlogic.gdx.utils.Disposable;
import me.scarlet.undertailor.environment.event.EventData;
import me.scarlet.undertailor.environment.event.EventListener;
import me.scarlet.undertailor.environment.event.EventReceiver;
import me.scarlet.undertailor.manager.EnvironmentManager;
import me.scarlet.undertailor.util.InputRetriever.InputData;

import java.util.HashMap;
import java.util.Map;

public class Environment implements Disposable, EventReceiver, EventListener {
    
    private EnvironmentManager envMan;
    private String name;
    
    private Map<String, EventReceiver> listeners;
    private OverworldController ovw;
    private Scheduler scheduler;
    private UIController ui;
    
    public Environment(EnvironmentManager envMan, String name) {
        this.envMan = envMan;
        this.name = name;
        
        this.listeners = new HashMap<>();
        this.ovw = new OverworldController(this, envMan.generateViewport());
        this.ui = new UIController(this, envMan.generateViewport());
        this.scheduler = new Scheduler(this);
    }
    
    public String getName() {
        return this.name;
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
    
    public void process(float delta, InputData input) {
        this.scheduler.process(delta, input);
        this.ui.process(delta, input);
        this.ovw.process(delta, input);
    }
    
    @Override
    public void pushEvent(EventData data) {
        this.listeners.values().forEach(receiver -> receiver.pushEvent(data));
        this.ui.pushEvent(data);
        this.ovw.pushEvent(data);
    }
    
    public void render() {
        this.ovw.render();
        this.ui.render();
    }
    
    public Scheduler getScheduler() {
        return this.scheduler;
    }
    
    public OverworldController getOverworldController() {
        return this.ovw;
    }
    
    public UIController getUIController() {
        return this.ui;
    }
    
    public void resize(int width, int height) {
        this.ovw.resize(width, height);
        this.ui.resize(width, height);
    }
    
    public void updateViewport() {
        this.ovw.setViewport(envMan.generateViewport());
        this.ui.setViewport(envMan.generateViewport());
    }

    @Override
    public void dispose() {
        this.ovw.dispose();
    }
}
