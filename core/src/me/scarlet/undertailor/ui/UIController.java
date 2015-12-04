package me.scarlet.undertailor.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.ui.event.UIEvent;
import me.scarlet.undertailor.util.InputRetriever.InputData;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;

public class UIController {
    
    /** Next ID holder for incoming generations UI objects. */
    private static int nextUID;
    
    public static final int RENDER_WIDTH = 640;
    public static final int RENDER_HEIGHT = 480;
    
    static {
        UIController.nextUID = 0;
    }
    
    /** Map holding all headed UI objects. */
    private SortedMap<Integer, UIObject> uis;
    private UIComponentLoader lualoader;
    private OrthographicCamera camera;
    private Viewport port;
    
    public UIController(Viewport port) {
        this.uis = new TreeMap<>(((Comparator<Integer>) (Integer i1, Integer i2) -> {
                    return i1.compareTo(i2);
                }));
        this.camera = new OrthographicCamera(RENDER_WIDTH, RENDER_HEIGHT);
        this.setViewport(port);
        this.lualoader = new UIComponentLoader();
    }
    
    public UIObject getUIObject(int id) {
        return uis.get(id);
    }
    
    public UIComponentLoader getLuaLoader() {
        return lualoader;
    }
    
    public int registerObject(UIObject object) {
        int id = nextUID++;
        this.uis.put(id, object);
        object.id = id;
        if(object.isHeadless()) {
            object.startLifetime = TimeUtils.millis();
        }
        
        return id;
    }
    
    public boolean destroyObject(int id) {
        return this.uis.remove(id) != null;
    }
    
    public void pushEvent(UIEvent event) {
        this.processObjects(object -> {
            object.pushEvent(event);
        }, false);
    }
    
    public void process(float delta, InputData input) {
        if(uis.keySet().isEmpty()) {
            return;
        }
        
        this.processObjects(object -> {
            object.process(delta, input);
        }, false);
        
        Iterator<Entry<Integer, UIObject>> iterator = uis.entrySet().iterator();
        while(iterator.hasNext()) {
            UIObject obj = iterator.next().getValue();
            if(obj.isHeadless()) {
                if(TimeUtils.timeSinceMillis(obj.startLifetime) > obj.getLifetime()) {
                    iterator.remove();
                }
            }
        }
    }
    
    public void render() {
        Undertailor.getRenderer().setProjectionMatrix(camera.combined);
        this.processObjects(object -> {
            object.render();
        }, false);
    }
    
    private void processObjects(Consumer<UIObject> consumer, boolean all) {
        if(all) {
            for(Entry<Integer, UIObject> entry : uis.entrySet()) {
                UIObject object = entry.getValue();
                consumer.accept(object);
            }
        } else {
            UIObject active = null;
            for(Entry<Integer, UIObject> entry : uis.entrySet()) {
                UIObject object = entry.getValue();
                if(!object.isHeadless()) {
                    active = object;
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
