package me.scarlet.undertailor.lua.lib;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import me.scarlet.undertailor.lua.LuaColor;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class ColorsLib extends TwoArgFunction {
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable colors = new LuaTable();
        LuaTable presets = new LuaTable();
        colors.set("fromHex", new _fromHex());
        colors.set("fromRGB", new _fromRGB());
        colors.set("getRGB", new _getRGB());
        colors.set("setRGB", new _setRGB());
        Colors.getColors().entries().forEach(entry -> {
            presets.set(entry.key.toUpperCase(), new LuaColor(entry.value));
        });
        
        colors.set("presets", presets);
        
        if(LuaColor.METATABLE == null) LuaColor.METATABLE = LuaValue.tableOf(new LuaValue[] {LuaValue.INDEX, colors});
        env.set("colors", colors);
        return colors;
    }
    
    static class _fromHex extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue hexstring) {
            hexstring.checkstring();
            String string = hexstring.toString();
            string = string.charAt(0) == '#' ? string.substring(1) : string;
            if(string.length() < 6) {
                throw new LuaError("bad argument: invalid hex string; must be in the format RRGGBB or RRGGBBAA");
            } else {
                if(string.length() == 6) {
                    string = string + "FF";
                } else {
                    if(string.length() != 8) {
                        throw new LuaError("bad argument: invalid hex string; must be in the format RRGGBB or RRGGBBAA");
                    }
                }
            }
            
            Color color;
            try {
                color = Color.valueOf(string);
            } catch(Exception e) {
                throw new LuaError("error: bad hex value");
            }
            
            return new LuaColor(color);
        }
    }
    
    static class _fromRGB extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 3, 4);
            
            float r = args.isnil(1) ? 0 : new Float(args.checkdouble(1));
            float g = args.isnil(2) ? 0 : new Float(args.checkdouble(2));
            float b = args.isnil(3) ? 0 : new Float(args.checkdouble(3));
            float a = args.isnil(4) ? 0 : new Float(args.checkdouble(4));
            
            return new LuaColor(new Color(r, g, b, a));
        }
    }
    
    static class _getRGB extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Color color = LuaColor.checkcolor(args.arg(1)).getColor();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(color.r),
                    LuaValue.valueOf(color.g),
                    LuaValue.valueOf(color.b),
                    LuaValue.valueOf(color.a)});
        }
    }
    
    static class _setRGB extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 5); // color, r, g, b, a
            
            Color color = LuaColor.checkcolor(args.arg(1)).getColor();
            float r = args.isnil(2) ? color.r : new Float(args.checkdouble(2));
            float g = args.isnil(3) ? color.g : new Float(args.checkdouble(3));
            float b = args.isnil(4) ? color.b : new Float(args.checkdouble(4));
            float a = args.isnil(5) ? color.a : new Float(args.checkdouble(5));
            color.set(r, g, b, a);
            return LuaValue.NIL;
        }
    }
    
    static class _printColor extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue color) {
            LuaColor.checkcolor(color);
            
            Color c = ((LuaColor) color).getColor();
            System.out.println(c.toString());
            return LuaValue.NIL;
        }
    }
}
