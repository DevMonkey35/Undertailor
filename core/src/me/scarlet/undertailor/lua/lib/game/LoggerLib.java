package me.scarlet.undertailor.lua.lib.game;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LoggerLib extends LuaLibrary {
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new log(),
            new warn(),
            new error()
    };
    
    public LoggerLib() {
        super("logger", COMPONENTS);
    }
    
    static class log extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            String tag = args.narg() == 2 ? args.checkjstring(1) : null;
            String message = args.narg() == 2 ? args.checkjstring(2) : args.checkjstring(1);
            ((LoggerLib) this.getLibraryInstance()).log(0, tag, message, 1);
            return LuaValue.NIL;
        }
    }
    
    static class warn extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            String tag = args.narg() == 2 ? args.checkjstring(1) : null;
            String message = args.narg() == 2 ? args.checkjstring(2) : args.checkjstring(1);
            ((LoggerLib) this.getLibraryInstance()).log(1, tag, message, 1);
            return LuaValue.NIL;
        }
    }
    
    static class error extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 3);
            
            String tag = null, message;
            int level = 1;
            
            if(args.narg() == 1) { // error(message)
                message = args.checkjstring(1);
            } else if(args.narg() == 2) {
                if(args.type(2) == LuaValue.TSTRING) { // error(tag, message), use type() since isnumber/isstring accepts both numbers/strings
                    tag = args.checkjstring(1);
                    message = args.checkjstring(2);
                } else { // error(message, level)
                    message = args.checkjstring(1);
                    level = args.checkint(2);
                }
            } else { // error(tag, message, level)
                tag = args.checkjstring(1);
                message = args.checkjstring(2);
                level = args.checkint(3);
            }
            
            ((LoggerLib) this.getLibraryInstance()).log(2, tag, message, level);
            return LuaValue.NIL;
        }
    }
    
    public void log(int type, String tag, String message, int level) {
        switch(type) {
            default:
            case 0:
                Undertailor.instance.log(tag, message);
                break;
            case 1:
                Undertailor.instance.warn(tag, message);
                break;
            case 2:
                throw new LuaError("[" + tag + "] " + message, level);
        }
    }
}
