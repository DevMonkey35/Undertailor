package me.scarlet.undertailor.lua.lib;

import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.Random;

public class UtilLib extends LuaLibrary {
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new randomDouble(),
            new trim()
    };
    
    public UtilLib() {
        super("util", COMPONENTS);
    }
    
    static class randomDouble extends LibraryFunction { // temporary for a script test
        private Random random;
        
        public randomDouble() {
            this.random = new Random();
        }
        
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            
            return LuaValue.valueOf(random.nextDouble());
        }
    }
    
    static class trim extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            return LuaValue.valueOf(args.checkjstring(1).trim());
        }
    }
}
