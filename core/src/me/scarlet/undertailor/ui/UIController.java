package me.scarlet.undertailor.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import me.scarlet.undertailor.ui.event.UIEvent;
import me.scarlet.undertailor.util.MapUtil;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

public class UIController {
    
    /** Next ID holder for incoming generations UI objects. */
    private static int nextUID;
    
    static {
        UIController.nextUID = 0;
    }
    
    /** Map holding all headed UI objects. */
    private Map<Integer, UIObject> uis;
    /** Map holding all continuously active headed UI objects. */
    private Map<Integer, UIObject> auis;
    /** Map holding all headless UI objects. */
    private Map<Integer, HeadlessUIObject> huis;
    
    public UIController() {
        this.uis = new LinkedHashMap<Integer, UIObject>();
        this.auis = new LinkedHashMap<Integer, UIObject>();
        this.huis = new LinkedHashMap<Integer, HeadlessUIObject>();
    }
    
    public UIObject getUIObject(int id) {
        return uis.get(id);
    }
    
    public int registerObject(UIObject object) {
        int id = nextUID++;
        if(object instanceof HeadlessUIObject) {
            this.huis.put(id, (HeadlessUIObject) object);
        } else {
            if(object.isAlwaysActive()) {
                this.auis.put(id, object);
            } else {
                this.uis.put(id, object);
            }
        }
        
        object.id = id;
        
        return id;
    }
    
    public void destroyObject(int id) {
        this.huis.remove(id);
        this.auis.remove(id);
        this.uis.remove(id);
    }
    
    public void pushEvent(UIEvent event) {
        Entry<Integer, UIObject> entry = MapUtil.getLastEntry(this.uis);
        if(entry != null) entry.getValue().pushEvent(event); // process top-most only
        this.auis.values().forEach(object -> {
            object.pushEvent(event);
        });
        
        this.huis.values().forEach(object -> {
            object.pushEvent(event);
        });
    }
    
    public void process(float delta) {
        Entry<Integer, UIObject> entry = MapUtil.getLastEntry(this.uis);
        if(entry != null) entry.getValue().process(delta); // process top-most only
        this.auis.values().forEach(object -> {
            object.process(delta);
        });
        
        this.huis.values().forEach(object -> {
            object.process(delta);
        });
    }
    
    public void render(Batch batch) {
        // sort in order of registration
        SortedMap<Integer, UIObject> objects = new TreeMap<>(((Comparator<Integer>) (Integer i1, Integer i2) -> {
            return i1.compareTo(i2);
        }));
        
        this.auis.entrySet().forEach(entry -> {
            objects.put(entry.getKey(), entry.getValue());
        });
        
        this.huis.entrySet().forEach(entry -> {
            objects.put(entry.getKey(), entry.getValue());
        });
        
        this.uis.entrySet().forEach(entry -> {
            objects.put(entry.getKey(), entry.getValue());
        });
        
        // actually render
        objects.values().forEach(object -> {
            object.render(batch);
        });
        
        if(batch.isDrawing()) batch.end();
    }
}
