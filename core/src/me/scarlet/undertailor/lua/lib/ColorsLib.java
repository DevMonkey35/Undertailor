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

package me.scarlet.undertailor.lua.lib;

import static me.scarlet.undertailor.lua.LuaObjectValue.of;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import me.scarlet.undertailor.lua.LuaLibrary;

/**
 * Colors library accessible by Lua.
 * 
 * <p>Wraps around {@link Color}.</p>
 */
public class ColorsLib extends LuaLibrary {

    public ColorsLib() {
        super("colors");

        registerFunction("fromHex", vargs -> {
            return of(Color.valueOf(vargs.checkjstring(1)));
        });

        registerFunction("fromRGB", vargs -> {
            int r = vargs.optint(1, 0);
            int g = vargs.optint(2, 0);
            int b = vargs.optint(3, 0);
            return of(new Color(r / 255F, g / 255F, b / 255F, 1F));
        });
    }

    @Override
    public void postinit(LuaTable table, LuaValue environment) {
        Colors.getColors().forEach(entry -> {
            table.set(entry.key.toLowerCase(), of(entry.value));
        });
    }
}
