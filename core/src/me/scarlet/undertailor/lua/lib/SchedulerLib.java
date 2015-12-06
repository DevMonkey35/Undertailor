package me.scarlet.undertailor.lua.lib;

import com.badlogic.gdx.utils.TimeUtils;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.scheduler.LuaTask;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class SchedulerLib extends TwoArgFunction {
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable scheduler = new LuaTable();
        
        scheduler.set("registerTask", new _registerTask());
        scheduler.set("cancelTask", new _cancelTask());
        scheduler.set("millis", new _millis());
        scheduler.set("sinceMillis", new _sinceMillis());
        
        env.set("scheduler", scheduler);
        return scheduler;
    }
    
    static class _registerTask extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            LuaTable impl = arg1.checktable();
            boolean active = arg2.isnil() ? false : arg2.checkboolean();
            
            LuaTask task = new LuaTask(impl);
            return LuaValue.valueOf(Undertailor.getScheduler().registerTask(task, active));
        }
    }
    
    static class _cancelTask extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            int id = arg1.checkint();
            
            Undertailor.getScheduler().cancelTask(id);
            return LuaValue.NIL;
        }
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
