/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor.lua.lib;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class ColorsLib extends LuaLibrary {
    
    public static LuaObjectValue<Color> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_COLOR);
    }
    
    public static LuaObjectValue<Color> create(Color color) {
        return LuaObjectValue.of(color, Lua.TYPENAME_COLOR, LuaLibrary.asMetatable(Lua.LIB_COLORS));
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new fromHex(),
            new fromRGB(),
            new getRGB(),
            new setRGB(),
            new printColor()
    };
    
    public ColorsLib() {
        super("colors", COMPONENTS);
    }
    
    // ##########################
    // #   Library functions.   #
    // ##########################
    
    @Override
    public void postinit(LuaValue env, LuaValue table) {
        for(String key : Colors.getColors().keys()) {
            table.set(key, LuaValue.valueOf(Colors.get(key).toString()));
        }
    }
    
    static class fromHex extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            String string = args.checkjstring(1);
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
            
            return create(color);
        }
    }
    
    static class fromRGB extends LibraryFunction  {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 3, 3);
            
            float r = args.optint(1, 0) / 255F;
            float g = args.optint(2, 0) / 255F;
            float b = args.optint(3, 0) / 255F;
            
            return create(new Color(r, g, b, 1.0F));
        }
    }
    
    static class getRGB extends LibraryFunction  {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Color color = check(args.arg(1)).getObject();
            return LuaUtil.asVarargs(
                    LuaValue.valueOf(color.r),
                    LuaValue.valueOf(color.g),
                    LuaValue.valueOf(color.b),
                    LuaValue.valueOf(color.a));
        }
    }
    
    static class setRGB extends LibraryFunction  {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 5); // color, r, g, b, a
            
            Color color = check(args.arg(1)).getObject();
            float r = args.isnil(2) ? color.r : (float) args.checkdouble(2);
            float g = args.isnil(3) ? color.g : (float) args.checkdouble(3);
            float b = args.isnil(4) ? color.b : (float) args.checkdouble(4);
            float a = args.isnil(5) ? color.a : (float) args.checkdouble(5);
            color.set(r, g, b, a);
            return LuaValue.NIL;
        }
    }
    
    static class printColor extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Color c = check(args.arg1()).getObject();
            Undertailor.instance.log("[colorslib]", c.toString());
            return LuaValue.NIL;
        }
    }
}
