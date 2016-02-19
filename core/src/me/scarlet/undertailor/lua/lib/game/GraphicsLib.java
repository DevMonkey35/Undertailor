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

package me.scarlet.undertailor.lua.lib.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.lib.ColorsLib;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.util.NumberUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class GraphicsLib extends LuaLibrary {
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new getClearColor(),
            new setClearColor(),
            new getSpriteColor(),
            new setSpriteColor(),
            new getShapeColor(),
            new setShapeColor(),
            new drawLine(),
            new drawRectangle(),
            new drawFilledRectangle(),
            new drawCircle(),
            new drawFilledCircle(),
            new drawTriangle(),
            new drawFilledTriangle()
    };
    
    public GraphicsLib() {
        super("graphics", COMPONENTS);
    }
    
    static class getClearColor extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            return ColorsLib.create(Undertailor.getRenderer().getClearColor());
        }
    }
    
    static class setClearColor extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Color color = ColorsLib.check(args.arg(1)).getObject();
            Undertailor.getRenderer().setClearColor(color);
            return LuaValue.NIL;
        }
    }
    
    static class getSpriteColor extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            return ColorsLib.create(Undertailor.getRenderer().getBatchColor());
        }
    }
    
    static class setSpriteColor extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            Color color = ColorsLib.check(args.arg(1)).getObject();
            float alpha = (float) args.optdouble(2, color.a);
            Undertailor.getRenderer().setBatchColor(color, alpha);
            return LuaValue.NIL;
        }
    }
    
    static class getShapeColor extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            return ColorsLib.create(Undertailor.getRenderer().getShapeColor());
        }
    }
    
    static class setShapeColor extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            Color color = ColorsLib.check(args.arg(1)).getObject();
            float alpha = (float) args.optdouble(2, color.a);
            Undertailor.getRenderer().setShapeColor(color, alpha);
            return LuaValue.NIL;
        }
    }
    
    static class drawArc extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 5, 6);
            
            Vector2 pos = new Vector2((float) args.checkdouble(1), (float) args.checkdouble(2));
            float radius = (float) args.checkdouble(3);
            float start = (float) args.checkdouble(4);
            float degrees = (float) args.checkdouble(5);
            int segments = args.optint(6, -1);
            
            if(segments <= -1) {
                Undertailor.getRenderer().drawArc(pos, radius, start, degrees);
            } else {
                Undertailor.getRenderer().drawArc(pos, radius, start, degrees, segments);
            }
            
            return LuaValue.NIL;
        }
    }
    
    static class drawLine extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 4, 5);
            
            Vector2 a = new Vector2((float) args.checkdouble(1),
                    (float) args.checkdouble(2));
            Vector2 b = new Vector2((float) args.checkdouble(3),
                    (float) args.checkdouble(4));
            float thickness = args.isnil(5) ? 1.0F : (float) args.checkdouble(5);
            
            Undertailor.getRenderer().drawLine(a, b, thickness);
            return LuaValue.NIL;
        }
    }
    
    static class drawRectangle extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 4, 5);
            
            Vector2 pos = new Vector2((float) args.checkdouble(1),
                    (float) args.checkdouble(2));
            float width = (float) args.checkdouble(3);
            float height = (float) args.checkdouble(4);
            float thickness = args.isnil(5) ? 1.0F : (float) args.checkdouble(5);
            
            Undertailor.getRenderer().drawRectangle(pos, width, height, thickness);
            return LuaValue.NIL;
        }
    }
    
    static class drawFilledRectangle extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 4, 4);
            
            Vector2 pos = new Vector2((float) args.checkdouble(1),
                    (float) args.checkdouble(2));
            float width = (float) args.checkdouble(3);
            float height = (float) args.checkdouble(4);
            
            Undertailor.getRenderer().drawFilledRectangle(pos, width, height);
            return LuaValue.NIL;
        }
    }
    
    static class drawCircle extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 3, 3);
            
            float x = (float) args.checkdouble(1);
            float y = (float) args.checkdouble(2);
            float radius = (float) args.checkdouble(3);
            
            Undertailor.getRenderer().drawCircle(x, y, radius);
            return LuaValue.NIL;
        }
    }
    
    static class drawFilledCircle extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 3, 3);
            
            float x = (float) args.checkdouble(1);
            float y = (float) args.checkdouble(2);
            float radius = (float) args.checkdouble(3);
            
            Undertailor.getRenderer().drawFilledCircle(x, y, radius);
            return LuaValue.NIL;
        }
    }
    
    static class drawTriangle extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 6, 7);
            
            Vector2 vx1 = new Vector2(0, 0);
            Vector2 vx2 = new Vector2(0, 0);
            Vector2 vx3 = new Vector2(0, 0);
            vx1.x = (float) args.checkdouble(1);
            vx1.y = (float) args.checkdouble(2);
            vx2.x = (float) args.checkdouble(3);
            vx2.y = (float) args.checkdouble(4);
            vx3.x = (float) args.checkdouble(5);
            vx3.y = (float) args.checkdouble(6);
            float lineThickness = NumberUtil.boundFloat((float) args.optdouble(7, 1F), 0.0F);
            
            Undertailor.getRenderer().drawTriangle(vx1, vx2, vx3, lineThickness);
            return LuaValue.NIL;
        }
    }
    
    static class drawFilledTriangle extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 6, 6);
            
            Vector2 vx1 = new Vector2(0, 0);
            Vector2 vx2 = new Vector2(0, 0);
            Vector2 vx3 = new Vector2(0, 0);
            vx1.x = (float) args.checkdouble(1);
            vx1.y = (float) args.checkdouble(2);
            vx2.x = (float) args.checkdouble(3);
            vx2.y = (float) args.checkdouble(4);
            vx3.x = (float) args.checkdouble(5);
            vx3.y = (float) args.checkdouble(6);
            
            Undertailor.getRenderer().drawFilledTriangle(vx1, vx2, vx3);
            return LuaValue.NIL;
        }
    }
}
