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

import me.scarlet.undertailor.engine.scheduler.Scheduler;
import me.scarlet.undertailor.engine.scheduler.Task;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link Scheduler} objects.
 */
public class LuaSchedulerMeta implements LuaObjectMeta {

    static LuaObjectValue<Scheduler> convert(LuaValue value) {
        return Lua.checkType(value, LuaSchedulerMeta.class);
    }

    static Scheduler obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaSchedulerMeta() {
        this.metatable = new LuaTable();

        // scheduler:registerTask(task[, active])
        set("registerTask", asFunction(vargs -> {
            Task task = Task.asLuaTask(vargs.checknotnil(2));
            boolean active = vargs.optboolean(3, false);
            return valueOf(obj(vargs).registerTask(task, active));
        }));

        // scheduler:cancelTask(id)
        set("cancelTask", asFunction(vargs -> {
            long id = vargs.checklong(2);
            obj(vargs).cancelTask(id);
            return NIL;
        }));

        // scheduler:hasTask(id)
        set("hasTask", asFunction(vargs -> {
            long id = vargs.checklong(2);
            return valueOf(obj(vargs).hasTask(id));
        }));
    }

    @Override
    public boolean isPrimaryType() {
        return true;
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return Scheduler.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "scheduler";
    }
}
