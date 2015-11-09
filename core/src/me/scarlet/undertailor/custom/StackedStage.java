package me.scarlet.undertailor.custom;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class StackedStage extends Stage {
    
    public StackedStage() {
        super();
    }
    
    public StackedStage(Viewport port) {
        super(port);
    }
    
    public StackedStage(Viewport port, Batch batch) {
        super(port, batch);
    }
    
    public boolean allowNextStage() {
        return true;
    }
    
    @Override
    public void act(float delta) {
        for(Actor actor : this.getActors()) {
            actor.act(delta);
            if(actor instanceof StackedActor) {
                if(!((StackedActor) actor).allowNextActor()) {
                    break;
                }
            }
        }
    }
}
