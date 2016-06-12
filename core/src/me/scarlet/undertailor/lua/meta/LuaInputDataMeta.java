package me.scarlet.undertailor.lua.meta;

import static me.scarlet.undertailor.lua.LuaObjectValue.of;
import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static org.luaj.vm2.LuaValue.NIL;
import static org.luaj.vm2.LuaValue.valueOf;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.input.InputData;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

public class LuaInputDataMeta implements LuaObjectMeta {

    static LuaObjectValue<InputData> convert(LuaValue value) {
        return Lua.checkType(value, LuaInputDataMeta.class);
    }

    static InputData obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaInputDataMeta() {
        this.metatable = new LuaTable();

        // input:getPressData(key)
        set("getPressData", asFunction(vargs -> {
            return of(obj(vargs).getPressData(vargs.checkint(2)));
        }));

        // input:isConsumed()
        set("isConsumed", asFunction(vargs -> {
            return valueOf(obj(vargs).isConsumed());
        }));

        // input:consume()
        set("consume", asFunction(vargs -> {
            obj(vargs).consume();
            return NIL;
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return InputData.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "inputdata";
    }
}
