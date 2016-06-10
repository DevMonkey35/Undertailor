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

import me.scarlet.undertailor.exception.LuaScriptException;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class LuaObjectValue<T> extends LuaTable {

    static final Map<Object, LuaObjectValue<?>> STORED;

    static {
        STORED = new WeakHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public static <T> LuaObjectValue<T> of(T object) {
        if (LuaObjectValue.STORED.containsKey(object)) {
            return (LuaObjectValue<T>) LuaObjectValue.STORED.get(object);
        }

        return new LuaObjectValue<>(object);
    }

    private String typename;
    private LuaObjectMeta meta;
    private WeakReference<T> ref;

    @SuppressWarnings("unchecked")
    public LuaObjectValue(T object) {
        this.ref = new WeakReference<>(object);

        LuaObjectValue.STORED.put(object, this);
        LuaObjectMeta meta = Lua.getMeta(object);
        if (meta != null) {
            this.meta = meta;
            this.typename = meta.getTypeName();
            if(meta.getMetatable() != null) this.setmetatable(meta.getMetatable());
        }

        if (object instanceof LuaImplementable) {
            ((LuaImplementable<T>) object).setObjectValue(this);
        }
    }

    @Override
    public String typename() {
        if (this.typename != null) {
            return this.typename;
        }

        return super.typename();
    }

    public T getObject() {
        return this.ref.get();
    }

    public LuaObjectMeta getMeta() {
        return this.meta;
    }

    public void load(ScriptManager manager, File luaFile)
        throws FileNotFoundException, LuaScriptException {
        manager.loadAsModule(luaFile, this);
    }
}
