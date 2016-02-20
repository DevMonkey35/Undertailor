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
import org.luaj.vm2.lib.VarArgFunction;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
    
    public static final String PARENT_GLOBAL_KEY = "_TLR_PARENTGLOBAL";
    
    public static final LuaLibraryComponent[] COMPONENTS = {}; // not a shareable library; we don't use this
    
    private Globals globals;
    
    public BaseLib() {
        super(null,
                new loadscript(),
                new execute(),
                new print());
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
        globals.package_.setLuaPath(System.getProperty("user.dir") + File.separatorChar);
        
        // removals
        globals.set("load", LuaValue.NIL);
        globals.set("dofile", LuaValue.NIL);
        globals.set("loadfile", LuaValue.NIL);
        globals.set("package", LuaValue.NIL);
        globals.set("collectgarbage", LuaValue.NIL);
        
        globals.get("os").set("exit", LuaValue.NIL);
        globals.get("os").set("rename", LuaValue.NIL);
        globals.get("os").set("remove", LuaValue.NIL);
        globals.get("os").set("setlocale", LuaValue.NIL);
        
        globals.set(PARENT_GLOBAL_KEY, LuaValue.NIL);
        
        super.call(modname, env);
        
        return env;
    }
    
    // ##########################
    // #   Library functions.   #
    // ##########################
    
    static final class loadscript extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            String path = args.checkjstring(1);
            BaseLib lib = ((BaseLib) this.getLibraryInstance());
            return lib.loadFile(path);
        }
    }
    
    static final class execute extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) { // constructor name, so what; we're a function
            LuaUtil.checkArguments(args, 1, 1);
            
            String path = args.checkjstring(1);
            BaseLib lib = ((BaseLib) this.getLibraryInstance());
            lib.loadFile(path);
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
    
    /*static final class error extends LibraryFunction {
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            BaseLib lib = ((BaseLib) this.getLibraryInstance());
            LuaValue parentGlobals = lib.globals.get(PARENT_GLOBAL_KEY);
            if(parentGlobals.isnil()) {
                throw args.isnil(1)? new LuaError(null, args.optint(2, 1)): 
                    args.isstring(1)? new LuaError(args.tojstring(1), args.optint(2,1)): 
                        new LuaError(args.arg1());
            } else {
                return parentGlobals.get("error").invoke(args);
            }
        }
    }*/
    
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
        File file = new File(Undertailor.ASSETS_DIRECTORY, path);
        InputStream input = globals.finder.findResource(file.getAbsolutePath());
        if (input == null)
            throw new LuaError("file at " + file.getAbsolutePath() + " was not found");
        try {
            Globals table = Undertailor.getScriptManager().generateGlobals();
            globals.load(input, "@" + file.getName(), "bt", table).invoke();
            table.set(PARENT_GLOBAL_KEY, this.globals);
            
            Map<String, LuaValue> replaceQueue = new HashMap<>();
            LuaUtil.iterateTable(table, args -> {
                if(args.arg(1).isstring() && args.arg(2).tojstring().startsWith("function: @" + file.getName())) {
                    replaceQueue.put(args.arg1().tojstring(), args.arg(2));
                }
            });
            
            for(Entry<String, LuaValue> entry : replaceQueue.entrySet()) {
                table.set(entry.getKey(), new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        try {
                            return entry.getValue().invoke(args);
                        } catch(LuaError e) {
                            throw new LuaError("\n\t" + e.getMessage());
                        }
                    }
                });
            }
            
            return table;
        } finally {
            try {
                input.close();
            } catch ( Exception ignored) {}
        }
    }
}
