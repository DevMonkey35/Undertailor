package me.scarlet.undertailor.lua.lib;

import org.luaj.vm2.LuaValue;

import me.scarlet.undertailor.lua.LuaLibrary;

public class BaseLib extends LuaLibrary {

    private static String scriptPath;

    public static void setScriptPath(String scriptPath) {
        BaseLib.scriptPath = scriptPath;
        if (!BaseLib.scriptPath.endsWith("/"))
            BaseLib.scriptPath += "/";
    }
    
    public BaseLib() {
        super(null);

        this.registerFunction("require", vargs -> {
            return LuaValue.NIL;
        });
    }
}
