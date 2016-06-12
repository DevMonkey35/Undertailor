package me.scarlet.undertailor.lua.meta;

import static me.scarlet.undertailor.lua.LuaObjectValue.of;
import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static org.luaj.vm2.LuaValue.valueOf;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.gfx.text.TextComponent;
import me.scarlet.undertailor.gfx.text.TextStyle;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;

import java.util.List;

public class LuaTextComponentMeta implements LuaObjectMeta {

    static LuaObjectValue<TextComponent> convert(LuaValue value) {
        return Lua.checkType(value, LuaTextComponentMeta.class);
    }

    static TextComponent obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaTextComponentMeta() {
        this.metatable = new LuaTable();

        // textComponent:getText()
        set("getText", asFunction(vargs -> {
            return valueOf(obj(vargs).getText());
        }));

        // textComponent:getFont()
        set("getFont", asFunction(vargs -> {
            return of(obj(vargs).getFont());
        }));

        // textComponent:getColor()
        set("getColor", asFunction(vargs -> {
            return of(obj(vargs).getColor());
        }));

        // textComponent:getSound()
        set("getSound", asFunction(vargs -> {
            return of(obj(vargs).getSound());
        }));

        // textComponent:getStyles()
        set("getStyles", asFunction(vargs -> {
            List<TextStyle> styles = obj(vargs).getStyles();
            LuaValue[] returned = new LuaValue[styles.size()];
            for (int i = 0; i < styles.size(); i++) {
                returned[i] = of(styles.get(i));
            }

            return LuaUtil.varargsOf(returned);
        }));

        // textComponent:getTextSpeed()
        set("getTextSpeed", asFunction(vargs -> {
            return valueOf(obj(vargs).getTextSpeed());
        }));

        // textComponent:getSegmentSize()
        set("getSegmentSize", asFunction(vargs -> {
            return valueOf(obj(vargs).getSegmentSize());
        }));

        // textComponent:getDelay()
        set("getDelay", asFunction(vargs -> {
            return valueOf(obj(vargs).getDelay());
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return TextComponent.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "textcomponent";
    }
}
