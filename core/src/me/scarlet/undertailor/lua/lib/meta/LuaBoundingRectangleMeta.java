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
import me.scarlet.undertailor.collision.bbshapes.BoundingRectangle;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaBoundingRectangleMeta extends LuaBoundingBoxMeta {
    
    public static LuaObjectValue<BoundingRectangle> create(BoundingRectangle rectangle) {
        return LuaObjectValue.of(rectangle, Lua.TYPENAME_BOUNDINGBOX_RECTANGLE, Lua.META_BOUNDINGBOX_RECTANGLE);
    }
    
    public static LuaObjectValue<BoundingRectangle> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_BOUNDINGBOX_RECTANGLE);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = new LibraryFunction[] {
            new getDimensions(),
            new setDimensions(),
            new getVertices()
    };
    
    public LuaBoundingRectangleMeta() {
        super();
        
        this.addComponents(COMPONENTS);
    }
    
    static class getDimensions extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            BoundingRectangle box = check(args.arg(1)).getObject();
            Vector2 dimensions = box.getDimensions();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(dimensions.x),
                    LuaValue.valueOf(dimensions.y)
            });
        }
    }
    
    static class setDimensions extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 3);
            
            BoundingRectangle box = check(args.arg1()).getObject();
            Vector2 dimensions = box.getDimensions();
            float width = (float) args.optdouble(2, dimensions.x);
            float height = (float) args.optdouble(3, dimensions.y);
            box.setDimensions(width, height);
            return LuaValue.NIL;
        }
    }
    
    static class getVertices extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            BoundingRectangle box = check(args.arg(1)).getObject();
            float[] vertices = box.getVertices();
            LuaValue[][] returned = new LuaValue[vertices.length / 2][2];
            for(int i = 0; i < vertices.length; i++) {
                returned[i][0] = LuaValue.valueOf(vertices[i * 2]);
                returned[i][1] = LuaValue.valueOf(vertices[i * 2 + 1]);
            }
            
            return LuaUtil.asVarargs(
                    LuaValue.tableOf(returned[0]),
                    LuaValue.tableOf(returned[1]),
                    LuaValue.tableOf(returned[2]),
                    LuaValue.tableOf(returned[3]));
        }
    }
}
