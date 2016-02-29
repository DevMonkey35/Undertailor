package me.scarlet.undertailor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import me.scarlet.undertailor.texts.Font;
import me.scarlet.undertailor.util.InputRetriever.InputData;

import java.util.HashMap;
import java.util.Map;

public class SystemHandler {
    
    public static enum SystemKeybind {
        FULLSCREEN(Keys.F11, () -> {
            Undertailor.getEnvironmentManager().setFullscreen(!Undertailor.getEnvironmentManager().isFullscreen());
        }),
        CONSOLE(Keys.F3, () -> {
            Undertailor.instance.getConsole().show();
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
