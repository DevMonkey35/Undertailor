package me.scarlet.undertailor.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.ui.event.UIEvent;

import java.util.ArrayList;
import java.util.List;

public class UIObject {
    
    protected int id;
    private float alpha;
    private Vector3 position;
    private boolean isAlwaysActive;
    private List<UIComponent> components;
    
    public UIObject(boolean isAlwaysActive) {
        this.position = new Vector3(0, 0, 0);
        this.components = new ArrayList<>();
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
    }
    
    public void process(float delta) {
        this.components.forEach(component -> {
            if(this.isComponentActive(component)) {
                component.process(delta);
            }
        });
    }
    
    public void render(Batch batch) {
        this.components.forEach(component -> {;
           if(this.isComponentActive(component)) {
               component.render(batch, alpha);
           }
        });
    }
    
    public void destroy() {
        Undertailor.getUIController().destroyObject(id);
    }
    
    public boolean isComponentActive(UIComponent component) {
        if(!component.getParent().equals(this)) {
            throw new IllegalArgumentException("Component was not a child");
        } else {
            if(this.components.isEmpty()) {
                return component.isAlwaysActive(); // weird state, but whatever
            } else {
                return component.isAlwaysActive() || component.equals(this.components.get(this.components.size() - 1));
            }
        }
    }
    
    protected void registerChild(UIComponent component) {
        if(component.getParent().equals(this)) {
            this.components.add(component);
        }
    }
    
    public void destroyChild(UIComponent component) {
        if(component.getParent().equals(this)) {
            this.components.remove(component);
        }
    }
}
