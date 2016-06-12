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

package me.scarlet.undertailor.lua.meta;

import static me.scarlet.undertailor.lua.LuaObjectValue.of;
import static me.scarlet.undertailor.lua.LuaObjectValue.orNil;
import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static org.luaj.vm2.LuaValue.valueOf;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.gfx.text.TextComponent;
import me.scarlet.undertailor.gfx.text.TextStyle;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;

import java.util.List;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link TextComponent} objects.
 */
public class LuaTextComponentMeta implements LuaObjectMeta {

    static LuaObjectValue<TextComponent> convert(LuaValue value) {
        return Lua.checkType(value, LuaTextComponentMeta.class);
    }

    static TextComponent obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaTextComponentMeta() {
        this.metatable = new LuaTable();

        // textComponent:getText()
        set("getText", asFunction(vargs -> {
            return valueOf(obj(vargs).getText());
        }));

        // textComponent:getFont()
        set("getFont", asFunction(vargs -> {
            return orNil(obj(vargs).getFont());
        }));

        // textComponent:getColor()
        set("getColor", asFunction(vargs -> {
            return orNil(obj(vargs).getColor());
        }));

        // textComponent:getSound()
        set("getSound", asFunction(vargs -> {
            return orNil(obj(vargs).getSound());
        }));

        // textComponent:getStyles()
        set("getStyles", asFunction(vargs -> {
            List<TextStyle> styles = obj(vargs).getStyles();
            LuaValue[] returned = new LuaValue[styles.size()];
            for (int i = 0; i < styles.size(); i++) {
                returned[i] = of(styles.get(i));
            }

            return LuaUtil.arrayOf(returned);
        }));

        // textComponent:getTextSpeed()
        set("getTextSpeed", asFunction(vargs -> {
            return valueOf(obj(vargs).getTextSpeed());
        }));

        // textComponent:getSegmentSize()
        set("getSegmentSize", asFunction(vargs -> {
            return valueOf(obj(vargs).getSegmentSize());
        }));

        // textComponent:getDelay()
        set("getDelay", asFunction(vargs -> {
            return valueOf(obj(vargs).getDelay());
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return TextComponent.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "textcomponent";
    }
}
