package me.scarlet.undertailor.lua.lib;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.File;
import java.io.InputStream;

/**
 * Extended base library.
 * 
 * <p>This library should be loaded after all normal libraries have been applied
 * to the target {@link Globals} object. Attempting to load this onto a gGobals
 * object before loading normal libraries occurs in this library's function
 * replacements being replaced by their defaults. Attempting to load this with
 * an environment that is not a Globals object will result in errors.</p>
 * 
 * <p>This library should not be shared between different instances of Globals
 * objects, as this library contains functions dependent on knowing the current
 * Globals instance.</p>
 */
public class BaseLib extends LuaLibrary {
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new load(),
            new execute(),
            new print()
    };
    
    private Globals globals;
    
    public BaseLib() {
        super(null, COMPONENTS);
    }
    
    /**
     * Library instantiation stuffs.
     * 
     * <p>Blanks out functions that shouldn't be exposed and executes default
     * functionality.</p>
     */
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        globals = env.checkglobals();
        
        // removals
        globals.set("load", LuaValue.NIL);
        globals.set("dofile", LuaValue.NIL);
        globals.set("loadfile", LuaValue.NIL);
        globals.set("collectgarbage", LuaValue.NIL);
        
        globals.get("os").set("exit", LuaValue.NIL);
        globals.get("os").set("rename", LuaValue.NIL);
        globals.get("os").set("remove", LuaValue.NIL);
        globals.get("os").set("setlocale", LuaValue.NIL);
        
        super.call(modname, env);
        
        return env;
    }
    
    // ##########################
    // #   Library functions.   #
    // ##########################
    
    static final class load extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            String path = args.checkjstring(1);
            return ((BaseLib) this.getLibraryInstance()).loadFile(path);
        }
    }
    
    static final class execute extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) { // constructor name, so what; we're a function
            LuaUtil.checkArguments(args, 1, 1);
            
            String path = args.checkjstring(1);
            ((BaseLib) this.getLibraryInstance()).loadFile(path);
            return LuaValue.NIL;
        }
    }
    
    static final class print extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            if(args.narg() == 1) {
                Undertailor.instance.log("[LUA] luascript ", args.arg1().checkjstring());
            } else if(args.narg() == 2) {
                Undertailor.instance.log("[LUA] " + args.arg(1).checkjstring(), args.arg(2).checkjstring());
            }
            
            return LuaValue.NIL;
        }
    }
    
    // ########################
    // #   Class methods.   #
    // ########################
    
    @Override
    public boolean isShareable() {
        return false;
    }
    
    @Override
    public LuaLibrary cleanClone() {
        return new BaseLib();
    }
    
    public LuaTable loadFile(String path) throws LuaError {
        File file = new File(System.getProperty("user.dir") + File.separatorChar + path);
        InputStream input = globals.finder.findResource(file.getAbsolutePath());
        if (input == null)
            throw new LuaError("file at " + file.getAbsolutePath() + " was not found");
        try {
            Globals table = Undertailor.getScriptManager().generateGlobals();
            table.load(input, "@" + file.getName(), "bt", table).invoke();
            return table;
        } catch(Exception e) {
            throw e;
        } finally {
            try {
                input.close();
            } catch ( Exception e ) {}
        }
    }
}
