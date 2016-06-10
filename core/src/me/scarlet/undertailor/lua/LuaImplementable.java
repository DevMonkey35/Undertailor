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

public interface LuaImplementable<T> {

    LuaObjectValue<T> getObjectValue();

    void setObjectValue(LuaObjectValue<T> value);
    
    default Class<?> getPrimaryIdentifyingClass() {
        return null;
    }

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

    default Varargs invokeSelf(String funcName, LuaValue... args) {
        if (this.getObjectValue() != null && !this.getObjectValue().get(funcName).isnil()) {
            if (args.length > 0) {
                return this.getObjectValue().get(funcName).invoke(LuaValue.varargsOf(this.getObjectValue(), LuaValue.varargsOf(args)));
            } else {
                return this.getObjectValue().get(funcName).invoke(this.getObjectValue());
            }
        }

        return null;
    }
}
