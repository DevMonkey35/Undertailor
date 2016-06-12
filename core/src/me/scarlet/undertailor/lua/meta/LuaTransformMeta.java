package me.scarlet.undertailor.lua.meta;

import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static org.luaj.vm2.LuaValue.NIL;
import static org.luaj.vm2.LuaValue.valueOf;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.gfx.Transform;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

public class LuaTransformMeta implements LuaObjectMeta {

    static LuaObjectValue<Transform> convert(LuaValue value) {
        return Lua.checkType(value, LuaTransformMeta.class);
    }

    static Transform obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaTransformMeta() {
        this.metatable = new LuaTable();

        // ---------------- getters ----------------

        // transform:getScaleX()
        set("getScaleX", asFunction(vargs -> {
            return valueOf(obj(vargs).getScaleX());
        }));

        // transform:getScaleY()
        set("getScaleY", asFunction(vargs -> {
            return valueOf(obj(vargs).getScaleY());
        }));

        // transform:getFlipX()
        set("getFlipX", asFunction(vargs -> {
            return valueOf(obj(vargs).getFlipX());
        }));

        // transform:getFlipY()
        set("getFlipY", asFunction(vargs -> {
            return valueOf(obj(vargs).getFlipY());
        }));

        // transform:getRotation()
        set("getRotation", asFunction(vargs -> {
            return valueOf(obj(vargs).getRotation());
        }));

        // ---------------- setters ----------------

        // transform:setScaleX(scaleX)
        set("setScaleX", asFunction(vargs -> {
            obj(vargs).setScaleX(vargs.checknumber(2).tofloat());
            return NIL;
        }));

        // transform:setScaleY(scaleY)
        set("setScaleY", asFunction(vargs -> {
            obj(vargs).setScaleY(vargs.checknumber(2).tofloat());
            return NIL;
        }));

        // transform:setFlipX(flipX)
        set("setFlipX", asFunction(vargs -> {
            obj(vargs).setFlipX(vargs.checkboolean(2));
            return NIL;
        }));

        // transform:setFlipY(flipY)
        set("setFlipY", asFunction(vargs -> {
            obj(vargs).setFlipY(vargs.checkboolean(2));
            return NIL;
        }));

        // transform:setRotation(rotation)
        set("setRotation", asFunction(vargs -> {
            obj(vargs).setRotation(vargs.checknumber(2).tofloat());
            return NIL;
        }));
}

    @Override
    public Class<?> getTargetObjectClass() {
        return Transform.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "transform";
    }
}
