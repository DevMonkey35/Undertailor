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

/**
 * Generic container for Java objects to be passable as Lua
 * objects.
 *
 * @param <T> the type of the Java object to contain
 */
public class LuaObjectValue<T> extends LuaTable {

    static final Map<Object, WeakReference<LuaObjectValue<?>>> STORED;

    static {
        STORED = new WeakHashMap<>();
    }

    /**
     * Returns a {@link LuaObjectValue} holding the provided
     * object.
     * 
     * <p>If the given object is already held by an existing
     * object value, the existing instance is returned.
     * Otherwise, a new one is generated, registered and
     * returned.</p>
     * 
     * @param object the Object for the value to hold
     * 
     * @return the LuaObjectValue holding the provided
     *         object
     */
    @SuppressWarnings("unchecked")
    public static <T> LuaObjectValue<T> of(T object) {
        if(object == null) {
            return null;
        }

        if (LuaObjectValue.STORED.containsKey(object)) {
            if (LuaObjectValue.STORED.get(object).get() != null) {
                return (LuaObjectValue<T>) LuaObjectValue.STORED.get(object).get();
            }

            LuaObjectValue.STORED.remove(object);
        }

        return new LuaObjectValue<>(object);
    }

    // ---------------- object ----------------

    private String typename;
    private LuaObjectMeta meta;
    private T ref;

    @SuppressWarnings("unchecked")
    private LuaObjectValue(T object) {
        this.ref = object;

        LuaObjectValue.STORED.put(object, new WeakReference<>(this));
        LuaObjectMeta meta = Lua.getMeta(object);
        if (meta != null) {
            this.meta = meta;
            this.typename = meta.getTypeName();
        }

        LuaTable metatable = Lua.generateMetatable(object);
        if (metatable != null) {
            this.setmetatable(metatable);
        }

        if (object instanceof LuaImplementable) {
            ((LuaImplementable<T>) object).setObjectValue(this);
        }
    }

    // ---------------- abstract method implementation ----------------

    @Override
    public String typename() {
        if (this.typename != null) {
            return this.typename;
        }

        return super.typename();
    }

    // ---------------- g/s object params ----------------

    /**
     * Returns the Java object held by this
     * {@link LuaObjectValue}.
     * 
     * @return the Java object held by this value
     */
    public T getObject() {
        return this.ref;
    }

    /**
     * Returns the {@link LuaObjectMeta} assigned to this
     * {@link LuaObjectValue} as its primary identifier.
     * 
     * @return the LuaObjectMeta assigned to this object
     *         value
     */
    public LuaObjectMeta getMeta() {
        return this.meta;
    }

    // ---------------- functional methods ----------------

    /**
     * Loads the given Lua script File into this
     * {@link LuaObjectValue}.
     * 
     * <p>LuaObjectValue scripts are <strong>strictly
     * modules</strong>, that is, if the script does not
     * implement itself as a module by storing its functions
     * and relevant variables in a table and returning the
     * latter, a {@link LuaScriptException} is raised.</p>
     * 
     * @param manager the ScriptManager used to load the
     *        script
     * @param luaFile the Lua script file to load
     * 
     * @throws FileNotFoundException if the file was not
     *         found
     * @throws LuaScriptException if the script was not
     *         implemented as a module
     */
    public void load(ScriptManager manager, File luaFile)
        throws FileNotFoundException, LuaScriptException {
        manager.loadAsModule(luaFile, this);
    }
}
