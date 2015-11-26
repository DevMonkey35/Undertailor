package me.scarlet.undertailor.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import me.scarlet.undertailor.ui.event.UIEvent;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;

public class UIController {
    
    /** Next ID holder for incoming generations UI objects. */
    private static int nextUID;
    
    static {
        UIController.nextUID = 0;
    }
    
    /** Map holding all headed UI objects. */
    private SortedMap<Integer, UIObject> uis;
    
    public UIController() {
        this.uis = new TreeMap<>(((Comparator<Integer>) (Integer i1, Integer i2) -> {
                    return i1.compareTo(i2);
                }));
    }
    
    public UIObject getUIObject(int id) {
        return uis.get(id);
    }
    
    public int registerObject(UIObject object) {
        int id = nextUID++;
        this.uis.put(id, object);
        object.id = id;
        
        return id;
    }
    
    public void destroyObject(int id) {
        this.uis.remove(id);
    }
    
    public void pushEvent(UIEvent event) {
        this.processObjects(object -> {
            object.pushEvent(event);
        }, false);
    }
    
    public void process(float delta) {
        this.processObjects(object -> {
            object.process(delta);
        }, false);
    }
    
    public void render(Batch batch) {
        this.processObjects(object -> {
            object.render(batch);
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
                if(!object.isAlwaysActive() && !(object instanceof HeadlessUIObject)) {
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
}
