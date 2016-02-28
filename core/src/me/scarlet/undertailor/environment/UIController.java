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

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.environment.event.EventData;
import me.scarlet.undertailor.environment.event.EventListener;
import me.scarlet.undertailor.environment.event.EventReceiver;
import me.scarlet.undertailor.environment.ui.UIObject;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.util.Renderable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;

public class UIController implements Renderable, EventReceiver, EventListener {
    
    /** Next ID holder for incoming generations UI objects. */
    private static int nextUID;
    
    public static final int RENDER_WIDTH = 640;
    public static final int RENDER_HEIGHT = 480;
    
    static {
        UIController.nextUID = 0;
    }
    
    /** Map holding all headed UI objects. */
    private Map<String, EventReceiver> listeners;
    private SortedMap<Integer, UIObject> uis;
    private OrthographicCamera camera;
    private Environment env;
    private Viewport port;
    
    public UIController(Environment env, Viewport port) {
        this.env = env;
        this.listeners = new HashMap<>();
        this.uis = new TreeMap<>(((Comparator<Integer>) Integer::compareTo));
        this.camera = new OrthographicCamera(RENDER_WIDTH, RENDER_HEIGHT);
        
        this.setViewport(port);
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
    
    public Environment getEnvironment() {
        return this.env;
    }
    
    public UIObject getUIObject(int id) {
        return uis.get(id);
    }
    
    public int registerObject(UIObject object) {
        int id = nextUID++;
        this.uis.put(id, object);
        object.claim(this, id);
        
        return id;
    }
    
    public boolean destroyObject(int id) {
        return this.uis.remove(id) != null;
    }
    
    public void process(float delta, InputData input) {
        if(uis.keySet().isEmpty()) {
            return;
        }
        
        this.processObjects(object -> object.process(delta, input), false);
        
        Iterator<Entry<Integer, UIObject>> iterator = uis.entrySet().iterator();
        while(iterator.hasNext()) {
            UIObject obj = iterator.next().getValue();
            if(obj.isPastLifetime()) {
                iterator.remove();
            }
        }
    }
    
    @Override
    public void pushEvent(EventData data) {
        this.listeners.values().forEach(receiver -> receiver.pushEvent(data));
        this.processObjects(ui -> ui.pushEvent(data), true);
    }
    
    public void render() {
        Undertailor.getRenderer().setProjectionMatrix(camera.combined);
        this.processObjects(UIObject::render, false);
    }
    
    private void processObjects(Consumer<UIObject> consumer, boolean ignoreActiveStack) {
        if(ignoreActiveStack) {
            for(Entry<Integer, UIObject> entry : uis.entrySet()) {
                UIObject object = entry.getValue();
                consumer.accept(object);
            }
        } else {
            UIObject active = null;
            for(Entry<Integer, UIObject> entry : uis.entrySet()) {
                UIObject object = entry.getValue();
                if(!object.isHeadless()) {
                    if(active == null) {
                        active = object;
                    }
                } else {
                    consumer.accept(object);
                }
            }
            
            if(active != null) {
                consumer.accept(active);
            }
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
