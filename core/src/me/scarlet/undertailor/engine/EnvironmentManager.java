package me.scarlet.undertailor.engine;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import me.scarlet.undertailor.LaunchOptions.ViewportType;
import me.scarlet.undertailor.Undertailor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Manager class for {@link Environment} instances.
 */
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

    // ---------------- abstract method implementation ----------------

    @Override
    public boolean catchEvent(String eventName, Map<String, Object> data) {
        return false;
    }

    // ---------------- object ----------------

    /**
     * Returns the {@link Environment} of the given name,
     * creating it if it has yet to exist.
     * 
     * @param name the name of the Environment
     * 
     * @return the Environment under the given name, a new
     *         one if it didn't exist prior
     */
    public Environment getEnvironment(String name) {
        if (!this.environments.containsKey(name)) {
            this.environments.put(name, new Environment(tailor));
            this.environments.get(name).setViewport(this.viewportType);
        }

        return this.environments.get(name);
    }

    /**
     * Destroys the {@link Environment} of the given name.
     * 
     * @param name the name of the environment
     */
    public void destroyEnvironment(String name) {
        if (this.environments.containsKey(name)) {
            this.environments.get(name).destroy();
            this.environments.remove(name);
        }
    }

    /**
     * Returns the current active {@link Environment}.
     * 
     * @return the curret active Environment
     */
    public Environment getActiveEnvironment() {
        return this.environments.get(this.activeEnvironment);
    }

    /**
     * Sets the current active {@link Environment}.
     * 
     * @param env the new active Environment
     */
    public void setActiveEnvironment(Environment env) {
        this.activeEnvironment = null;

        for (Entry<String, Environment> entry : this.environments.entrySet()) {
            if (entry.getValue().equals(env)) {
                this.activeEnvironment = entry.getKey();
                break;
            }
        }
    }

    // ---------------- internal ----------------

    /**
     * Internal method.
     * 
     * <p>Although public, should only be called by the
     * system.</p>
     */
    public void resize(int width, int height) {
        this.environments.values().forEach(env -> env.resize(width, height));
    }
}
