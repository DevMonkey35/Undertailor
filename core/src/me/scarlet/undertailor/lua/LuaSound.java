package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.wrappers.SoundWrapper;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

public class LuaSound extends LuaValue {
    
    public static final String TYPENAME = "gdx-sound";
    public static LuaValue METATABLE;
    
    public static LuaSound checkSound(LuaValue value) {
        if(!value.typename().equals(TYPENAME)) {
            throw new LuaError("bad argument: expected " + TYPENAME + "; got " + value.typename());
        }
        
        return (LuaSound) value;
    }
    
    private SoundWrapper sound;
    public LuaSound(SoundWrapper sound) {
        this.sound = sound;
    }
    
    public SoundWrapper getSound() {
        return sound;
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
