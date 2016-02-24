package me.scarlet.undertailor.lua.lib.meta;

import me.scarlet.undertailor.environment.event.EventReceiver;
import me.scarlet.undertailor.environment.event.LuaEventData;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaEventReceiverMeta extends LuaLibrary {
    
    @SuppressWarnings("unchecked")
    public static LuaObjectValue<? extends EventReceiver> check(LuaValue value) {
        LuaObjectValue<?> returned = null;
        if(value instanceof LuaObjectValue && ((LuaObjectValue<?>) value).getObject() instanceof EventReceiver) {
            returned = (LuaObjectValue<?>) value;
        }
        
        if(returned == null) {
            returned = LuaUtil.checkType(value,
                    Lua.TYPENAME_ENVIRONMENT,
                    Lua.TYPENAME_OVERWORLDCONTROLLER,
                    Lua.TYPENAME_UICONTROLLER,
                    Lua.TYPENAME_WORLDROOM,
                    
                    Lua.TYPENAME_WORLDOBJECT,
                    Lua.TYPENAME_UIOBJECT,
                    Lua.TYPENAME_UICOMPONENT);
        }
        
        return (LuaObjectValue<? extends EventReceiver>) returned;
    }
    
    public static LuaLibraryComponent[] COMPONENTS = {
            new pushEvent()
    };
    
    public LuaEventReceiverMeta(String libName) {
        super(null, COMPONENTS);
    }
    
    static class pushEvent extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            EventReceiver receiver = check(args.arg1()).getObject();
            LuaTable data = args.checktable(2);
            
            receiver.pushEvent(new LuaEventData(data));
            return LuaValue.NIL;
        }
    }
}
