package me.scarlet.undertailor.nlua;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public abstract class LuaImplementation extends LuaTable {
    
    public abstract LuaImplementable getImplementable();
    
    @Override
    public final int type() {
        return LuaValue.TVALUE;
    }
    
    @Override
    public final String typename() {
        return this.getImplementable().getTypeName();
    }
    
    @Override
    public void rawset(LuaValue key, LuaValue value) {
        if(value != LuaValue.NIL) {
            if(key.isstring()) {
                for(String method : this.getImplementable().getFunctions()) {
                    if(key.tojstring().equals(method)) {
                        if(!value.isnil() && !value.isfunction()) {
                            throw new LuaError("cannot set variable of the name " + key.tojstring() + " to a non-functional value other than nil (implemented method)");
                        }
                        
                        this.getImplementable().onFunctionChange(this, key.tojstring(), value);
                        return;
                    }
                }
            }
        }
        
        super.rawset(key, value);
    }
}
