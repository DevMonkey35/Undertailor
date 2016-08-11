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
import org.luaj.vm2.Varargs;
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
import me.scarlet.undertailor.lua.lib.OsLib;
import me.scarlet.undertailor.lua.lib.TextsLib;
import me.scarlet.undertailor.util.LuaUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Manager class responsible for generating {@link Globals}
 * objects with their appropriate libraries to load Lua
 * scripts with.
 */
public class ScriptManager {

    static final Logger log = LoggerFactory.getLogger(ScriptManager.class);

    private Undertailor undertailor;

    private File scriptPath;
    private Globals globals;

    public ScriptManager(Undertailor undertailor) {
        this.scriptPath = null;
        this.undertailor = undertailor;
    }

    public void load() {
        this.globals = this.generateGlobals();
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

        if (!scriptPath.exists()) {
            scriptPath.mkdirs();
        }

        BaseLib.setScriptPath(baseLibPath);
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
        LuaValue value = this.runScript(luaFile).arg(1);
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
    public Varargs runScript(File luaFile) throws FileNotFoundException {
        InputStream stream = new FileInputStream(luaFile);
        String chunkname = "@" + luaFile.getName();
        return globals.load(stream, chunkname, "bt", this.globals).invoke();
    }

    // ---------------- internal methods ----------------

    /**
     * Internal method.
     * 
     * <p>Generates a {@link Globals} object with all Lua
     * and Undertailor libraries loaded, used for all Lua
     * operations of Undertailor.</p>
     */
    private Globals generateGlobals() {
        Globals returned = new Globals();
        LoadState.install(returned);
        LuaC.install(returned);

        // core Lua libraries
        returned.load(new JseBaseLib());
        returned.load(new PackageLib());
        returned.load(new DebugLib());
        returned.load(new Bit32Lib());
        returned.load(new TableLib());
        returned.load(new StringLib());
        returned.load(new JseMathLib());
        returned.load(new JseOsLib());

        // undertailor libraries
        returned.load(new BaseLib(this));
        returned.load(new OsLib());
        returned.load(new GameLib(undertailor));
        returned.load(new ColorsLib());
        returned.load(new TextsLib(undertailor));
        returned.load(new MetaLib());

        // Clean the globals.
        LuaValue lib = returned.get("os");
        if (lib.istable()) {
            lib.set("execute", LuaValue.NIL);
            lib.set("exit", LuaValue.NIL);
            lib.set("remove", LuaValue.NIL);
            lib.set("rename", LuaValue.NIL);
            lib.set("setlocale", LuaValue.NIL);
            lib.set("tmpname", LuaValue.NIL);
        }

        returned.set("load", LuaValue.NIL);
        returned.set("loadfile", LuaValue.NIL);
        returned.set("collectgarbage", LuaValue.NIL);

        returned.set("debug", LuaValue.NIL);
        returned.set("package", LuaValue.NIL);

        return returned;
    }
}
