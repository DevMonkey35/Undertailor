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

package me.scarlet.undertailor.util;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import me.scarlet.undertailor.lua.LuaObjectValue;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class for interaction with lua-based objects.
 */
public class LuaUtil {

    static final Set<Object> MISC_SET;

    static {
        MISC_SET = new HashSet<>();
    }

    /**
     * Iterates through a given {@link LuaTable}, passing
     * each key/value pair as {@link Varargs} to the
     * provided {@link Consumer}.
     * 
     * <p>Assuming the table is used as a table (as in, not
     * an array), the first arg within the Varargs
     * (<code>varargs.arg(1)</code>) is the key. The second
     * (<code>varargs.arg(2)</code>) is the value. If the
     * table is an array, then the first arg refers to the
     * value, while the second remains nil.</p>
     * 
     * @param table the table to iterate through
     * @param consumer the consumer processing each table
     *        entry
     */
    public static void iterateTable(LuaTable table, Consumer<Varargs> consumer) {
        LuaValue key = LuaValue.NIL;
        while (true) {
            Varargs pair = table.next(key);
            if (pair.isnil(1)) {
                break;
            }

            consumer.accept(pair);
            key = pair.arg1();
        }
    }

    /**
     * Generates a new anonymously-typed {@link LuaFunction}
     * object from the provided {@link Function}.
     * 
     * @param func the function processing the Lua function
     *        call
     * 
     * @return a LuaFunction wrapping the call to the
     *         provided function
     */
    public static LuaFunction asFunction(Function<Varargs, Varargs> func) {
        return new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return func.apply(args);
            }
        };
    }

    /**
     * Returns the count of entries within the provided
     * {@link LuaTable}.
     * 
     * @param table the table to count entries for
     * 
     * @return how many entries exist within the given table
     */ // because y'kno, all the length methods for LuaTable assumed integer keys
    public static int getTableSize(LuaTable table) {
        MISC_SET.clear();
        iterateTable(table, MISC_SET::add);

        return MISC_SET.size();
    }

    /**
     * Generates an array of values from the provided set of
     * {@link LuaValue}s.
     * 
     * <p>That is, each value provided is a new entry
     * assigned an integer key, the key being the index it
     * was listed in.</p>
     * 
     * @param values the values of the array
     * 
     * @return the {@link LuaTable} containing the value
     *         array
     */
    public static LuaTable arrayOf(LuaValue... values) {
        LuaTable array = new LuaTable();
        for (int i = 0; i < values.length; i++) {
            array.set(i + 1, values[i]);
        }

        return array;
    }

    /**
     * Returns a {@link Varargs} instance containing the
     * provided {@link LuaValue}s in their given order.
     * 
     * <p>Convenience method replacing
     * {@link LuaValue#varargsOf(LuaValue[])} so we can
     * actually make varargs <strong>with</strong>
     * varargs.</p>
     * 
     * @param values the values to contain within the
     *        Varargs to generate
     * 
     * @return a Varargs instance
     */
    public static Varargs varargsOf(LuaValue... values) {
        return LuaValue.varargsOf(values);
    }

    /**
     * Generates a {@link Varargs} instance, converting the
     * provided Objects into their LuaValue/LuaObjectValue
     * forms packaged into the former.
     * 
     * @param values the Objects to convert to LuaValues
     * 
     * @return the provided Objects as Varargs
     */
    public static Varargs varargsOf(Object... values) {
        LuaValue[] vargs = new LuaValue[values.length];
        for (int i = 0; i < values.length; i++) {
            Object obj = values[i];
            if (obj == null) {
                vargs[i] = LuaValue.NIL;
                continue;
            }

            if (obj instanceof LuaValue) {
                vargs[i] = (LuaValue) obj;
            } else if (NumberUtil.isNumber(obj)) {
                vargs[i] = LuaValue.valueOf((double) obj);
            } else if (Boolean.class.isInstance(obj)) {
                vargs[i] = LuaValue.valueOf((boolean) obj);
            } else if (String.class.isInstance(obj)) {
                vargs[i] = LuaValue.valueOf(obj.toString());
            } else {
                vargs[i] = LuaObjectValue.of(obj);
            }
        }

        return varargsOf(vargs);
    }

    /**
     * Generates an Object array containing the Java
     * versions of the values within the provided
     * {@link Varargs}.
     * 
     * <p>Note that tables are not converted.</p>
     * 
     * @param vargs the Varargs to process
     * 
     * @return a new Object array containing Java variants
     *         of the Varargs' contained values
     */
    public static Object[] asJavaVargs(Varargs vargs) {
        Object[] obj = new Object[vargs.narg()];
        for (int i = 0; i < obj.length; i++) {
            LuaValue arg = vargs.arg(i + 1);
            if (arg instanceof LuaObjectValue) {
                obj[i] = ((LuaObjectValue<?>) arg).getObject();
            } else {
                if (arg.isnumber()) {
                    obj[i] = arg.todouble();
                } else if (arg.isboolean()) {
                    obj[i] = arg.toboolean();
                } else if (arg.istable()) {
                    obj[i] = arg;
                } else {
                    obj[i] = arg.tojstring();
                }
            }
        }

        return obj;
    }
}
