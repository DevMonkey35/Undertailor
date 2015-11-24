package me.scarlet.undertailor.lua.lib.meta;

import me.scarlet.undertailor.lua.LuaDisplayMeta;
import me.scarlet.undertailor.lua.LuaStyle;
import me.scarlet.undertailor.texts.Style;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class LuaStyleMeta extends LuaTable {
    
    static {
        if(LuaStyle.METATABLE == null) {
            LuaStyle.METATABLE = LuaValue.tableOf(new LuaValue[] {INDEX, new LuaStyleMeta()});
        }
    }
    
    public LuaStyleMeta() {
        this.set("applyCharacter", new _applyCharacter());
        this.set("onNextTextRender", new _onNextTextRender());
    }
    
    static class _applyCharacter extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
            Style style = LuaStyle.checkStyle(arg1);
            int charIndex = arg2.checkint();
            int textLength = arg3.checkint();
            
            return new LuaDisplayMeta(style.applyCharacter(charIndex, textLength));
        }
    }
    
    static class _onNextTextRender extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            Style style = LuaStyle.checkStyle(arg1);
            float delta = new Float(arg2.checkdouble());
            
            style.onNextTextRender(delta);
            return LuaValue.NIL;
        }
    }
}
