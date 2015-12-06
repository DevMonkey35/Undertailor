package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.collision.BoundingRectangle;
import me.scarlet.undertailor.lua.lib.meta.LuaBoundingBoxMeta;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

public class LuaBoundingBox extends LuaValue {
    
    public static final String TYPENAME = "tailor-boundingbox";
    public static LuaValue METATABLE;
    
    static {
        LuaBoundingBoxMeta.prepareMetatable();
    }
    
    public static LuaBoundingBox checkBoundingBox(LuaValue value) {
        if(!value.typename().equals(TYPENAME)) {
            throw new LuaError("bad argument: expected " + TYPENAME + ", got " + value.typename());
        }
        
        return (LuaBoundingBox) value;
    }
    
    private BoundingRectangle boundingBox;
    public LuaBoundingBox(BoundingRectangle boundingBox) {
        this.boundingBox = boundingBox;
    }
    
    public BoundingRectangle getBoundingBox() {
        return boundingBox;
    }
    
    @Override
    public String typename() {
        return TYPENAME;
    }
    
    @Override
    public int type() {
        return LuaValue.TVALUE;
    }
    
    @Override
    public LuaValue getmetatable() {
        return METATABLE;
    }
}
