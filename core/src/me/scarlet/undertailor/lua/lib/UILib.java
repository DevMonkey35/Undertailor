package me.scarlet.undertailor.lua.lib;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaUIObject;
import me.scarlet.undertailor.ui.UIObject;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class UILib extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable ui = new LuaTable();
        ui.set("newComponent", new _newComponent());
        ui.set("newObject", new _newObject());
        ui.set("registerObject", new _registerObject());
        ui.set("getObject", new _getObject());
        ui.set("destroyObject", new _destroyObject());
        
        env.set("ui", ui);
        return ui;
    }
    
    static class _newComponent extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, -1);
            try {
                return Undertailor.getUIController().getLuaLoader().newLuaComponent(args.arg(1).checkjstring(), args.subargs(2));
            } catch(LuaError e) {
                throw new LuaError("\n" + e.getMessage());
            }
        }
    }
    
    static class _newObject extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            long lifetime = arg1.isnil() ? 0 : arg1.checklong();
            boolean headless = arg2.isnil() ? false : arg2.checkboolean();
            return new LuaUIObject(new UIObject(lifetime, headless));
        }
    }
    
    static class _registerObject extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            UIObject obj = LuaUIObject.checkUIObject(arg).getUIObject();
            return LuaValue.valueOf(Undertailor.getUIController().registerObject(obj));
        }
    }
    
    static class _getObject extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            int id = arg.checkint();
            UIObject obj = Undertailor.getUIController().getUIObject(id);
            if(obj == null) {
                return LuaValue.NIL;
            } else {
                return new LuaUIObject(obj);
            }
        }
    }
    
    static class _destroyObject extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            int id = arg.checkint();
            return LuaValue.valueOf(Undertailor.getUIController().destroyObject(id));
        }
    }
}
