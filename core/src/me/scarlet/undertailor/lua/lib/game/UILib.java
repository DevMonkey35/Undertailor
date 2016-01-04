package me.scarlet.undertailor.lua.lib.game;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.lib.meta.LuaUIObjectMeta;
import me.scarlet.undertailor.ui.UIObject;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class UILib extends LuaLibrary {
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new newComponent(),
            new newObject(),
            new registerObject(),
            new getObject(),
            new destroyObject()
    };
    
    public UILib() {
        super("ui", COMPONENTS);
    }
    
    static class newComponent extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, -1);
            
            try {
                return Undertailor.getUIController().getLuaLoader().newLuaComponent(args.arg(1).checkjstring(), args.subargs(2));
            } catch(LuaError e) {
                throw new LuaError("\n" + e.getMessage(), 2);
            }
        }
    }
    
    static class newObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 2);
            
            long lifetime = args.optlong(1, 0);
            boolean headless = args.optboolean(2, false);
            return LuaUIObjectMeta.create(new UIObject(lifetime, headless));
        }
    }
    
    static class registerObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIObject obj = LuaUIObjectMeta.check(args.arg(1)).getObject();
            return LuaValue.valueOf(Undertailor.getUIController().registerObject(obj));
        }
    }
    
    static class getObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            int id = args.checkint(1);
            UIObject obj = Undertailor.getUIController().getUIObject(id);
            if(obj == null) {
                return LuaValue.NIL;
            } else {
                return LuaUIObjectMeta.create(obj);
            }
        }
    }
    
    static class destroyObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            int id = args.checkint(1);
            return LuaValue.valueOf(Undertailor.getUIController().destroyObject(id));
        }
    }
}
