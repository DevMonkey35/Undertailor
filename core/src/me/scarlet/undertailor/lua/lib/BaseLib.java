package me.scarlet.undertailor.lua.lib;

import me.scarlet.undertailor.Undertailor;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class BaseLib extends TwoArgFunction {
    
    private Globals globals;
    private Set<File> loadedLibs;
    
    public BaseLib() {
        loadedLibs = new HashSet<File>();
    }
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        globals = env.checkglobals();
        
        globals.set("require", new _require());
        globals.set("execute", new _execute());
        globals.set("print", new _print());
        
        // removals
        globals.set("load", LuaValue.NIL);
        globals.set("dofile", LuaValue.NIL);
        globals.set("loadfile", LuaValue.NIL);
        globals.set("collectgarbage", LuaValue.NIL);
        
        globals.get("os").set("exit", LuaValue.NIL);
        globals.get("os").set("rename", LuaValue.NIL);
        globals.get("os").set("remove", LuaValue.NIL);
        globals.get("os").set("setlocale", LuaValue.NIL);
        return env;
    }
    
    final class _require extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String path = arg.checkjstring();
            BaseLib.this.load(path, true);
            return LuaValue.NIL;
        }
    }
    
    final class _execute extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String path = arg.checkjstring();
            BaseLib.this.load(path, false);
            return LuaValue.NIL;
        }
    }
    
    static class _print extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if(args.narg() == 1) {
                Undertailor.instance.log("[LUA] luascript", args.arg1().checkjstring());
            } else if(args.narg() == 2) {
                Undertailor.instance.log("[LUA] " + args.arg(1).checkjstring(), args.arg(2).checkjstring());
            } else {
                throw new LuaError("arguments insufficient or overflowing (min 1, max 2)");
            }
            
            return LuaValue.NIL;
        }
    }
    
    public LuaValue load(String path, boolean save) {
        if(path.startsWith("/") || path.startsWith("\\") || path.startsWith("" + File.separatorChar)) {
            path = path.substring(1);
        }
        
        File file = new File(System.getProperty("user.dir") + File.separatorChar + path);
        Undertailor.instance.log("lua", "loading lua file at " + file.getAbsolutePath());
        if(file.isDirectory()) {
            Undertailor.instance.error("lua", "cannot load a directory; load the specific path to the file");
            return LuaValue.NIL;
        }
        
        if(file.isFile()) {
            if(isFileLoaded(file)) {
                Undertailor.instance.warn("lua", "skipping loading of " + file.getAbsolutePath() + " (already loaded)");
                return LuaValue.NIL;
            } else {
                String libName = file.getName().split("\\.")[0];
                try {
                    loadFile(libName, file, save);
                    Undertailor.instance.debug("lua", "loaded lua file at path " + path);
                } catch(FileNotFoundException e) {
                    Undertailor.instance.error("lua", "could not load lib at path " + path + " (could not find file)");
                } catch(Exception e) {
                    Undertailor.instance.error("lua", "could not load lib at path " + path + ": " + e.getClass().getSimpleName() + " - " + e.getMessage());
                }
            }
        } else {
            Undertailor.instance.error("lua", "somehow tried to load something that wasn't a directory or file");
        }
        
        return LuaValue.NIL;
    }
    
    public boolean isFileLoaded(File file) {
        return loadedLibs.contains(file);
    }
    
    public LuaTable loadFile(String libName, File file, boolean save) throws FileNotFoundException {
        InputStream input = globals.finder.findResource(file.getAbsolutePath());
        if ( input == null )
            throw new FileNotFoundException();
        try {
            LuaTable table = Undertailor.newGlobals();
            if(save) {
                globals.set(libName, table);
            }
            
            globals.load(input, "@" + file.getName(), "bt", table).invoke();
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
