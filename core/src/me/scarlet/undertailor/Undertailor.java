/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without
 * limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package me.scarlet.undertailor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.input.InputRetriever;
import me.scarlet.undertailor.resource.ResourceHandler;

/**
 * The entrypoint class to the base game.
 * 
 * <p>Let's be a little cleaner this time, shall we?</p>
 */
public class Undertailor extends ApplicationAdapter {

    static Logger log = LoggerFactory.getLogger(Undertailor.class);

    // ---------------- System variables -- Core variables. ----------------

    private LaunchOptions options;
    private LwjglApplicationConfiguration lwjglConfig;

    private InputRetriever input;
    private MultiRenderer renderer;
    private AssetManager assets;

    public Undertailor(LaunchOptions options, LwjglApplicationConfiguration lwjglConfig) {
        this.options = options;
        this.lwjglConfig = lwjglConfig;

        new ResourceHandler().start();

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            log.error("Uncaught exception. Program will close.", e);
            this.exit();
        });
    }

    // ---------------- g/s core variables ----------------

    /**
     * Returns the launch configuration that was used to
     * launch the game.
     * 
     * @return a {@link LaunchOptions} instance
     */
    public LaunchOptions getLaunchOptions() {
        return this.options;
    }

    /**
     * Returns the {@link LwjglApplicationConfiguration}
     * used to launch the application with.
     * 
     * @return the application configuration
     */
    public LwjglApplicationConfiguration getApplicationConfiguration() {
        return this.lwjglConfig;
    }

    /**
     * Returns the {@link InputRetriever} used to track
     * input frame-by-frame.
     * 
     * @return the InputRetriever
     */
    public InputRetriever getInput() {
        return this.input;
    }

    /**
     * Returns the {@link MultiRenderer} used to render
     * everything the game needs to display.
     * 
     * @return the MultiRenderer
     */
    public MultiRenderer getRenderer() {
        return this.renderer;
    }

    /**
     * Returns the {@link AssetManager} used to direct all
     * other managers handling loading and tracking of
     * assets.
     * 
     * @return the AssetManager
     */
    public AssetManager getAssetManager() {
        return this.assets;
    }

    // ---------------- abstract method implementation ----------------

    @Override
    public void create() {
        this.input = new InputRetriever();
        this.renderer = new MultiRenderer();

        this.assets = new AssetManager(this);
        this.assets.loadAll(this.options.assetDir);
    }

    @Override
    public void render() {
        this.assets.getAudioManager().update(); // update audio
        this.renderer.clear();
        this.input.update(); // Prepare input for current frame.

        this.renderer.flush(); // Flush graphics for next frame.
    }

    // ---------------- methods ----------------

    void exit() {
        System.exit(0); // TODO proper exit
    }
}
