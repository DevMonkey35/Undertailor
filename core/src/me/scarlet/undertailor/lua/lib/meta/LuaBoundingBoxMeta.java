package me.scarlet.undertailor.lua.lib.meta;

import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.collision.BoundingRectangle;
import me.scarlet.undertailor.lua.LuaBoundingBox;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaBoundingBoxMeta extends LuaTable {
    
    public static void prepareMetatable() {
        if(LuaBoundingBox.METATABLE == null) {
            LuaBoundingBox.METATABLE = LuaValue.tableOf(new LuaValue[] {INDEX, new LuaBoundingBoxMeta()});
        }
    }
    
    public LuaBoundingBoxMeta() {
        this.set("getPositionOffset", new _getPositionOffset());
        this.set("setPositionOffset", new _setPositionOffset());
        this.set("getDimensions", new _getDimensions());
        this.set("setDimensions", new _setDimensions());
        this.set("getOrigin", new _getOrigin());
        this.set("setOrigin", new _setOrigin());
        this.set("getVertices", new _getVertices());
    }
    
    static class _getPositionOffset extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            BoundingRectangle box = LuaBoundingBox.checkBoundingBox(args.arg(1)).getBoundingBox();
            Vector2 pos = box.getPositionOffset();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(pos.x),
                    LuaValue.valueOf(pos.y)
            });
        }
    }
    
    static class _setPositionOffset extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
            BoundingRectangle box = LuaBoundingBox.checkBoundingBox(arg1).getBoundingBox();
            Vector2 pos = box.getPositionOffset();
            float x = arg2.isnil() ? pos.x : new Float(arg2.checkdouble());
            float y = arg3.isnil() ? pos.y : new Float(arg3.checkdouble());
            box.setPositionOffset(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class _getDimensions extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            BoundingRectangle box = LuaBoundingBox.checkBoundingBox(args.arg(1)).getBoundingBox();
            Vector2 dimensions = box.getDimensions();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(dimensions.x),
                    LuaValue.valueOf(dimensions.y)
            });
        }
    }
    
    static class _setDimensions extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
            BoundingRectangle box = LuaBoundingBox.checkBoundingBox(arg1).getBoundingBox();
            Vector2 dimensions = box.getDimensions();
            float width = arg2.isnil() ? dimensions.x : new Float(arg2.checkdouble());
            float height = arg3.isnil() ? dimensions.y : new Float(arg3.checkdouble());
            box.setDimensions(width, height);
            return LuaValue.NIL;
        }
    }
    
    static class _getOrigin extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            BoundingRectangle box = LuaBoundingBox.checkBoundingBox(args.arg(1)).getBoundingBox();
            Vector2 origin = box.getOrigin();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(origin.x),
                    LuaValue.valueOf(origin.y)
            });
        }
    }
    
    static class _setOrigin extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
            BoundingRectangle box = LuaBoundingBox.checkBoundingBox(arg1).getBoundingBox();
            Vector2 origin = box.getOrigin();
            float x = arg2.isnil() ? origin.x : new Float(arg2.checkdouble());
            float y = arg3.isnil() ? origin.y : new Float(arg3.checkdouble());
            box.setOrigin(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class _getVertices extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            BoundingRectangle box = LuaBoundingBox.checkBoundingBox(args.arg(1)).getBoundingBox();
            float[] vertices = box.getVertices();
            LuaValue[][] returned = new LuaValue[vertices.length / 2][2];
            for(int i = 0; i < vertices.length; i++) {
                returned[i][0] = LuaValue.valueOf(vertices[i * 2]);
                returned[i][1] = LuaValue.valueOf(vertices[i * 2 + 1]);
            }
            
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.tableOf(returned[0]),
                    LuaValue.tableOf(returned[1]),
                    LuaValue.tableOf(returned[2]),
                    LuaValue.tableOf(returned[3])
            });
        }
    }
}
