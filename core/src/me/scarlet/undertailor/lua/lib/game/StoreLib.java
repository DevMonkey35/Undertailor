package me.scarlet.undertailor.lua.lib.game;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class StoreLib extends TwoArgFunction {
    
    private LuaTable table;
    public StoreLib() {
        table = new LuaTable();
    }
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable store = new LuaTable();
        
        store.set("get", new _get());
        store.set("set", new _set());
        
        env.set("store", store);
        return store;
    }
    
    final class _get extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            return table.get(arg);
        }
    }
    
    final class _set extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            try {
                table.set(arg1, arg2);
            } catch(Exception e) {
                throw new LuaError(e.getClass().getSimpleName() + " - " + e.getMessage());
            }
            
            return LuaValue.NIL;
        }
    }
}
