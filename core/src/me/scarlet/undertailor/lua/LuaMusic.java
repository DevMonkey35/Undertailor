package me.scarlet.undertailor.lua;

import com.badlogic.gdx.audio.Music;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

public class LuaMusic extends LuaValue {
    
    public static final String TYPENAME = "gdx-music";
    public static LuaValue METATABLE;
    
    public static LuaMusic checkMusic(LuaValue value) {
        if(!value.typename().equals(TYPENAME)) {
            throw new LuaError("bad argument: expected " + TYPENAME + "; got " + value.typename());
        }
        
        return (LuaMusic) value;
    }
    
    private Music music;
    public LuaMusic(Music music) {
        this.music = music;
    }
    
    public Music getMusic() {
        return music;
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
