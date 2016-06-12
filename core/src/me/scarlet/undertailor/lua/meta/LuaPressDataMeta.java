package me.scarlet.undertailor.lua.meta;

import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static org.luaj.vm2.LuaValue.valueOf;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.input.PressData;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

public class LuaPressDataMeta implements LuaObjectMeta {

    static LuaObjectValue<PressData> convert(LuaValue value) {
        return Lua.checkType(value, LuaPressDataMeta.class);
    }

    static PressData obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaPressDataMeta() {
        this.metatable = new LuaTable();

        // pressData:getLastReleaseTime()
        set("getLastReleaseTime", asFunction(vargs -> {
            return valueOf(obj(vargs).getLastReleaseTime());
        }));

        // pressData:justReleased(time)
        set("justReleased", asFunction(vargs -> {
            return valueOf(obj(vargs).justReleased(vargs.checklong(2)));
        }));

        // pressData:getLastPressTime()
        set("getLastPressTime", asFunction(vargs -> {
            return valueOf(obj(vargs).getLastPressTime());
        }));

        // pressData:justPressed(time)
        set("justPressed", asFunction(vargs -> {
            return valueOf(obj(vargs).justPressed(vargs.checklong(2)));
        }));

        // pressData:getHoldTime()
        set("getHoldTime", asFunction(vargs -> {
            return valueOf(obj(vargs).getHoldTime());
        }));

        // pressData:isPressed()
        set("isPressed", asFunction(vargs -> {
            return valueOf(obj(vargs).isPressed());
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return PressData.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "pressdata";
    }
}
