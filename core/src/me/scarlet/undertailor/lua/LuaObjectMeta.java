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

/**
 * Skeleton implementation for metadata associated with
 * {@link LuaObjectValue}s who store specific types of
 * objects.
 */
public interface LuaObjectMeta {

    /**
     * Returns the class type an object held by a
     * {@link LuaObjectValue} needs to be to consider this
     * {@link LuaObjectMeta} as possible metadata for the
     * former.
     */
    public Class<?> getTargetObjectClass();

    /**
     * Returns the metatable to apply to applicable
     * {@link LuaObjectValue}s.
     * 
     * <p>The metatable should only be a table of
     * metamethods, the script manager will handle
     * conversion into <code>{__index = table}</code>
     * form.</p>
     * 
     * @return the metatable for this {@link LuaObjectMeta}
     */
    public LuaTable getMetatable();

    /**
     * Returns the typename to return when the
     * <code>type()</code> function is called with a
     * {@link LuaObjectValue} holding this
     * {@link LuaObjectMeta} as metadata passed as a
     * parameter.
     * 
     * @return the typename for objects primarily
     *         identifying with this {@link LuaObjectMeta}
     */
    public String getTypeName();

    // ---------------- default methods ----------------

    /**
     * Returns whether or not this {@link LuaObjectMeta}
     * should be considered a priority type, that is, if an
     * object is of this meta's target type, this object
     * meta will be one of the first choices.
     * 
     * @return whether or not this meta should take priority
     */
    default boolean isPrimaryType() {
        return false;
    }

    /**
     * Called after the metatable of this
     * {@link LuaObjectMeta} and others that match the
     * target {@link LuaObjectValue}'s class are compiled
     * into the provided metatable.
     */
    default void postMetaInit(LuaTable metatable) {}

    // ---------------- convenience methods because i'm an ass ----------------

    /**
     * Convenience method to forward value setting on this
     * {@link LuaObjectMeta}'s metatable.
     * 
     * <p>This will raise a {@link NullPointerException}
     * should {@link #getMetatable()} return null.</p>
     * 
     * @param key the key
     * @param value the value
     */
    default void set(String key, LuaValue value) {
        this.getMetatable().set(key, value);
    }
}
