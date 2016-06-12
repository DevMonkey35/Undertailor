package me.scarlet.undertailor.lua.meta;

import static me.scarlet.undertailor.lua.LuaObjectValue.of;
import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static me.scarlet.undertailor.util.LuaUtil.varargsOf;
import static org.luaj.vm2.LuaValue.NIL;
import static org.luaj.vm2.LuaValue.valueOf;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.gfx.text.Text;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.Pair;

public class LuaTextMeta implements LuaObjectMeta {

    static LuaObjectValue<Text> convert(LuaValue value) {
        return Lua.checkType(value, LuaTextMeta.class);
    }

    static Text obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaTextMeta() {
        this.metatable = new LuaTable();

        // ---------------- getters: immutable/calculated ----------------

        // text:getBoundedText()
        set("getBoundedText", asFunction(vargs -> {
            return valueOf(obj(vargs).getBoundedText());
        }));

        // text:getComponents()
        set("getComponents", asFunction(vargs -> {
            Object[] components = obj(vargs).getComponents().toArray();
            LuaValue[] returned = new LuaValue[components.length];
            for (int i = 0; i < returned.length; i++) {
                returned[i] = of(components[i]);
            }

            return varargsOf(returned);
        }));

        // text:getSpaceTaken()
        set("getSpaceTaken", asFunction(vargs -> {
            Pair<Float> space = obj(vargs).getSpaceTaken();
            return varargsOf(valueOf(space.getFirst()), valueOf(space.getSecond()));
        }));

        // ---------------- g/s text parameters ----------------

        // text:getStringBounds()
        set("getStringBounds", asFunction(vargs -> {
            Pair<Integer> bounds = obj(vargs).getStringBounds();
            return varargsOf(valueOf(bounds.getFirst()), valueOf(bounds.getSecond()));
        }));

        // text:setStringBounds(iFirst, iSecond)
        // tries to match Lua's 1-based stuff by negating 1 from the first bound
        set("setStringBounds", asFunction(vargs -> {
            obj(vargs).setStringBounds(vargs.checkint(2) - 1, vargs.checkint(3));
            return NIL;
        }));

        // ---------------- functional --------------------

        // text:getTextComponentAt(iIndex)
        // tries to match Lua's 1-based stuff by negating 1 from the provided index
        set("getTextComponentAt", asFunction(vargs -> {
            return of(obj(vargs).getTextComponentAt(vargs.checkint(2) - 1));
        }));

        // text:substring(iFirst, iSecond)
        // tries to match Lua's 1-based stuff by negating 1 from the first bound
        set("substring", asFunction(vargs -> {
            return of(obj(vargs).substring(vargs.checkint(2) - 1, vargs.checkint(3)));
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return Text.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "text";
    }

}
