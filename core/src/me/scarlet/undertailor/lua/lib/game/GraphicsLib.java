package me.scarlet.undertailor.lua.lib.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaColor;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class GraphicsLib extends TwoArgFunction {
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable graphics = new LuaTable();
        graphics.set("getShapeColor", new _getShapeColor());
        graphics.set("setShapeColor", new _setShapeColor());
        graphics.set("drawLine", new _drawLine());
        graphics.set("drawRectangle", new _drawRectangle());
        graphics.set("drawFilledRectangle", new _drawFilledRectangle());
        graphics.set("drawCircle", new _drawCircle());
        graphics.set("drawFilledCircle", new _drawFilledCircle());
        
        env.set("graphics", graphics);
        return graphics;
    }
    
    static class _getShapeColor extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return new LuaColor(Undertailor.getRenderer().getShapeColor());
        }
    }
    
    static class _setShapeColor extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            Color color = LuaColor.checkcolor(arg).getColor();
            Undertailor.getRenderer().setShapeColor(color);
            return LuaValue.NIL;
        }
    }
    
    static class _drawLine extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
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
    
    static class _drawRectangle extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
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
    
    static class _drawFilledRectangle extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 4, 4);
            
            Vector2 pos = new Vector2(new Float(args.checkdouble(1)),
                    new Float(args.checkdouble(2)));
            float width = new Float(args.checkdouble(3));
            float height = new Float(args.checkdouble(4));
            
            Undertailor.getRenderer().drawFilledRectangle(pos, width, height);
            return LuaValue.NIL;
        }
    }
    
    static class _drawCircle extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 3, 3);
            
            float x = new Float(args.checkdouble(1));
            float y = new Float(args.checkdouble(2));
            float radius = new Float(args.checkdouble(3));
            
            Undertailor.getRenderer().drawCircle(x, y, radius);
            return LuaValue.NIL;
        }
    }
    
    static class _drawFilledCircle extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 3, 3);
            
            float x = new Float(args.checkdouble(1));
            float y = new Float(args.checkdouble(2));
            float radius = new Float(args.checkdouble(3));
            
            Undertailor.getRenderer().drawFilledCircle(x, y, radius);
            return LuaValue.NIL;
        }
    }
}
