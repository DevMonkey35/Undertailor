package me.scarlet.undertailor.environment.event;

import org.luaj.vm2.LuaTable;

public class LuaEventData implements EventData {
    
    private LuaTable impl;
    public LuaEventData(LuaTable impl) {
        this.impl = impl;
        
        if(impl.get("name").isnil()) {
            impl.set("name", "generic");
        } else {
            impl.get("name").checkjstring();
        }
    }
    
    @Override
    public String getName() {
        return impl.get("name").checkjstring();
    }

    @Override
    public Object get(String key) {
        return impl.get(key);
    }
    
    @Override
    public LuaTable asLuaTable() {
        return impl;
    }
    
}
