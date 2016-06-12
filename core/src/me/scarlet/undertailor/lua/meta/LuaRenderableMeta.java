package me.scarlet.undertailor.lua.meta;

import static me.scarlet.undertailor.lua.LuaObjectValue.of;
import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static org.luaj.vm2.LuaValue.NIL;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

public class LuaRenderableMeta implements LuaObjectMeta {

    static LuaObjectValue<Renderable> convert(LuaValue value) {
        return Lua.checkType(value, LuaRenderableMeta.class);
    }

    static Renderable obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaRenderableMeta() {
        this.metatable = new LuaTable();

        // renderable:getTransform()
        set("getTransform", asFunction(vargs -> {
            return of(obj(vargs).getTransform());
        }));

        // renderable:setTransform(transform)
        set("setTransform", asFunction(vargs -> {
            obj(vargs).setTransform(
                Lua.<Transform>checkType(vargs.checknotnil(2), LuaTransformMeta.class).getObject());
            return NIL;
        }));

        // renderable:draw(x, y[, transform])
        set("draw", asFunction(vargs -> {
            float x = vargs.checknumber(2).tofloat();
            float y = vargs.checknumber(3).tofloat();
            Transform transform = vargs.isnil(4) ? null
                : Lua.<Transform>checkType(vargs.arg(4), LuaTransformMeta.class).getObject();
            if (transform == null) {
                obj(vargs).draw(x, y);
            } else {
                obj(vargs).draw(x, y, transform);
            }

            return NIL;
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return Renderable.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "renderable";
    }
}
