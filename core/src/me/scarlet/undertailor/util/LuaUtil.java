/* 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
        }
        
        try {
            LuaUtil.loadFile(globals, scriptFile);
        } catch(FileNotFoundException e) {
            Undertailor.instance.error(StyleManager.MANAGER_TAG, "failed to load style: file " + scriptFile.getAbsolutePath() + " wasn't found");
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
    
    @SuppressWarnings("unchecked")
    public static <T extends LuaValue> T checkType(LuaValue value, String typename) {
        if(!isOfType(value, typename)) {
            throw new LuaError("expected " + typename + ", got " + value.typename());
        }
        
        return (T) value;
    }
    
    public static boolean isOfType(LuaValue value, String typename) {
        if(value.typename().equals(typename)) {
            return true;
        }
        
        return false;
    }
    
    public static LuaValue toMetatable(LuaValue functionTable) {
        return asPairTable(LuaValue.INDEX, functionTable);
    }
    
    public static Varargs asVarargs(LuaValue... values) {
        if(values.length <= 0) {
            return LuaValue.NIL;
        }
        
        return LuaValue.varargsOf(values);
    }
    
    public static LuaValue asTable(LuaValue... values) {
        if(values.length <= 0) {
            return LuaValue.NIL;
        }
        
        return LuaValue.listOf(values);
    }
    
    public static LuaValue asPairTable(LuaValue... values) {
        if(values.length <= 0) {
            return LuaValue.NIL;
        }
        
        return LuaValue.tableOf(values);
    }
    
    public static Varargs invokeNonNull(LuaFunction func, Varargs args) {
        if(func != null) {
            return func.invoke(args);
        }
        
        return null;
    }
    
    public static Varargs invokeNonNull(LuaFunction func, LuaValue... args) {
        return invokeNonNull(func, asVarargs(args));
    }
    
    public static String formatJavaException(Exception e) {
        return e.getClass().getSimpleName() + " - " + e.getMessage();
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
