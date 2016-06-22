package me.scarlet.undertailor.engine;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import me.scarlet.undertailor.LaunchOptions.ViewportType;
import me.scarlet.undertailor.Undertailor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class EnvironmentManager implements EventListener {

    private Undertailor tailor;
    private String activeEnvironment;
    private Map<String, Environment> environments;
    private Class<? extends Viewport> viewportType;

    public EnvironmentManager(Undertailor tailor) {
        this.tailor = tailor;
        this.activeEnvironment = null;
        this.environments = new HashMap<>();

        this.viewportType = tailor.getLaunchOptions().scaling == ViewportType.FIT
            ? FitViewport.class : StretchViewport.class;
    }

    @Override
    public boolean catchEvent(String eventName, Map<String, Object> data) {
        return false;
    }

    public Environment getEnvironment(String name) {
        if (!this.environments.containsKey(name)) {
            this.environments.put(name, new Environment(tailor));
            this.environments.get(name).setViewport(this.viewportType);
        }

        return this.environments.get(name);
    }

    public void deleteEnvironment(String name) {
        this.environments.remove(name);
    }

    public Environment getActiveEnvironment() {
        return this.environments.get(this.activeEnvironment);
    }

    public void setActiveEnvironment(Environment env) {
        this.activeEnvironment = null;

        for (Entry<String, Environment> entry : this.environments.entrySet()) {
            if (entry.getValue().equals(env)) {
                this.activeEnvironment = entry.getKey();
                break;
            }
        }
    }

    public void resize(int width, int height) {
        this.environments.values().forEach(env -> env.resize(width, height));
    }
}
