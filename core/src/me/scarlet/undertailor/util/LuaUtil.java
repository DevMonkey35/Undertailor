package me.scarlet.undertailor.util;

import me.scarlet.undertailor.exception.LuaScriptException;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class LuaUtil {
    
    /**
     * Iterate through the values of a {@link LuaTable}.
     * 
     * <p>The {@link Varargs} given to the consumer contains the current
     * key/value pair being processed, with the key as the first argument
     * (Varargs.arg(1)) and the value as the second (Varargs.arg(2)).</p>
     * 
     * @param table the LuaTable to iterate through
     * @param consumer the Consumer that processes the key/value pairs
     */
    public static void iterateTable(LuaTable table, Consumer<Varargs> consumer) {
        LuaValue key = LuaValue.NIL;
        while(true) {
            Varargs pair = table.next(key);
            if(pair.isnil(1)) {
                break;
            }
            
            consumer.accept(pair);
            key = pair.arg1();
        }
    }
    
    public static Map<String, LuaFunction> checkImplementation(LuaValue table, String[] requiredMethods, String[] optionalMethods) throws LuaScriptException {
        Map<String, LuaFunction> functions = new HashMap<>();
        if(requiredMethods != null) {
            for(String method : requiredMethods) {
                if(table.get(method).isnil() || !table.get(method).isfunction()) {
                    throw new LuaScriptException("failed to implement required method " + method);
                }
                
                functions.put(method, table.get(method).checkfunction());
            }
        }
        
        if(optionalMethods != null) {
            for(String method : optionalMethods) {
                if(table.get(method).isfunction()) {
                    functions.put(method, table.get(method).checkfunction());
                }
            }
        }
        
        return functions;
    }
}
