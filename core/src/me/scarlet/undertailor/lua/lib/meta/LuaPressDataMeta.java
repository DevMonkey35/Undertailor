package me.scarlet.undertailor.lua.lib.meta;

import me.scarlet.undertailor.lua.LuaPressData;
import me.scarlet.undertailor.util.InputRetriever.PressData;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class LuaPressDataMeta extends LuaTable {
    
    public static void prepareMetatable() {
        if(LuaPressData.METATABLE == null) {
            LuaPressData.METATABLE = LuaValue.tableOf(new LuaValue[] {INDEX, new LuaPressDataMeta()});
        }
    }

    public LuaPressDataMeta() {
        this.set("isPressed", new _isPressed());
        this.set("getHoldTime", new _getHoldTime());
        this.set("getLastPressTime", new _getLastPressTime());
        this.set("getLastReleaseTime", new _getLastReleaseTime());
    }
    
    static class _isPressed extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            PressData data = LuaPressData.checkPressData(arg).getData();
            return LuaValue.valueOf(data.isPressed());
        }
    }
    
    static class _getHoldTime extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            PressData data = LuaPressData.checkPressData(arg).getData();
            return LuaValue.valueOf(data.getHoldTime());
        }
    }
    
    static class _getLastPressTime extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            PressData data = LuaPressData.checkPressData(arg).getData();
            return LuaValue.valueOf(data.getLastPressTime());
        }
    }
    
    static class _getLastReleaseTime extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            PressData data = LuaPressData.checkPressData(arg).getData();
            return LuaValue.valueOf(data.getLastReleaseTime());
        }
    }
}
