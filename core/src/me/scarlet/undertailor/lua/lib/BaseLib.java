package me.scarlet.undertailor.lua.lib;

import org.luaj.vm2.LuaValue;

import me.scarlet.undertailor.lua.LuaLibrary;

public class BaseLib extends LuaLibrary {

    private String scriptPath;
    public BaseLib() {
        super(null);

        this.registerFunction("require", vargs -> {
            return LuaValue.NIL;
        });
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }
}
