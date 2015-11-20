package me.scarlet.undertailor.lua.lib;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Random;

public class MathUtilLib extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable mathutil = new LuaTable();
        mathutil.set("randomDouble", new _randomDouble());
        
        env.set("mathutil", mathutil);
        return mathutil;
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
}
