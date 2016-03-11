/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import me.scarlet.undertailor.texts.Font;
import me.scarlet.undertailor.util.InputRetriever.InputData;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the execution of "system tasks," tasks of which are always done by
 * the system typically for purposes of base functionality.
 */
public class SystemHandler {
    
    public static enum SystemKeybind {
        FULLSCREEN(Keys.F11, () -> {
            Undertailor.getEnvironmentManager().setFullscreen(!Undertailor.getEnvironmentManager().isFullscreen());
        }),
        CONSOLE(Keys.F3, () -> {
            Undertailor.instance.getConsole().show();
        }),
        RESHOW_LAUNCHER(Keys.F12, () -> {
            LaunchOptions options = Undertailor.instance.getLaunchOptions();
            if(!options.dev && options.skipLauncher) {
                options.skipLauncher = false;
                options.save();
            }
        }),
        EXIT(Keys.ESCAPE, () -> {
            // TODO exit process
        });
        
        private int defaultKey;
        private Runnable action;
        
        SystemKeybind(int defaultKey, Runnable action) {
            this.defaultKey = defaultKey;
            this.action = action;
        }
        
        public int getDefaultKey() {
            return this.defaultKey;
        }
        
        public Runnable getAction() {
            return this.action;
        }
    }
    
    private Map<SystemKeybind, Integer> keyMapping;
    
    public SystemHandler() {
        this.keyMapping = new HashMap<>();
        for(SystemKeybind bind : SystemKeybind.values()) {
            this.keyMapping.put(bind, bind.getDefaultKey());
        }
    }
    
    public int getKeybind(SystemKeybind bind) {
        return this.keyMapping.get(bind);
    }
    
    public void setKeybind(SystemKeybind bind, int key) {
        this.keyMapping.put(bind, key);
    }
    
    public void process(float delta, InputData input) {
        Font bitop = Undertailor.getFontManager().getFont("8bitop");
        bitop.write(Gdx.graphics.getFramesPerSecond() + "", null, null, 10, 427, 2);
        Undertailor.getRenderer().flush();
        
        for(SystemKeybind bind : this.keyMapping.keySet()) {
            if(input.getPressData(this.getKeybind(bind)).justPressed(0) && bind.getAction() != null) {
                bind.getAction().run();
            }
        }
    }
}
