package me.scarlet.undertailor.lua.lib;

import static me.scarlet.undertailor.lua.LuaObjectValue.of;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.text.Text;
import me.scarlet.undertailor.lua.LuaLibrary;

/**
 * Text library accessible by Lua.
 * 
 * <p>Wraps around {@link Text}.</p>
 */
public class TextLib extends LuaLibrary {

    public TextLib(Undertailor undertailor) {
        super("text");

        // text.of(baseParams, text)
        registerFunction("newText", vargs -> {
            return of(Text.of(undertailor, vargs.checkjstring(1), vargs.checkjstring(2)));
        });
    }

}
