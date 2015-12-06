package me.scarlet.undertailor.lua.lib.meta;

import me.scarlet.undertailor.lua.LuaPressData;
import me.scarlet.undertailor.util.InputRetriever.PressData;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

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
        this.set("justReleased", new _justReleased());
        this.set("justPressed", new _justPressed());
    }
    
    static class _justPressed extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            PressData data = LuaPressData.checkPressData(arg1).getData();
            float time = arg2.isnil() ? 0.1F : new Float(arg2.checkdouble());
            return LuaValue.valueOf(data.justPressed(time));
        }
    }
    
    static class _justReleased extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            PressData data = LuaPressData.checkPressData(arg1).getData();
            float time = arg2.isnil() ? 0.1F : new Float(arg2.checkdouble());
            return LuaValue.valueOf(data.justReleased(time));
        }
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
