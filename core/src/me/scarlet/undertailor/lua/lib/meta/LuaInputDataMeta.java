package me.scarlet.undertailor.lua.lib.meta;

import me.scarlet.undertailor.lua.LuaInputData;
import me.scarlet.undertailor.lua.LuaPressData;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class LuaInputDataMeta extends LuaTable {
    
    public static void prepareMetatable() {
        if(LuaInputData.METATABLE == null) {
            LuaInputData.METATABLE = LuaValue.tableOf(new LuaValue[] {INDEX, new LuaInputDataMeta()});
        }
    }

    public LuaInputDataMeta() {
        this.set("getPressData", new _getPressData());
        this.set("isConsumed", new _isConsumed());
        this.set("consume", new _consume());
    }
    
    static class _getPressData extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            InputData data = LuaInputData.checkInputData(arg1).getData();
            return new LuaPressData(data.getPressData(arg2.checkint()));
        }
    }
    
    static class _isConsumed extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            InputData data = LuaInputData.checkInputData(arg1).getData();
            return LuaValue.valueOf(data.isConsumed());
        }
    }
    
    static class _consume extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            InputData data = LuaInputData.checkInputData(arg1).getData();
            data.consume();
            return LuaValue.NIL;
        }
    }
}
