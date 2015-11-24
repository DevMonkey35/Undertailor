package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.texts.TextComponent.Text;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

public class LuaText extends LuaTextComponent {
    
    public static final String TYPENAME = "tailor-text";
    public static LuaValue METATABLE;
    
    public static LuaText checkText(LuaValue value) {
        if(!value.typename().equals(TYPENAME)) {
            throw new LuaError("bad argument: expected " + TYPENAME + "; got " + value.typename());
        }
        
        return (LuaText) value;
    }
    
    public LuaText(Text text) {
        super(text);
    }
    
    public Text getText() {
        return (Text) this.getTextComponent();
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
