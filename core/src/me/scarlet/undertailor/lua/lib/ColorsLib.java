package me.scarlet.undertailor.lua.lib;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import me.scarlet.undertailor.lua.LuaColor;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class ColorsLib extends TwoArgFunction {
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable colors = new LuaTable();
        LuaTable presets = new LuaTable();
        colors.set("fromHex", new _fromHex());
        colors.set("fromRGB", new _fromRGB());
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
    
    static class _fromRGB extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue r, LuaValue g, LuaValue b) {
            r.checkint();
            g.checkint();
            b.checkint();
            
            return new LuaColor(new Color(r.toint() / 255F, g.toint() / 255F, b.toint() / 255F, 255F));
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
