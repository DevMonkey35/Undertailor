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
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * A collection of actions executable by reference of a Lua
 * script.
 */
public class LuaLibrary extends TwoArgFunction {

    private String name;
    private Map<String, LuaFunction> functions;

    public LuaLibrary(String name) {
        this.name = name;
        this.functions = new HashMap<>();
    }

    // ---------------- abstract method implementation ----------------

    /**
     * {@inheritDoc}
     * 
     * <p>Pertaining to loading a {@link LuaLibrary}, if the
     * name of the library was set to null then its
     * functions are loaded into the provided environment.
     * Otherwise, the functions are loaded into the
     * environment within a table of the library's name.</p>
     */
    @Override
    public final LuaValue call(LuaValue modname, LuaValue env) {
        if (name == null) {
            this.inject((LuaTable) env);
            this.postinit((LuaTable) env, env);
            return env;
        } else {
            LuaTable table;
            if (env.get(name).istable()) {
                table = env.get(name).checktable();
            } else {
                table = new LuaTable();
            }

            this.inject(table);

            if (env != null)
                env.set(name, table);

            this.postinit(table, env);
            return table;
        }
    }

    // ---------------- functional methods ----------------

    /**
     * Registers a function into this {@link LuaLibrary}.
     * 
     * @param funcId the id to give the function
     * @param func the function
     */
    public final void set(String funcId, LuaFunction func) {
        functions.put(funcId, func);
    }

    /**
     * Injects the functions of this {@link LuaLibrary} into
     * the provided {@link LuaTable}.
     * 
     * <p>This directly injects into the target table unlike
     * normal installation through
     * {@link #call(LuaValue, LuaValue)}.</p>
     * 
     * @param table the table to inject into
     */
    public final void inject(LuaTable table) {
        functions.keySet().forEach(key -> {
            table.set(key, functions.get(key));
        });
    }

    // ---------------- optional implementable methods ----------------

    /**
     * Called whenever this {@link LuaLibrary} is properly
     * initialized within a {@link Globals} object by the
     * means of {@link Globals#load(LuaValue)}.
     * 
     * <p>If this library's name was set to null,
     * <code>table</code> and <code>environment</code> are
     * both the environment table.</p>
     * 
     * @param table the table containing this LuaLibrary's
     *        functions
     * @param environment the environment holding the table
     */
    public void postinit(LuaTable table, LuaValue environment) {}
}
