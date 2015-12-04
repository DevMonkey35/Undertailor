package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.util.InputRetriever.InputData;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

public class LuaInputData extends LuaValue {
    
    public static final String TYPENAME = "tailor-inputdata";
    public static LuaValue METATABLE;
    
    public LuaInputData checkInputData(LuaValue value) {
        if(!value.typename().equals(TYPENAME)) {
            throw new LuaError("bad argument: expected " + TYPENAME + ", got " + value.typename());
        }
        
        return (LuaInputData) value;
    }
    
    private InputData data;
    public LuaInputData(InputData data) {
        this.data = data;
    }
    
    public InputData getData() {
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
