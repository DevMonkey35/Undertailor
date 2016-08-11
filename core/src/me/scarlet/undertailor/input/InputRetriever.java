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

package me.scarlet.undertailor.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.ObjectMap;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.engine.events.Event;

/**
 * Handles input tracking operations. Intended to be set as
 * the input processor of the system, done by feeding an
 * instance to the method
 * <code>Gdx.input.setInputProcessor(InputProcessor)</code>
 * ({@link Input#setInputProcessor(InputProcessor)}.
 * 
 * <p>Input tracking is a frame-by-frame operation,
 * therefore for currently pressed keys to update for a
 * given instance the {@link InputRetriever#update()} method
 * should be called.</p>
 * 
 * <p>Setup for usage of the class is simply by feeding the
 * instance to libGDX as the new input processor, then
 * updating it every frame.</p>
 * 
 * <pre>
 * public void create() {
 *     input = new InputRetriever();
 *     Gdx.input.setInputProcessor(input);
 * }
 * 
 * // ...
 * 
 * public void render() {
 *     input.update();
 *     // ...
 * }
 * </pre>
 * 
 * <p>Usage of the class involves querying the current
 * instance of {@link InputData} provided by the
 * {@link InputRetriever#getCurrentData()} method about
 * which keys are currently pressed, providing key codes
 * denoted by the {@link Keys} class.</p>
 * 
 * <pre>
 * InputRetriever input;
 * // ...
 * 
 * input.getPressData(Keys.A).isPressed();
 * </pre>
 * 
 * @see InputData
 * @see PressData
 * @see Keys
 */
public class InputRetriever implements InputProcessor {

    // ---------------- object ----------------

    private long tick;
    private Undertailor tailor;
    private InputData currentData;
    private ObjectMap<Integer, PressData> pressData;

    public InputRetriever(Undertailor tailor) {
        this.tick = 0;
        this.tailor = tailor;
        this.pressData = new ObjectMap<>();
        this.currentData = new InputData(pressData);
    }

    // ---------------- g/s ----------------

    /**
     * Returns the current {@link InputData} tracked by this
     * {@link InputRetriever}.
     * 
     * @return some InputData
     */
    public InputData getCurrentData() {
        return currentData;
    }

    // ---------------- functional methods ----------------

    /**
     * Updates the state of this {@link InputRetriever} and
     * renews the underlying {@link InputData}.
     */
    public void update() {
        tick++;
        currentData.isConsumed = false;
        currentData.currentTick = tick;
    }

    // ---------------- abstract method implementation ----------------

    /**
     * {@inheritDoc}
     * 
     * <p>Internal method, called by the base libGDX
     * framework. Should not be called by the game
     * engine.</p>
     */
    @Override
    public boolean keyDown(int keycode) {
        if (!pressData.containsKey(keycode)) {
            pressData.put(keycode, new PressData(currentData));
        }

        this.tailor.getEnvironmentManager().callEvent(new Event(Event.EVT_KEYDOWN, keycode));
        pressData.get(keycode).down();
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Internal method, called by the base libGDX
     * framework. Should not be called by the game
     * engine.</p>
     */
    @Override
    public boolean keyUp(int keycode) {
        if (!pressData.containsKey(keycode)) {
            pressData.put(keycode, new PressData(currentData));
        }

        this.tailor.getEnvironmentManager().callEvent(new Event(Event.EVT_KEYUP, keycode));
        pressData.get(keycode).up();
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
