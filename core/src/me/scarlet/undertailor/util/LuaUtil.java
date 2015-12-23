package me.scarlet.undertailor.util;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.manager.StyleManager;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.BaseLib;
import org.luaj.vm2.lib.jse.JseBaseLib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class LuaUtil {
    
    /**
     * Iterate through the values of a {@link LuaTable}.
     * 
     * <p>The {@link Varargs} given to the consumer contains the current
     * key/value pair being processed, with the key as the first argument
     * (Varargs.arg(1)) and the value as the second (Varargs.arg(2)).</p>
     * 
     * @param table the LuaTable to iterate through
     * @param consumer the Consumer that processes the key/value pairs
     */
    public static void iterateTable(LuaTable table, Consumer<Varargs> consumer) {
        LuaValue key = LuaValue.NIL;
        while(true) {
            Varargs pair = table.next(key);
            if(pair.isnil(1)) {
                break;
            }
            
            consumer.accept(pair);
            key = pair.arg1();
        }
    }
    
    public static Map<String, LuaFunction> checkImplementation(Globals table, File scriptFile, String[] requiredMethods) throws LuaScriptException {
        Map<String, LuaFunction> functions = getScriptFunctions(table, scriptFile);
        if(requiredMethods != null) {
            for(String method : requiredMethods) {
                if(!functions.containsKey(method)) {
                    throw new LuaScriptException("failed to implement required method " + method);
                }
            }
        }
        
        return functions;
    }
    
    public static Map<String, LuaFunction> getScriptFunctions(Globals globals, File scriptFile) {
        if(!scriptFile.exists()) {
            return null;
        }
        
        if(scriptFile.isDirectory()) {
            return null;
        }
        
        Map<String, LuaFunction> functions = new HashMap<>();
        if(globals == null) {
            globals = new Globals();
            BaseLib base = new JseBaseLib();
            LoadState.install(globals);
            LuaC.install(globals);
            globals.baselib = base;
            globals.finder = base;
            try {
                LuaUtil.loadFile(globals, scriptFile);
            } catch(FileNotFoundException e) {
                Undertailor.instance.error(StyleManager.MANAGER_TAG, "failed to load style: file " + scriptFile.getAbsolutePath() + " wasn't found");
            }
        }
        
        iterateTable(globals, entry -> {
            if(entry.arg(2).tojstring().startsWith("function: @")) {
                functions.put(entry.arg(1).tojstring(), entry.arg(2).checkfunction());
            }
        });
        
        return functions;
    }
    
    public static void checkArguments(Varargs args, int min, int max) {
        if(args.narg() < min || (max <= 0 ? false : args.narg() > max)) {
            throw new LuaError("arguments insufficient or overflowing (min " + min + (max <= 0 ? ")" : " max " + max + ")"));
        }
    }
    
    public static void loadFile(Globals loader, File scriptFile) throws FileNotFoundException {
        InputStream stream = loader.finder.findResource(scriptFile.getAbsolutePath());
        if(stream == null) {
            throw new FileNotFoundException(scriptFile.getAbsolutePath());
        }
        
        String chunkname = "@" + scriptFile.getName();
        loader.load(stream, chunkname, "bt", loader).invoke();
    }
}
