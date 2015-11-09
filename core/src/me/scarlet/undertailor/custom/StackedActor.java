package me.scarlet.undertailor.custom;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class StackedActor extends Actor {
    
    public boolean allowNextActor() {
        return true;
    }
    
    public boolean allowNextStage() {
        return true;
    }
    
    public boolean removeOnFinish() {
        return false;
    }
    
    public boolean isFinished() {
        return false;
    }
}
