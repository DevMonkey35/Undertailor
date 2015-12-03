package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.lua.lib.meta.LuaUIObjectMeta;
import me.scarlet.undertailor.ui.UIObject;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

public class LuaUIObject extends LuaValue {
    
    public static final String TYPENAME = "tailor-uiobject";
    public static LuaValue METATABLE;
    
    static {
        LuaUIObjectMeta.prepareMetatable();
    }
    
    public static LuaUIObject checkUIObject(LuaValue value) {
        if(!value.typename().equals(TYPENAME)) {
            throw new LuaError("bad argument: expected " + TYPENAME + "; got " + value.typename());
        }
        
        return (LuaUIObject) value;
    }
    
    private UIObject object;
    public LuaUIObject(UIObject object) {
        this.object = object;
    }
    
    public UIObject getUIObject() {
        return object;
    }
    
    @Override
    public int type() {
        return LuaValue.TVALUE;
    }
    
    @Override
    public String typename() {
        return TYPENAME;
    }
    
    @Override
    public LuaValue getmetatable() {
        return METATABLE;
    }
}
