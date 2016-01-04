package me.scarlet.undertailor.lua.lib.meta;

import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.ui.UIComponent;
import me.scarlet.undertailor.ui.UIObject;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaUIObjectMeta extends LuaLibrary {
    
    public static LuaObjectValue<UIObject> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_UIOBJECT);
    }
    
    public static LuaObjectValue<UIObject> create(UIObject value) {
        return LuaObjectValue.of(value, Lua.TYPENAME_UIOBJECT, Lua.META_UIOBJECT);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = new LibraryFunction[] {
            new getId(),
            new isHeadless(),
            new getLifetime(),
            new isVisible(),
            new setVisible(),
            new getPosition(),
            new setPosition(),
            new registerComponent(),
            new destroy()
    };
    
    public LuaUIObjectMeta() {
        super(null, COMPONENTS);
    }
    
    static class getId extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIObject obj = check(args.arg1()).getObject();
            return LuaValue.valueOf(obj.getId());
        }
    }
    
    static class isHeadless extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIObject obj = check(args.arg1()).getObject();
            return LuaValue.valueOf(obj.isHeadless());
        }
    }
    
    static class getLifetime extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIObject obj = check(args.arg1()).getObject();
            return LuaValue.valueOf(obj.getLifetime());
        }
    }
    
    static class isVisible extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIObject obj = check(args.arg1()).getObject();
            return LuaValue.valueOf(obj.isVisible());
        }
    }
    
    static class setVisible extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            UIObject obj = check(args.arg1()).getObject();
            boolean flag = args.checkboolean(2);
            obj.setVisible(flag);
            return LuaValue.NIL;
        }
    }
    
    static class getPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIObject obj = check(args.arg(1)).getObject();
            Vector2 pos = obj.getPosition();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(pos.x),
                    LuaValue.valueOf(pos.y)});
        }
    }
    
    static class setPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 3);
            
            UIObject obj = check(args.arg(1)).getObject();
            float x = new Float(args.arg(2).checkdouble());
            float y = new Float(args.arg(3).checkdouble());
            
            Vector2 pos = obj.getPosition();
            pos.set(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class destroy extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIObject obj = check(args.arg1()).getObject();
            obj.destroy();
            return LuaValue.NIL;
        }
    }
    
    static class registerComponent extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            UIObject obj = check(args.arg1()).getObject();
            UIComponent component = LuaUIComponentMeta.check(args.arg(2)).getObject();
            
            obj.registerChild(component);
            return LuaValue.NIL;
        }
    }
}
