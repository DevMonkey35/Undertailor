package me.scarlet.undertailor.lua.lib;

import com.badlogic.gdx.graphics.Color;
import me.scarlet.undertailor.lua.LuaColor;
import me.scarlet.undertailor.lua.LuaDisplayMeta;
import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class TextLib extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable text = new LuaTable();
        text.set("newDisplayMeta", new _newDisplayMeta());
        
        env.set("text", text);
        return text;
    }
    
    static class _newDisplayMeta extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if(args.narg() < 0 || args.narg() > 5) {
                throw new LuaError("function arguments insufficient or overflowing (min 0, max 5)");
            }
            
            float offX = args.arg(1).isnil() ? 0F : new Float(args.arg(1).checkdouble());
            float offY = args.arg(2).isnil() ? 0F : new Float(args.arg(2).checkdouble());
            float scaleX = args.arg(3).isnil() ? 1F : new Float(args.arg(3).checkdouble());
            float scaleY = args.arg(4).isnil() ? 1F : new Float(args.arg(4).checkdouble());
            Color color = args.arg(5).isnil() ? Color.WHITE : LuaColor.checkcolor(args.arg(5)).getColor();
            
            if(scaleX < 0F) {
                scaleX = 0F;
            }
            
            if(scaleY < 0F) {
                scaleY = 0F;
            }
            
            return LuaValue.varargsOf(new LuaValue[] {new LuaDisplayMeta(new DisplayMeta(offX, offY, scaleX, scaleY, color))});
        }
    }
}
