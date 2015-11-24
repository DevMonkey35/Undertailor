package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.texts.TextComponent;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

public class LuaTextComponent extends LuaValue {
    
    public static final String TYPENAME = "tailor-textcomponent";
    public static LuaValue METATABLE;
    
    public static LuaTextComponent checkTextComponent(LuaValue value) {
        return checkTextComponent(value, false);
    }
    
    public static LuaTextComponent checkTextComponent(LuaValue value, boolean strict) {
        if(strict) {
            if(!value.typename().equals(LuaTextComponent.TYPENAME)) {
                throw new LuaError("bad argument: strictly expected " + TYPENAME + "; got " + value.typename());
            }
        } else {
            if(!value.typename().equals(LuaTextComponent.TYPENAME) && !(value instanceof LuaTextComponent)) {
                throw new LuaError("bad argument: expected " + TYPENAME + "; got " + value.typename());
            }
        }
        
        return (LuaTextComponent) value;
    }
    
    private TextComponent component;
    public LuaTextComponent(TextComponent component) {
        this.component = component;
    }
    
    public TextComponent getTextComponent() {
        return this.component;
    }
    
    @Override
    public int type() {
        return LuaValue.TVALUE;
    }

    @Override
    public String typename() {
        return LuaTextComponent.TYPENAME;
    }
    
    @Override
    public LuaValue getmetatable() {
        return LuaTextComponent.METATABLE;
    }
}
