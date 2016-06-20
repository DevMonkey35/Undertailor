package me.scarlet.undertailor.engine;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.engine.overworld.OverworldController;
import me.scarlet.undertailor.engine.scheduler.Scheduler;
import me.scarlet.undertailor.engine.ui.UIController;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

public class Environment implements Processable, Renderable {

    private UIController ui;
    private Scheduler scheduler;
    private OverworldController overworld;

    public Environment(Undertailor tailor) {
        this.scheduler = new Scheduler(this);
        this.overworld = new OverworldController(tailor.getRenderer(), this, new FitViewport(640, 480));
        this.ui = new UIController();
    }

    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public void setTransform(Transform transform) {}

    @Override
    public void draw(float x, float y, Transform transform) {
        this.overworld.draw();
        this.ui.draw();
    }

    @Override
    public boolean process(Object... params) {
        scheduler.process();
        ui.process();
        overworld.process();
        return true;
    }

    public Scheduler getScheduler() {
        return this.scheduler;
    }

    public OverworldController getOverworld() {
        return this.overworld;
    }

    public UIController getUI() {
        return this.ui;
    }
    
    public void setViewport(Viewport viewport) {
        
    }
    
    public void resize(int width, int height) {
        this.overworld.resize(width, height);
    }
}
