package me.scarlet.undertailor.lua.lib.meta;

import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.lua.LuaUIComponent;
import me.scarlet.undertailor.lua.LuaUIObject;
import me.scarlet.undertailor.ui.UIComponent;
import me.scarlet.undertailor.ui.UIObject;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaUIObjectMeta extends LuaTable {
    
    public static void prepareMetatable() {
        if(LuaUIObject.METATABLE == null) {
            LuaUIObject.METATABLE = LuaValue.tableOf(new LuaValue[] {INDEX, new LuaUIObjectMeta()});
        }
    }
    
    public LuaUIObjectMeta() {
        this.set("getId", new _getId());
        this.set("isHeadless", new _isHeadless());
        this.set("getLifetime", new _getLifetime());
        this.set("isVisible", new _isVisible());
        this.set("setVisible", new _setVisible());
        this.set("getPosition", new _getPosition());
        this.set("setPosition", new _setPosition());
        this.set("destroy", new _destroy());
        this.set("registerComponent", new _registerComponent());
    }
    
    static class _getId extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            UIObject obj = LuaUIObject.checkUIObject(arg).getUIObject();
            return LuaValue.valueOf(obj.getId());
        }
    }
    
    static class _isHeadless extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            UIObject obj = LuaUIObject.checkUIObject(arg).getUIObject();
            return LuaValue.valueOf(obj.isHeadless());
        }
    }
    
    static class _getLifetime extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            UIObject obj = LuaUIObject.checkUIObject(arg).getUIObject();
            return LuaValue.valueOf(obj.getLifetime());
        }
    }
    
    static class _isVisible extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            UIObject obj = LuaUIObject.checkUIObject(arg).getUIObject();
            return LuaValue.valueOf(obj.isVisible());
        }
    }
    
    static class _setVisible extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            UIObject obj = LuaUIObject.checkUIObject(arg1).getUIObject();
            boolean flag = arg2.checkboolean();
            obj.setVisible(flag);
            return LuaValue.NIL;
        }
    }
    
    static class _getPosition extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIObject obj = LuaUIObject.checkUIObject(args.arg(1)).getUIObject();
            Vector2 pos = obj.getPosition();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(pos.x),
                    LuaValue.valueOf(pos.y)});
        }
    }
    
    static class _setPosition extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 3);
            
            UIObject obj = LuaUIObject.checkUIObject(args.arg(1)).getUIObject();
            float x = new Float(args.arg(2).checkdouble());
            float y = new Float(args.arg(3).checkdouble());
            
            Vector2 pos = obj.getPosition();
            pos.set(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class _destroy extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            UIObject obj = LuaUIObject.checkUIObject(arg).getUIObject();
            obj.destroy();
            return LuaValue.NIL;
        }
    }
    
    static class _registerComponent extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            UIObject obj = LuaUIObject.checkUIObject(arg1).getUIObject();
            UIComponent component = LuaUIComponent.checkUIComponent(arg2).getComponent();
            
            obj.registerChild(component);
            return LuaValue.NIL;
        }
    }
}
