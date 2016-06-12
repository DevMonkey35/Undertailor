package me.scarlet.undertailor.lua.lib;

import static me.scarlet.undertailor.lua.LuaObjectValue.of;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import me.scarlet.undertailor.lua.LuaLibrary;

public class ColorsLib extends LuaLibrary {

    public ColorsLib() {
        super("colors");

        registerFunction("fromHex", vargs -> {
            return of(Color.valueOf(vargs.checkjstring(1)));
        });

        registerFunction("fromRGB", vargs -> {
            int r = vargs.optint(1, 0);
            int g = vargs.optint(2, 0);
            int b = vargs.optint(3, 0);
            return of(new Color(r / 255F, g / 255F, b / 255F, 1F));
        });
    }

    @Override
    public void postinit(LuaTable table, LuaValue environment) {
        Colors.getColors().forEach(entry -> {
            table.set(entry.key.toLowerCase(), of(entry.value));
        });
    }
}
