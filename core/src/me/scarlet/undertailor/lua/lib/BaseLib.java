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

import static me.scarlet.undertailor.util.LuaUtil.asFunction;

import com.badlogic.gdx.utils.ObjectMap;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.ScriptManager;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Replaces core functions to either function correctly with
 * the engine, or function more safely.
 */
public class BaseLib extends LuaLibrary {

    static Logger log = LoggerFactory.getLogger(BaseLib.class);
    private static String scriptPath;
    private static final ObjectMap<String, Varargs> LOADED;

    static {
        LOADED = new ObjectMap<>();
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
    }

    public BaseLib(ScriptManager manager) {
        super(null);

        // print(...) -- Prints to console.
        this.set("print", asFunction(vargs -> {
            String str = "[print] ";
            for(int i = 0; i < vargs.narg(); i++) {
                str = str.concat(vargs.arg(i + 1).tojstring());
                if(i + 1 != vargs.narg()) {
                    str = str.concat("\t");
                }
            }

            Lua.log(str);
            return LuaValue.NIL;
        }));

        // require(path) -- Loads a file, executes and returns its result.
        //               -- If already done before, returns the old result.
        this.set("require", asFunction(vargs -> {
            String path = vargs.checkjstring(1);
            File file = new File(scriptPath, path);

            String abs = file.getAbsolutePath();
            if (LOADED.containsKey(abs)) {
                return LOADED.get(abs);
            }

            try {
                Varargs loadedLib = manager.runScript(file);
                LOADED.put(path, loadedLib);
                return loadedLib;
            } catch (FileNotFoundException wontHappen) {
                throw new LuaError("module " + path + " not found");
            }
        }));

        // dofile(path) -- Loads a file. Does not cache.
        this.set("dofile", asFunction(vargs -> {
            String path = vargs.checkjstring(1);
            File file = new File(scriptPath, path);

            try {
                return manager.runScript(file);
            } catch (FileNotFoundException e) {
                throw new LuaError("file at " + path + " not found");
            }
        }));

        // istype(obj, type) -- Checks the type.
        this.set("istype", asFunction(vargs -> {
            LuaValue obj = vargs.checkvalue(1);
            String typename = vargs.checkjstring(2);

            // simply check the object typename first
            if (obj.typename().equals(typename)) {
                return valueOf(true);
            }

            // wasn't? check for if its a tailor type
            if (obj instanceof LuaObjectValue<?>) {
                LuaObjectMeta meta = Lua.getMeta(typename);
                System.out.println("meta null?: " + (meta == null));
                if (meta != null) {
                    return valueOf(meta.getTargetObjectClass().isInstance(((LuaObjectValue<?>) obj).getObject()));
                }
            }

            return valueOf(false);
        }));
    }
}
