package me.scarlet.undertailor.lua.lib.meta;

import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaInputDataMeta extends LuaLibrary {
    
    public static LuaObjectValue<InputData> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_INPUTDATA);
    }
    
    public static LuaObjectValue<InputData> create(InputData value) {
        return LuaObjectValue.of(value, Lua.TYPENAME_INPUTDATA, Lua.META_INPUTDATA);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new getPressData(),
            new isConsumed(),
            new consume()
    };
    
    public LuaInputDataMeta() {
        super(null, COMPONENTS);
    }
    
    static class getPressData extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            InputData data = check(args.arg1()).getObject();
            return LuaPressDataMeta.create(data.getPressData(args.checkint(2)));
        }
    }
    
    static class isConsumed extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            InputData data = check(args.arg1()).getObject();
            return LuaValue.valueOf(data.isConsumed());
        }
    }
    
    static class consume extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            InputData data = check(args.arg1()).getObject();
            data.consume();
            return LuaValue.NIL;
        }
    }
}
