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

import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.collision.bbshapes.BoundingBox;
import me.scarlet.undertailor.collision.bbshapes.BoundingCircle;
import me.scarlet.undertailor.collision.bbshapes.BoundingRectangle;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaBoundingBoxMeta extends LuaLibrary {
    
    public static LuaObjectValue<? extends BoundingBox> create(BoundingBox box) {
        if(box instanceof BoundingRectangle) {
            return LuaBoundingRectangleMeta.create((BoundingRectangle) box);
        } else {
            return LuaBoundingCircleMeta.create((BoundingCircle) box);
        }
    }
    
    public static LuaObjectValue<? extends BoundingBox> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_BOUNDINGBOX_RECTANGLE, Lua.TYPENAME_BOUNDINGBOX_CIRCLE);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = new LibraryFunction[] {
            new getOffset(),
            new setOffset(),
            new isSensor(),
            new setSensor()
    };
    
    public LuaBoundingBoxMeta() {
        super(null, COMPONENTS);
    }
    
    static class getOffset extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            BoundingBox box = check(args.arg(1)).getObject();
            Vector2 offset = box.getOffset();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(offset.x),
                    LuaValue.valueOf(offset.y)
            });
        }
    }
    
    static class setOffset extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 3);
            
            BoundingBox box = check(args.arg1()).getObject();
            Vector2 offset = box.getOffset();
            float x = new Float(args.optdouble(2, offset.x));
            float y = new Float(args.optdouble(3, offset.y));
            box.setOffset(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class isSensor extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);;
            
            BoundingBox box = check(args.arg1()).getObject();
            return LuaValue.valueOf(box.isSensor());
        }
    }
    
    static class setSensor extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            BoundingBox box = check(args.arg1()).getObject();
            boolean flag = args.checkboolean(2);
            box.setSensor(flag);
            
            return LuaValue.NIL;
        }
    }
}
