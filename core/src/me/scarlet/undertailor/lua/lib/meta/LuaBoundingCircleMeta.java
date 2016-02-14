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

import me.scarlet.undertailor.collision.bbshapes.BoundingCircle;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaBoundingCircleMeta extends LuaBoundingBoxMeta {
    
    public static LuaObjectValue<BoundingCircle> create(BoundingCircle circle) {
        return LuaObjectValue.of(circle, Lua.TYPENAME_BOUNDINGBOX_CIRCLE, Lua.META_BOUNDINGBOX_CIRCLE);
    }
    
    public static LuaObjectValue<BoundingCircle> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_BOUNDINGBOX_CIRCLE);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = new LibraryFunction[] {
            new getRadius(),
            new setRadius()
    };
    
    public LuaBoundingCircleMeta() {
        super();
        
        this.addComponents(COMPONENTS);
    }
    
    static class getRadius extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            BoundingCircle circle = check(args.arg(1)).getObject();
            return LuaValue.valueOf(circle.getRadius());
        }
    }
    
    static class setRadius extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            BoundingCircle circle = check(args.arg(1)).getObject();
            circle.setRadius(new Float(args.checkdouble(2)));
            return LuaValue.NIL;
        }
    }
}
