package me.scarlet.undertailor.environment;

import com.badlogic.gdx.utils.Disposable;
import me.scarlet.undertailor.manager.EnvironmentManager;
import me.scarlet.undertailor.util.InputRetriever.InputData;

public class Environment implements Disposable {
    
    private EnvironmentManager envMan;
    private String name;
    
    private OverworldController ovw;
    private Scheduler scheduler;
    private UIController ui;
    
    public Environment(EnvironmentManager envMan, String name) {
        this.envMan = envMan;
        this.name = name;
        
        this.ovw = new OverworldController(this, envMan.generateViewport());
        this.ui = new UIController(this, envMan.generateViewport());
        this.scheduler = new Scheduler(this);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void process(float delta, InputData input) {
        this.scheduler.process(delta, input);
        this.ui.process(delta, input);
        this.ovw.process(delta, input);
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
