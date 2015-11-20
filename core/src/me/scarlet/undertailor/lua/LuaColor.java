package me.scarlet.undertailor.lua;

import com.badlogic.gdx.graphics.Color;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

public class LuaColor extends LuaValue {

    public static final String TYPENAME = "gdx-color";
    public static LuaValue METATABLE;
    
    public static LuaColor checkcolor(LuaValue value) {
        if(!value.typename().equals(LuaColor.TYPENAME)) throw new LuaError("bad argument: expected a " + LuaColor.TYPENAME + "; got " + value.typename());
        return (LuaColor) value;
    }
    
    private Color color;
    
    public LuaColor() {
        this(null);
    }
    
    public LuaColor(Color color) {
        this.color = color == null ? new Color(0F, 0F, 0F, 1F) : color;
    }
    
    public Color getColor() {
        return color;
    }
    
    @Override
    public LuaValue getmetatable() {
        return LuaColor.METATABLE;
    }
    
    @Override
    public int type() {
        return LuaValue.TVALUE;
    }

    @Override
    public String typename() {
        return LuaColor.TYPENAME;
    }
}
