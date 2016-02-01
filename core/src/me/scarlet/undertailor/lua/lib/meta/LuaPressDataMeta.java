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

package me.scarlet.undertailor.lua.lib.meta;

import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.InputRetriever.PressData;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaPressDataMeta extends LuaLibrary {
    
    public static LuaObjectValue<PressData> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_PRESSDATA);
    }
    
    public static LuaObjectValue<PressData> create(PressData data) {
        return LuaObjectValue.of(data, Lua.TYPENAME_PRESSDATA, Lua.META_PRESSDATA);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new justPressed(),
            new justReleased(),
            new isPressed(),
            new getHoldTime(),
            new getLastPressTime(),
            new getLastReleaseTime()
    };
    
    public LuaPressDataMeta() {
        super(null, COMPONENTS);
    }
    
    static class justPressed extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            PressData data = check(args.arg1()).getObject();
            float time = new Float(args.optdouble(2, 0));
            return LuaValue.valueOf(data.justPressed(time));
        }
    }
    
    static class justReleased extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            PressData data = check(args.arg1()).getObject();
            float time = new Float(args.optdouble(2, 0));
            return LuaValue.valueOf(data.justReleased(time));
        }
    }
    
    static class isPressed extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            PressData data = check(args.arg1()).getObject();
            return LuaValue.valueOf(data.isPressed());
        }
    }
    
    static class getHoldTime extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            PressData data = check(args.arg1()).getObject();
            return LuaValue.valueOf(data.getHoldTime());
        }
    }
    
    static class getLastPressTime extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            PressData data = check(args.arg1()).getObject();
            return LuaValue.valueOf(data.getLastPressTime());
        }
    }
    
    static class getLastReleaseTime extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            PressData data = check(args.arg1()).getObject();
            return LuaValue.valueOf(data.getLastReleaseTime());
        }
    }
}
