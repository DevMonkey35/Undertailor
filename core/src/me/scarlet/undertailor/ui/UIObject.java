package me.scarlet.undertailor.ui;

import static me.scarlet.undertailor.Undertailor.warn;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.ui.event.UIEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UIObject {
    
    protected int id;
    private float alpha;
    private Vector3 position;
    private boolean isAlwaysActive;
    private List<UIComponent> marked;
    private List<UIComponent> components;
    
    public UIObject() {
        this(false);
    }
    
    public UIObject(boolean isAlwaysActive) {
        this.position = new Vector3(0, 0, 0);
        this.components = new ArrayList<>();
        this.marked = new ArrayList<>();
        this.isAlwaysActive = isAlwaysActive;
    }
    
    public int getId() {
        return id;
    }
    
    public boolean isAlwaysActive() {
        return this.isAlwaysActive;
    }
    
    public Vector3 getPosition() {
        return this.position;
    }
    
    public void pushEvent(UIEvent event) {
       this.components.forEach(component -> {
            if(this.isComponentActive(component)) {
                component.onEvent(event);
            }
        });
       
       cleanup();
    }
    
    public void process(float delta) {
        this.components.forEach(component -> {
            if(this.isComponentActive(component)) {
                component.process(delta);
            }
        });
        
        cleanup();
    }
    
    public void render(Batch batch) {
        this.components.forEach(component -> {;
           if(this.isComponentActive(component)) {
               component.render(batch, alpha);
           }
        });
    }
    
    public void destroy() {
        for(UIComponent child : components) {
            child.onDestroy(true);
        }
        
        Undertailor.getUIController().destroyObject(id);
    }
    
    public boolean isComponentActive(UIComponent component) {
        if(!component.getParent().equals(this)) {
            throw new IllegalArgumentException("Component was not a child");
        } else {
            if(this.marked.contains(component)) {
                return false;
            } else {
                if(this.components.isEmpty()) {
                    return component.isAlwaysActive(); // weird state, but whatever
                } else {
                    return component.isAlwaysActive() || component.equals(this.components.get(this.components.size() - 1));
                }
            }
        }
    }
    
    public void registerChild(UIComponent component) {
        component.parent = this;
        this.components.add(component);
    }
    
    public void destroyChild(UIComponent component) {
        if(component.getParent().equals(this)) {
            this.marked.add(component);
            component.onDestroy(false);
        } else {
            warn("ui", "request ignored to destroy non-child component");
        }
    }
    
    private void cleanup() {
        Iterator<UIComponent> marked = this.marked.iterator();
        while(marked.hasNext()) {
            UIComponent component = marked.next();
            this.components.remove(component);
            marked.remove();
        }
    }
}
