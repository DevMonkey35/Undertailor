package me.scarlet.undertailor.lua.lib;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Random;

public class UtilLib extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable util = new LuaTable();
        util.set("randomDouble", new _randomDouble());
        util.set("trim", new _trim());
        
        env.set("util", util);
        return util;
    }
    
    static class _randomDouble extends ZeroArgFunction { // temporary for a script test
        private Random random;
        
        public _randomDouble() {
            this.random = new Random();
        }
        
        @Override
        public LuaValue call() {
            return LuaValue.valueOf(random.nextDouble());
        }
    }
    
    static class _trim extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            return LuaValue.valueOf(arg.checkjstring().trim());
        }
    }
}
