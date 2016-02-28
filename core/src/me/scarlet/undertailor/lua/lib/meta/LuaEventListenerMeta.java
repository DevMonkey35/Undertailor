package me.scarlet.undertailor.lua.lib.meta;

import me.scarlet.undertailor.environment.event.EventListener;
import me.scarlet.undertailor.environment.event.EventReceiver;
import me.scarlet.undertailor.environment.event.LuaEventReceiver;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaEventListenerMeta extends LuaLibrary {
    
    @SuppressWarnings("unchecked")
    public static LuaObjectValue<? extends EventListener> check(LuaValue value) {
        LuaObjectValue<?> returned = null;
        if(value instanceof LuaObjectValue && ((LuaObjectValue<?>) value).getObject() instanceof EventListener) {
            returned = (LuaObjectValue<?>) value;
        }
        
        if(returned == null) {
            returned = LuaUtil.checkType(value,
                    Lua.TYPENAME_ENVIRONMENT,
                    Lua.TYPENAME_WORLDROOM);
        }
        
        return (LuaObjectValue<? extends EventListener>) returned;
    }
    
    public static LuaLibraryComponent[] COMPONENTS = {
            new addListener(),
            new removeListener(),
            new getListener(),
            new clearListeners()
    };
    
    public LuaEventListenerMeta() {
        super(null, COMPONENTS);
    }
    
    static class addListener extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 3, 3);
            
            EventListener listener = check(args.arg1()).getObject();
            String listenerId = args.checkjstring(2);
            LuaTable impl = args.checktable(3);
            
            if(!impl.get("pushEvent").isfunction()) {
                throw new LuaError("listener implementation must contain a pushEvent function");
            }
            
            listener.addListener(listenerId, new LuaEventReceiver(impl));
            return LuaValue.NIL;
        }
    }
    
    static class removeListener extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            EventListener listener = check(args.arg1()).getObject();
            String listenerId = args.checkjstring(2);
            
            listener.removeListener(listenerId);
            return LuaValue.NIL;
        }
    }
    
    static class getListener extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            EventListener listener = check(args.arg1()).getObject();
            String listenerId = args.checkjstring(2);
            
            EventReceiver receiver = listener.getListener(listenerId);
            if(receiver == null || !(receiver instanceof LuaEventReceiver)) {
                return LuaValue.NIL;
            }
            
            return ((LuaEventReceiver) receiver).asLuaTable();
        }
    }
    
    static class clearListeners extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            EventListener listener = check(args.arg1()).getObject();
            listener.clearListeners();
            return LuaValue.NIL;
        }
    }
}
