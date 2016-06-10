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

package me.scarlet.undertailor.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;

import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.util.LuaUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager class responsible for generating {@link Globals}
 * objects with their appropriate libraries to load Lua
 * scripts with.
 */
public class ScriptManager {

    private List<LuaValue> libraries;

    public ScriptManager() {
        this.libraries = new ArrayList<>();

        libraries.add(new Bit32Lib());
        libraries.add(new TableLib());
        libraries.add(new StringLib());
        libraries.add(new JseMathLib());
    }

    /**
     * Registers a {@link LuaLibrary} to be registered into
     * any {@link Globals} object this {@link ScriptManager}
     * generates.
     * 
     * @param library a LuaLibrary to register
     */
    public void registerLibrary(LuaLibrary library) {
        if (!this.libraries.contains(library))
            this.libraries.add(library);
    }

    /**
     * Generates a {@link Globals} object with most default
     * Lua libraries and any LuaLibraries (
     * {@link LuaLibrary}) registered with this
     * {@link ScriptManager}.
     * 
     * @return a new Globals object
     */
    public Globals generateGlobals() {
        Globals returned = new Globals();
        returned.load(new JseBaseLib());
        returned.load(new PackageLib());

        returned.set("debug", LuaValue.NIL);
        LoadState.install(returned);
        LuaC.install(returned);

        this.libraries.forEach(returned::load);

        return returned;
    }

    /**
     * Returns a {@link LuaTable} representative of the
     * module loaded from the provided Lua script file.
     * 
     * @param luaFile the Lua script File to load from
     * 
     * @return the module loaded from the given File
     * 
     * @throws FileNotFoundException if the file wasn't
     *         found
     * @throws LuaScriptException if the script isn't
     *         written as a module
     */
    public LuaTable loadAsModule(File luaFile) throws FileNotFoundException, LuaScriptException {
        return this.loadAsModule(luaFile, null);
    }

    /**
     * Returns the provided {@link LuaTable} injected with
     * the contents of the module loaded from the provided
     * Lua script file.
     * 
     * @param luaFile the Lua script File to load from
     * @param store the LuaTable to inject the module into
     * 
     * @return the module loaded from the given File
     * 
     * @throws FileNotFoundException if the file wasn't
     *         found
     * @throws LuaScriptException if the script isn't
     *         written as a module
     */
    public LuaTable loadAsModule(File luaFile, LuaTable store)
        throws FileNotFoundException, LuaScriptException {
        Globals globals = this.generateGlobals();
        InputStream stream = globals.finder.findResource(luaFile.getAbsolutePath());
        if (stream == null) {
            throw new FileNotFoundException(luaFile.getAbsolutePath());
        }

        String chunkname = "@" + luaFile.getName();
        LuaValue value = globals.load(stream, chunkname, "bt", globals).invoke().arg(1);
        if (value.istable()) {
            if (store == null) {
                return (LuaTable) value;
            }

            LuaUtil.iterateTable((LuaTable) value, vargs -> {
                store.set(vargs.arg(1), vargs.arg(2));
            });

            return store;
        } else {
            throw new LuaScriptException("Script does not resolve as a module");
        }
    }
}