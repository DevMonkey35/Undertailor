package me.scarlet.undertailor.lua.lib.meta;

import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.InputRetriever.PressData;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaPressDataMeta extends LuaLibrary {
    
    public static LuaObjectValue<PressData> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_PRESSDATA);
    }
    
    public static LuaObjectValue<PressData> create(PressData data) {
        return LuaObjectValue.of(data, Lua.TYPENAME_PRESSDATA, Lua.META_PRESSDATA);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new justPressed(),
            new justReleased(),
            new isPressed(),
            new getHoldTime(),
            new getLastPressTime(),
            new getLastReleaseTime()
    };
    
    public LuaPressDataMeta() {
        super(null, COMPONENTS);
    }
    
    static class justPressed extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            PressData data = check(args.arg1()).getObject();
            float time = new Float(args.optdouble(2, 0.15));
            return LuaValue.valueOf(data.justPressed(time));
        }
    }
    
    static class justReleased extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            PressData data = check(args.arg1()).getObject();
            float time = new Float(args.optdouble(2, 0.15));
            return LuaValue.valueOf(data.justReleased(time));
        }
    }
    
    static class isPressed extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            PressData data = check(args.arg1()).getObject();
            return LuaValue.valueOf(data.isPressed());
        }
    }
    
    static class getHoldTime extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            PressData data = check(args.arg1()).getObject();
            return LuaValue.valueOf(data.getHoldTime());
        }
    }
    
    static class getLastPressTime extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            PressData data = check(args.arg1()).getObject();
            return LuaValue.valueOf(data.getLastPressTime());
        }
    }
    
    static class getLastReleaseTime extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            PressData data = check(args.arg1()).getObject();
            return LuaValue.valueOf(data.getLastReleaseTime());
        }
    }
}
