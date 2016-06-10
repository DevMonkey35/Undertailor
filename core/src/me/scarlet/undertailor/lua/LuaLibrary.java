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

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class LuaLibrary extends TwoArgFunction {

    private String name;
    private Map<String, VarArgFunction> functions;

    public LuaLibrary(String name) {
        this.name = name;
        this.functions = new HashMap<>();
    }

    public final void registerFunction(String funcId, Function<Varargs, Varargs> func) {
        functions.put(funcId, new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return func.apply(args);
            }
        });
    }

    @Override
    public final LuaValue call(LuaValue modname, LuaValue env) {
        if (name == null) {
            this.inject((LuaTable) env);
            this.postinit((LuaTable) env, env);
            return env;
        } else {
            LuaTable table = new LuaTable();
            this.inject(table);

            if (env != null)
                env.set(name, table);

            this.postinit(table, env);
            return table;
        }
    }

    public final void inject(LuaTable table) {
        functions.keySet().forEach(key -> {
            table.set(key, functions.get(key));;
        });
    }

    public void postinit(LuaTable table, LuaValue environment) {}
}
