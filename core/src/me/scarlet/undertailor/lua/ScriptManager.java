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
import org.luaj.vm2.lib.DebugLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseOsLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.lib.BaseLib;
import me.scarlet.undertailor.lua.lib.ColorsLib;
import me.scarlet.undertailor.lua.lib.GameLib;
import me.scarlet.undertailor.lua.lib.MetaLib;
import me.scarlet.undertailor.lua.lib.TextLib;
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

    static final Logger log = LoggerFactory.getLogger(ScriptManager.class);

    private Undertailor undertailor;

    private File scriptPath;
    private List<LuaValue> libraries;
    private List<Class<? extends LuaValue>> baseLibraries;

    public ScriptManager(Undertailor undertailor) {
        this.scriptPath = null;
        this.libraries = new ArrayList<>();
        this.baseLibraries = new ArrayList<>();
        this.undertailor = undertailor;
    }

    public void load() {
        // base libs are libs that require a new instance for each Globals environment
        baseLibraries.add(JseBaseLib.class);
        baseLibraries.add(PackageLib.class);
        baseLibraries.add(DebugLib.class);
        baseLibraries.add(BaseLib.class);

        // generic libs can use the same instance across each Globals environment
        libraries.add(new Bit32Lib());
        libraries.add(new TableLib());
        libraries.add(new StringLib());
        libraries.add(new JseMathLib());
        libraries.add(new JseOsLib());

        libraries.add(new BaseLib());
        libraries.add(new GameLib(undertailor));
        libraries.add(new ColorsLib());
        libraries.add(new TextLib(undertailor));
        libraries.add(new MetaLib());
    }

    /**
     * Returns the path where scripts should only be loaded
     * from.
     * 
     * @return the path containing the game's scripts
     */
    public File getScriptPath() {
        return this.scriptPath;
    }

    /**
     * Sets the path where scripts should only be loaded
     * from.
     * 
     * @param scriptPath the path containing the game's
     *        scripts
     */
    public void setScriptPath(File scriptPath) {
        this.scriptPath = scriptPath;
        String baseLibPath = scriptPath.getAbsolutePath();
        if (!baseLibPath.endsWith(File.separator)) {
            baseLibPath += File.separator;
        }

        if(!scriptPath.exists()) {
            scriptPath.mkdirs();
        }

        BaseLib.setScriptPath(baseLibPath);
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
        LoadState.install(returned);
        LuaC.install(returned);

        this.baseLibraries.forEach(clazz -> {
            try {
                returned.load(clazz.newInstance());
            } catch (Exception e) {
                log.error("Failed to load base library of class " + clazz.getSimpleName(), e);
            }
        });

        this.libraries.forEach(returned::load);
        this.cleanGlobals(returned);

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
        LuaValue value = this.runScript(luaFile);
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

    /**
     * Runs the provided Lua script file.
     * 
     * @param luaFile the Lua script file to run
     * 
     * @return the {@link LuaValue} returned by the script
     *         file
     * 
     * @throws FileNotFoundException if the file wasn't
     *         found
     */
    public LuaValue runScript(File luaFile) throws FileNotFoundException {
        Globals globals = this.generateGlobals();
        InputStream stream = globals.finder.findResource(luaFile.getAbsolutePath());
        if (stream == null) {
            throw new FileNotFoundException(luaFile.getAbsolutePath());
        }

        String chunkname = "@" + luaFile.getName();
        return globals.load(stream, chunkname, "bt", globals).invoke().arg(1);
    }

    // ---------------- internal methods ----------------

    /**
     * Internal method.
     * 
     * <p>Removes disallowed functions from the provided
     * {@link Globals} object.</p>
     */
    private void cleanGlobals(Globals globals) {
        LuaValue lib = globals.get("os");
        if (lib.istable()) {
            lib.set("execute", LuaValue.NIL);
            lib.set("exit", LuaValue.NIL);
            lib.set("remove", LuaValue.NIL);
            lib.set("rename", LuaValue.NIL);
            lib.set("setlocale", LuaValue.NIL);
            lib.set("tmpname", LuaValue.NIL);
        }

        globals.set("load", LuaValue.NIL);
        globals.set("dofile", LuaValue.NIL);
        globals.set("loadfile", LuaValue.NIL);
        globals.set("collectgarbage", LuaValue.NIL);

        globals.set("debug", LuaValue.NIL);
        globals.set("package", LuaValue.NIL);
    }
}
