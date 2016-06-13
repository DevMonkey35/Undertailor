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

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Skeleton implementation for an object that can be
 * implemented by a Lua script.
 *
 * @param <T> the type of the Java object the script is
 *        implementing
 */
public interface LuaImplementable<T> {

    // ---------------- implementable methods ----------------

    /**
     * Returns the {@link LuaObjectValue} representing this
     * {@link LuaImplementable} as a Lua object.
     * 
     * @return the LuaObjectValue associated with this
     *         implementable
     */
    LuaObjectValue<T> getObjectValue();

    /**
     * Sets the {@link LuaObjectValue} representing this
     * {@link LuaImplementable} as a Lua object.
     * 
     * <p>This method should not actually be invoked by
     * anything other than a {@link ScriptManager} loading
     * the script that implements this implementable.</p>
     * 
     * @param the new LuaObjectValue associated with this
     *        implementable
     */
    void setObjectValue(LuaObjectValue<T> value);

    // ---------------- optional methods ----------------

    /**
     * Returns the {@link Class} that this
     * {@link LuaImplementable} primarily identifies itself
     * as.
     * 
     * <p>Used to choose the correct primary
     * {@link LuaObjectMeta} when loading the object value's
     * metadata.</p>
     * 
     * @return this implementable's primary identifying
     *         Class
     */
    default Class<?> getPrimaryIdentifyingClass() {
        return null;
    }

    // ---------------- default functional methods ----------------

    /**
     * Returns whether or not the Lua script implementing
     * this {@link LuaImplementable} registered a specific
     * function on its object value.
     * 
     * @param funcName the name of the function
     * 
     * @return if the function exists
     */
    default boolean hasFunction(String funcName) {
        return !this.getObjectValue().get(funcName).isnil();
    }

    /**
     * Convenience method for invoking a function held by
     * the implementing Lua script.
     * 
     * @param funcName the name of the function to invoke
     * @param args the arguments to pass to the function
     * 
     * @return the values returned by the invoked function
     */
    default Varargs invoke(String funcName, LuaValue... args) {
        if (this.getObjectValue() != null && !this.getObjectValue().get(funcName).isnil()) {
            if (args.length > 0) {
                return this.getObjectValue().get(funcName).invoke(LuaValue.varargsOf(args));
            } else {
                return this.getObjectValue().get(funcName).invoke();
            }
        }

        return null;
    }

    /**
     * Convenience method for invoking a function held by
     * the implementing Lua script, passing the Lua object
     * as its first parameter.
     * 
     * <p>Functionally equivalent to, in Lua script,
     * <code>object:funcName(args)</code>.</p>
     * 
     * @param funcName the name of the function to invoke
     * @param args the arguments to pass to the function
     * 
     * @return the values returned by the invoked function
     */
    default Varargs invokeSelf(String funcName, LuaValue... args) {
        if (this.getObjectValue() != null && !this.getObjectValue().get(funcName).isnil()) {
            if (args.length > 0) {
                return this.getObjectValue().get(funcName)
                    .invoke(LuaValue.varargsOf(this.getObjectValue(), LuaValue.varargsOf(args)));
            } else {
                return this.getObjectValue().get(funcName).invoke(this.getObjectValue());
            }
        }

        return null;
    }
}
