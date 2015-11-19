package me.scarlet.undertailor.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import me.scarlet.undertailor.ui.event.UIEvent;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class LuaUIComponent extends UIComponent {
    
    public static LuaTable generateLuaFrame(boolean isAlwaysActive) {
        LuaTable table = new LuaTable();
        table.set("j_objectid", "uicomponent");
        return table;
    }
    
    public static LuaTable prepareLuaTable(LuaTable lua) {
        return null;
    }
    
    private LuaTable component;
    public LuaUIComponent() {
        component = new LuaTable();
    }
    
    @Override
    public void onEvent(UIEvent event) {
    }
    
    @Override
    public void process(float delta) {
    }
    
    @Override
    public void render(Batch batch, float parentAlpha) {
        
    }
    
    public static class _destroy extends ZeroArgFunction {
        
        private UIComponent component;
        public _destroy(UIComponent component) {
            this.component = component;
        }
        
        @Override
        public LuaValue call() {
            component.destroy();
            return LuaValue.NIL;
        }
    }
}
