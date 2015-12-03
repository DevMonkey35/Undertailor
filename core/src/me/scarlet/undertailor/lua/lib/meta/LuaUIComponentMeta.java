package me.scarlet.undertailor.lua.lib.meta;

import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.lua.LuaUIComponent;
import me.scarlet.undertailor.lua.LuaUIObject;
import me.scarlet.undertailor.ui.UIComponent;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaUIComponentMeta extends LuaTable {
    
    public static void prepareMetatable() {
        if(LuaUIComponent.METATABLE == null) {
            LuaUIComponent.METATABLE = LuaValue.tableOf(new LuaValue[] {INDEX, new LuaUIComponentMeta()});
        }
    }
    
    public LuaUIComponentMeta() {
        this.set("getParent", new _getParent());
        this.set("getRealPosition", new _getRealPosition());
        this.set("getPosition", new _getPosition());
        this.set("setPosition", new _setPosition());
        this.set("setAlwaysActive", new _setAlwaysActive());
        this.set("setRenderWhenInactive", new _setRenderWhenInactive());
        this.set("destroy", new _destroy());
        this.set("destroyParent", new _destroyParent());
        this.set("getComponentTypeName", new _getComponentTypeName());
    }
    
    static class _getParent extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            UIComponent component = LuaUIComponent.checkUIComponent(arg).getComponent();
            
            if(component.getParent() == null) {
                return LuaValue.NIL;
            } else {
                return new LuaUIObject(component.getParent());
            }
        }
    }
    
    static class _getRealPosition extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIComponent component = LuaUIComponent.checkUIComponent(args.arg1()).getComponent();
            Vector2 pos = component.getRealPosition();
            
            return LuaValue.varargsOf(new LuaValue[] {LuaValue.valueOf(pos.x), LuaValue.valueOf(pos.y)});
        }
    }
    
    static class _getPosition extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIComponent component = LuaUIComponent.checkUIComponent(args.arg1()).getComponent();
            Vector2 pos = component.getPosition();
            
            return LuaValue.varargsOf(new LuaValue[] {LuaValue.valueOf(pos.x), LuaValue.valueOf(pos.y)});
        }
    }
    
    static class _setPosition extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
            UIComponent component = LuaUIComponent.checkUIComponent(arg1).getComponent();
            float x = new Float(arg2.checkdouble());
            float y = new Float(arg3.checkdouble());
            
            component.setPosition(new Vector2(x, y));
            return LuaValue.NIL;
        }
    }
    
    static class _setAlwaysActive extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            UIComponent component = LuaUIComponent.checkUIComponent(arg1).getComponent();
            boolean flag = arg2.checkboolean();
            
            component.setAlwaysActive(flag);
            return LuaValue.NIL;
        }
    }
    
    static class _setRenderWhenInactive extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            UIComponent component = LuaUIComponent.checkUIComponent(arg1).getComponent();
            boolean flag = arg2.checkboolean();
            
            component.setRenderWhenInactive(flag);
            return LuaValue.NIL;
        }
    }
    
    static class _destroy extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            UIComponent component = LuaUIComponent.checkUIComponent(arg).getComponent();
            try {
                component.destroy();
            } catch(Exception e) {
                throw new LuaError(e.getMessage());
            }
            
            return LuaValue.NIL;
        }
    }
    
    static class _destroyParent extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            UIComponent component = LuaUIComponent.checkUIComponent(arg).getComponent();
            component.destroyObject();
            return LuaValue.NIL;
        }
    }
    
    static class _getComponentTypeName extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            UIComponent component = LuaUIComponent.checkUIComponent(arg).getComponent();
            return LuaValue.valueOf(component.getComponentTypeName());
        }
    }
}
