package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.texts.TextComponent;
import org.luaj.vm2.LuaValue;

public class LuaTextComponent extends LuaValue {
    
    public static final String TYPENAME = "tailor-textcomponent";
    public static LuaValue METATABLE;
    
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
