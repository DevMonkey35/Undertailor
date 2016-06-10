package me.scarlet.undertailor.lua;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public interface LuaImplementable<T> {

    LuaObjectValue<T> getObjectValue();

    void setObjectValue(LuaObjectValue<T> value);
    
    default Class<?> getPrimaryIdentifyingClass() {
        return null;
    }

    default Varargs invoke(String funcName, LuaValue... args) {
        if (this.getObjectValue() != null && !this.getObjectValue().get(funcName).isnil()) {
            if (args.length > 0) {
                return this.getObjectValue().get(funcName).invoke(LuaValue.varargsOf(args));
            } else {
                return this.getObjectValue().get(funcName).invoke();
            }
        }

        return null;
    }

    default Varargs invokeSelf(String funcName, LuaValue... args) {
        if (this.getObjectValue() != null && !this.getObjectValue().get(funcName).isnil()) {
            if (args.length > 0) {
                return this.getObjectValue().get(funcName).invoke(LuaValue.varargsOf(this.getObjectValue(), LuaValue.varargsOf(args)));
            } else {
                return this.getObjectValue().get(funcName).invoke(this.getObjectValue());
            }
        }

        return null;
    }
}
