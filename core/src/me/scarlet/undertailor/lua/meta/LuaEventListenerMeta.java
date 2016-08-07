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

import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static org.luaj.vm2.LuaValue.NIL;
import static org.luaj.vm2.LuaValue.valueOf;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.engine.events.Event;
import me.scarlet.undertailor.engine.events.EventListener;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link EventListener} objects.
 */
public class LuaEventListenerMeta implements LuaObjectMeta {

    public static LuaObjectValue<EventListener> convert(LuaValue value) {
        return Lua.checkType(value, LuaEventListenerMeta.class);
    }

    static EventListener obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaEventListenerMeta() {
        this.metatable = new LuaTable();

        // eventListener:callEvent(event)
        set("callEvent", asFunction(vargs -> {
            Event event = Event.asLuaEvent(vargs.checktable(2).unpack());
            return valueOf(obj(vargs).callEvent(event));
        }));

        // eventListener:onEvent(eventId, handler)
        set("onEvent", asFunction(vargs -> {
            obj(vargs).getEventHelper().registerHandler(vargs.checkjstring(2), event -> {
                Varargs params = LuaUtil.unpack(event.asLua(), 3 + event.getParameters().length);
                return vargs.checkfunction(3).invoke(params.subargs(2)).toboolean(1);
            });

            return NIL;
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return EventListener.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "eventlistener";
    }
}
