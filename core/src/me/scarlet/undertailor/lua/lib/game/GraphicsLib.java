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
            float alpha = new Float(args.optdouble(2, color.a));
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
            float alpha = new Float(args.optdouble(2, color.a));
            Undertailor.getRenderer().setShapeColor(color, alpha);
            return LuaValue.NIL;
        }
    }
    
    static class drawLine extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 4, 5);
            
            Vector2 a = new Vector2(new Float(args.checkdouble(1)),
                    new Float(args.checkdouble(2)));
            Vector2 b = new Vector2(new Float(args.checkdouble(3)),
                    new Float(args.checkdouble(4)));
            float thickness = args.isnil(5) ? 1.0F : new Float(args.checkdouble(5));
            
            Undertailor.getRenderer().drawLine(a, b, thickness);
            return LuaValue.NIL;
        }
    }
    
    static class drawRectangle extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 4, 5);
            
            Vector2 pos = new Vector2(new Float(args.checkdouble(1)),
                    new Float(args.checkdouble(2)));
            float width = new Float(args.checkdouble(3));
            float height = new Float(args.checkdouble(4));
            float thickness = args.isnil(5) ? 1.0F : new Float(args.checkdouble(5));
            
            Undertailor.getRenderer().drawRectangle(pos, width, height, thickness);
            return LuaValue.NIL;
        }
    }
    
    static class drawFilledRectangle extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 4, 4);
            
            Vector2 pos = new Vector2(new Float(args.checkdouble(1)),
                    new Float(args.checkdouble(2)));
            float width = new Float(args.checkdouble(3));
            float height = new Float(args.checkdouble(4));
            
            Undertailor.getRenderer().drawFilledRectangle(pos, width, height);
            return LuaValue.NIL;
        }
    }
    
    static class drawCircle extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 3, 3);
            
            float x = new Float(args.checkdouble(1));
            float y = new Float(args.checkdouble(2));
            float radius = new Float(args.checkdouble(3));
            
            Undertailor.getRenderer().drawCircle(x, y, radius);
            return LuaValue.NIL;
        }
    }
    
    static class drawFilledCircle extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 3, 3);
            
            float x = new Float(args.checkdouble(1));
            float y = new Float(args.checkdouble(2));
            float radius = new Float(args.checkdouble(3));
            
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
            vx1.x = new Float(args.checkdouble(1));
            vx1.y = new Float(args.checkdouble(2));
            vx2.x = new Float(args.checkdouble(3));
            vx2.y = new Float(args.checkdouble(4));
            vx3.x = new Float(args.checkdouble(5));
            vx3.y = new Float(args.checkdouble(6));
            float lineThickness = NumberUtil.boundFloat(new Float(args.optdouble(7, 1F)), 0.0F);
            
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
            vx1.x = new Float(args.checkdouble(1));
            vx1.y = new Float(args.checkdouble(2));
            vx2.x = new Float(args.checkdouble(3));
            vx2.y = new Float(args.checkdouble(4));
            vx3.x = new Float(args.checkdouble(5));
            vx3.y = new Float(args.checkdouble(6));
            
            Undertailor.getRenderer().drawFilledTriangle(vx1, vx2, vx3);
            return LuaValue.NIL;
        }
    }
}
