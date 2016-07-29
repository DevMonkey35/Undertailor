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

package me.scarlet.undertailor.engine.scheduler;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import me.scarlet.undertailor.engine.Processable;
import me.scarlet.undertailor.util.LuaUtil;

/**
 * Functional class used to perform a task to run across
 * several frames of a running game instance.
 */
public interface Task extends Processable {

    public static final String FIELD_NAME = "name";

    public static final String FUNC_PROCESS = "process";
    public static final String FUNC_ONFINISH = "onFinish";

    /**
     * Generates a {@link Task} based on the contents of the
     * provided Lua value.
     * 
     * <p>The value may be one of two types, either a table
     * (full implementation) or a function (process function
     * only).</p>
     * 
     * <p>If the value is a function, the task returned
     * contains nothing but the Lua script assigned to run
     * when this task is executed.</p>
     * 
     * <p>If the value is a table, then the task returned is
     * a full implementation based on the properties of the
     * table. The following keys are read from the
     * table.</p>
     * 
     * <pre>
     * "name": {@link #getName()},
     * "process": {@link #process(Object...)},
     * "onFinish": {@link #onFinish(boolean)}
     * </pre>
     * 
     * <p>The two functions of the tasks gain parameters
     * based on whether or not the provided value was a
     * function or a table. If a function, the functions
     * gain no extra values, otherwise the first argument
     * for both functions is always the table implementing
     * the task.</p>
     * 
     * @param value the value containing the task properties
     * 
     * @return a Lua-made Task
     */
    public static Task asLuaTask(LuaValue value) {
        if(value.isfunction()) {
            return (params) -> {
                return value.invoke(LuaUtil.varargsOf(params)).toboolean(1);
            };
        } else if(value.istable()) {
            LuaTable table = value.checktable();
            return new Task() {
                @Override
                public String getName() {
                    return table.get(FIELD_NAME).optjstring(null);
                }

                @Override
                public boolean process(Object... params) {
                    return table.get(FUNC_PROCESS).checkfunction().invoke(table, LuaUtil.varargsOf(params)).toboolean(1);
                }

                @Override
                public void onFinish(boolean forced) {
                    LuaFunction func = table.get(FUNC_ONFINISH).optfunction(null);
                    if(func != null) {
                        func.invoke(table, LuaValue.valueOf(forced));
                    }
                }
            };
        } else {
            throw new LuaError("bad argument: expected function or table, got " + value.typename());
        }
    }

    /**
     * Returns the name of this {@link Task}.
     * 
     * @return the name of this Task
     */
    default String getName() {
        return null;
    };

    /**
     * Called when this {@link Task} finishes (when
     * {@link #process(Object...)} returns false).
     * 
     * @param forced if the task was ended preemptively by
     *        means of an error or a scheduler call
     */
    default void onFinish(boolean forced) {}

    /**
     * Processes this {@link Task} for the current frame.
     * 
     * <p>The generic return value resolves to whether or
     * not this Task should keep running after this method
     * has been called. If false is returned, the Task is
     * removed from the host scheduler.</p>
     * 
     * @return if the Task should keep running after this
     *         frame
     */
    @Override
    boolean process(Object... params);
}
