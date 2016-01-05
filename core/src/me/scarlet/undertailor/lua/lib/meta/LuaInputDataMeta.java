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
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaInputDataMeta extends LuaLibrary {
    
    public static LuaObjectValue<InputData> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_INPUTDATA);
    }
    
    public static LuaObjectValue<InputData> create(InputData value) {
        return LuaObjectValue.of(value, Lua.TYPENAME_INPUTDATA, Lua.META_INPUTDATA);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new getPressData(),
            new isConsumed(),
            new consume()
    };
    
    public LuaInputDataMeta() {
        super(null, COMPONENTS);
    }
    
    static class getPressData extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            InputData data = check(args.arg1()).getObject();
            return LuaPressDataMeta.create(data.getPressData(args.checkint(2)));
        }
    }
    
    static class isConsumed extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            InputData data = check(args.arg1()).getObject();
            return LuaValue.valueOf(data.isConsumed());
        }
    }
    
    static class consume extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            InputData data = check(args.arg1()).getObject();
            data.consume();
            return LuaValue.NIL;
        }
    }
}
