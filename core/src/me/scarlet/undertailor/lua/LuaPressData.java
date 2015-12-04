package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.lua.lib.meta.LuaPressDataMeta;
import me.scarlet.undertailor.util.InputRetriever.PressData;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

public class LuaPressData extends LuaValue {
    
    public static final String TYPENAME = "tailor-pressdata";
    public static LuaValue METATABLE;
    
    static {
        LuaPressDataMeta.prepareMetatable();
    }
    
    public static LuaPressData checkPressData(LuaValue value) {
        if(!value.typename().equals(TYPENAME)) {
            throw new LuaError("bad argument: expected " + TYPENAME + ", got " + value.typename());
        }
        
        return (LuaPressData) value;
    }
    
    private PressData data;
    public LuaPressData(PressData data) {
        this.data = data;
    }
    
    public PressData getData() {
        return data;
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
