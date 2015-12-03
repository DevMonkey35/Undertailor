package me.scarlet.undertailor.lua.lib;

import com.badlogic.gdx.utils.TimeUtils;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class TimeLib extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        LuaTable time = new LuaTable();
        time.set("millis", new _millis());
        time.set("sinceMillis", new _sinceMillis());
        
        return time;
    }
    
    static class _millis extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaValue.valueOf(TimeUtils.millis());
        }
    }
    
    static class _sinceMillis extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            long millis = arg.checklong();
            return LuaValue.valueOf(TimeUtils.timeSinceMillis(millis));
        }
    }
}
