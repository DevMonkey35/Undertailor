package me.scarlet.undertailor.ui;

import com.badlogic.gdx.utils.TimeUtils;
import me.scarlet.undertailor.Undertailor;

public class UITestComponent extends UIComponent {
    
    private static int id = 0;
    private long time = -1;
    
    private int objId;
    {
        objId = id++;
        System.out.println("new uitest registered with id " + objId);
    }
    
    @Override
    public void onDestroy(boolean object) {
        Undertailor.instance.log("TESTCOMP" + objId, "destroyed " + (object ? "object" : "component"));
    }
    
    @Override
    public void process(float delta) {
        if(time == -1) {
            time = TimeUtils.millis();
        } else if(TimeUtils.timeSinceMillis(time) > 1000) {
            this.destroy();
            Undertailor.instance.log("TESTCOMP" + objId, "GOT DESTROYED");
        } else {
            Undertailor.instance.log("TESTCOMP" + objId, "BAD TIME SIMULATOR 2015");
        }
    }

    @Override
    public String getComponentTypeName() {
        return "tailor-testuicomp";
    }
}
