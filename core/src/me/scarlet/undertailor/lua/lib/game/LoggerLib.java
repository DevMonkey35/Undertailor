package me.scarlet.undertailor.lua.lib.game;

import me.scarlet.undertailor.Undertailor;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class LoggerLib extends TwoArgFunction {
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable logger = new LuaTable();
        logger.set("log", new _log());
        logger.set("warn", new _warn());
        logger.set("error", new _error());
        
        env.set("logger", logger);
        return logger;
    }
    
    static class _log extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if(args.narg() == 1) {
                Undertailor.instance.log("luascript", args.arg1().checkjstring());
            } else if(args.narg() == 2) {
                Undertailor.instance.log(args.arg(1).checkjstring(), args.arg(2).checkjstring());
            } else {
                throw new LuaError("arguments insufficient or overflowing (min 1, max 2)");
            }
            
            return LuaValue.NIL;
        }
    }
    
    static class _warn extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if(args.narg() == 1) {
                Undertailor.instance.warn("luascript", args.arg1().checkjstring());
            } else if(args.narg() == 2) {
                Undertailor.instance.warn(args.arg(1).checkjstring(), args.arg(2).checkjstring());
            } else {
                throw new LuaError("arguments insufficient or overflowing (min 1, max 2)");
            }
            
            return LuaValue.NIL;
        }
    }
    
    static class _error extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            String tag = "";
            String message = "";
            if(args.narg() == 1) {
                tag = "luascript";
                message = args.arg1().checkjstring();
            } else if(args.narg() == 2) {
                tag = args.arg(1).checkjstring();
                message = args.arg(2).checkjstring();
            } else {
                throw new LuaError("arguments insufficient or overflowing (min 1, max 2)");
            }
            
            throw new LuaError("[" + tag + "]: " + message);
        }
    }
}
