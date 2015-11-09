package me.scarlet.undertailor.custom;

import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;
import java.util.List;

public class MultiStage {
    private List<Stage> stages;
    
    public MultiStage(Stage... stages) {
        this.stages = new ArrayList<>();
        
        for(Stage stage: stages) {
            this.stages.add(stage);
        }
    }
    
    public List<Stage> getStages() {
        return stages;
    }
    
    public void act(float delta) {
        boolean allowAct = true;
        for(Stage stage : stages) {
            if(!allowAct) {
                break;
            }
            
            stage.act(delta);
            if(stage instanceof StackedStage) {
                allowAct = ((StackedStage) stage).allowNextStage();
            }
        }
    }
    
    public void draw() {
        boolean allowDraw = true;
        for(Stage stage : stages) {
            if(!allowDraw) {
                break;
            }
            
            stage.draw();
            if(stage instanceof StackedStage) {
                allowDraw = ((StackedStage) stage).allowNextStage();
            }
        }
    }
}
