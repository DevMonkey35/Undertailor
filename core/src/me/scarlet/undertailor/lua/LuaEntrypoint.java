package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.lua.lib.meta.LuaEntrypointMeta;
import me.scarlet.undertailor.overworld.WorldRoom.Entrypoint;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

public class LuaEntrypoint extends LuaValue {
    
    public static final String TYPENAME = "tailor-entrypoint";
    public static LuaValue METATABLE;
    
    static {
        LuaEntrypointMeta.prepareMetatable();
    }
    
    public static LuaEntrypoint checkEntrypoint(LuaValue value) {
        if(!value.typename().equals(TYPENAME)) {
            throw new LuaError("bad argument: expected " + TYPENAME + "; got " + value.typename());
        }
        
        return (LuaEntrypoint) value;
    }
    
    private Entrypoint entrypoint;
    public LuaEntrypoint(Entrypoint entrypoint) {
        this.entrypoint = entrypoint;
    }
    
    public Entrypoint getEntrypoint() {
        return entrypoint;
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
