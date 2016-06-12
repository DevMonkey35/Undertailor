package me.scarlet.undertailor.lua.lib.game;

import me.scarlet.undertailor.input.InputRetriever;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Control library accessible by Lua.
 * 
 * <p>Wraps around {@link InputRetriever}.</p>
 */
public class ControlLib extends LuaLibrary {

    public ControlLib(InputRetriever input) {
        super("control");

        registerFunction("getInput", vargs -> {
            return LuaObjectValue.of(input.getCurrentData());
        });
    }

}
