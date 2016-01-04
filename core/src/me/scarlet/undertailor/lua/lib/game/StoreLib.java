package me.scarlet.undertailor.lua.lib.game;

import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.HashMap;
import java.util.Map;

public class StoreLib extends LuaLibrary {
    
    private Map<String, LuaTable> tables;
    public StoreLib() {
        super("store",
                new get(),
                new set());
        
        tables = new HashMap<>();
    }
    
    static class get extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            String tableName = args.checkjstring(1);
            LuaValue key = args.checknotnil(2);
            return ((StoreLib) this.getLibraryInstance()).tables.get(tableName).get(key);
        }
    }
    
    static class set extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 3, 3);
            
            String tableName = args.checkjstring(1);
            LuaValue key = args.checknotnil(2);
            LuaValue value = args.arg(3);
            ((StoreLib) this.getLibraryInstance()).tables.get(tableName).set(key, value);
            return LuaValue.NIL;
        }
    }
    
    // TODO save/load from file
}
