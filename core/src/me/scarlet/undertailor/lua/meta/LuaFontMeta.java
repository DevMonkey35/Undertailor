package me.scarlet.undertailor.lua.meta;

import static me.scarlet.undertailor.lua.LuaObjectValue.of;
import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static me.scarlet.undertailor.util.LuaUtil.varargsOf;
import static org.luaj.vm2.LuaValue.NIL;
import static org.luaj.vm2.LuaValue.valueOf;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.gfx.text.Font;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.Pair;

public class LuaFontMeta implements LuaObjectMeta {

    static LuaObjectValue<Font> convert(LuaValue value) {
        return Lua.checkType(value, LuaFontMeta.class);
    }

    static Font obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaFontMeta() {
        this.metatable = new LuaTable();

        // font:getLineSize()
        set("getLineSize", asFunction(vargs -> {
            return valueOf(obj(vargs).getLineSize());
        }));

        // font:getSpaceLength()
        set("getSpaceLength", asFunction(vargs -> {
            return valueOf(obj(vargs).getSpaceLength());
        }));

        // font:getLetterSpacing(char)
        set("getLetterSpacing", asFunction(vargs -> {
            String str = vargs.checkjstring(1);
            if (str.trim().length() <= 0) {
                return NIL;
            }

            Pair<Float> spacing = obj(vargs).getLetterSpacing(str.charAt(0));
            return varargsOf(valueOf(spacing.getFirst()), valueOf(spacing.getSecond()));
        }));

        // font:getCharacterList()
        set("getCharacterList", asFunction(vargs -> {
            return valueOf(obj(vargs).getCharacterList());
        }));

        // font:getCharacterSprite(char)
        set("getCharacterSprite", asFunction(vargs -> {
            String str = vargs.checkjstring(1);
            if (str.trim().length() <= 0) {
                return NIL;
            }

            return of(obj(vargs).getCharacterSprite(str.charAt(0)));
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return Font.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "font";
    }
}
