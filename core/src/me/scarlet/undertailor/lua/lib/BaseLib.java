/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without
 * limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package me.scarlet.undertailor.lua.lib;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Replaces core functions to either function correctly with
 * the engine, or function more safely.
 */
public class BaseLib extends LuaLibrary {

    static Logger log = LoggerFactory.getLogger(BaseLib.class);
    private static String scriptPath;
    private static final Map<String, Varargs> LOADED;

    static {
        LOADED = new HashMap<>();
    }

    /**
     * Internal method.
     * 
     * <p>Used by <code>require</code>.</p>
     */
    static Varargs loadLib(Globals globals, String lib) {
        String path = new File(scriptPath, lib).getAbsolutePath();
        if (!path.endsWith(".lua"))
            path += ".lua";

        if (LOADED.containsKey(path)) {
            return LOADED.get(path);
        }

        Varargs loadedLib = globals.loadfile(path).invoke();
        LOADED.put(path, loadedLib);
        return loadedLib;
    }

    /**
     * Internal method.
     * 
     * <p>Used by <code>dofile</code>.</p>
     */
    static Varargs doFile(Globals globals, String file) {
        String path = new File(scriptPath, file).getAbsolutePath();
        if (!path.endsWith(".lua"))
            path += ".lua";

        return globals.loadfile(path).invoke();
    }

    /**
     * Sets the script path used when loading other script
     * files.
     * 
     * <p>The script path ensures that any scripts loaded
     * through require or dofile is within the provided
     * path. Paths to script files are always
     * <code>scriptPath + path</code>.</p>
     * 
     * @param scriptPath the path to the scripts directory
     */
    public static void setScriptPath(String scriptPath) {
        BaseLib.scriptPath = scriptPath;
        if (!BaseLib.scriptPath.endsWith("/"))
            BaseLib.scriptPath += "/";
    }

    private Globals globals;

    public BaseLib() {
        super(null);

        // print(string) -- Prints to console.
        this.registerFunction("print", vargs -> {
            Lua.log("[print] " + vargs.arg(1).tojstring());
            return LuaValue.NIL;
        });

        // require(path) -- Loads a file, executes and returns its result.
        //               -- If already done before, returns the old result.
        this.registerFunction("require", vargs -> {
            return BaseLib.loadLib(globals, vargs.arg(1).checkjstring());
        });

        // dofile(path) -- Loads a file. Does not cache.
        this.registerFunction("dofile", vargs -> {
            return BaseLib.doFile(globals, vargs.arg(1).checkjstring());
        });
    }

    @Override
    public void postinit(LuaTable table, LuaValue environment) {
        this.globals = environment.checkglobals();
    }
}
