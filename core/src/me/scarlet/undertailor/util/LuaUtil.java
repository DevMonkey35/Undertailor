package me.scarlet.undertailor.util;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.function.Consumer;

public class LuaUtil {

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

    public static Varargs varargsOf(LuaValue... values) {
        return LuaValue.varargsOf(values);
    }
}
