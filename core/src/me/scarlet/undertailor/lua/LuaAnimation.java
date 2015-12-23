package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.gfx.Animation;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

public class LuaAnimation extends LuaValue {
    
    public static final String TYPENAME = "tailor-animation";
    public static LuaValue METATABLE;
    
    public static LuaAnimation checkAnimation(LuaValue value) {
        if(!value.typename().equals(TYPENAME)) {
            throw new LuaError("bad argument: expected " + TYPENAME + "; got " + value.typename());
        }
        
        return (LuaAnimation) value;
    }
    
    private Animation<?> anim;
    public LuaAnimation(Animation<?> anim) {
        this.anim = anim;
    }
    
    public Animation<?> getAnimation() {
        return anim;
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
