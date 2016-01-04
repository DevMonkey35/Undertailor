package me.scarlet.undertailor.lua.lib;

import com.badlogic.gdx.utils.TimeUtils;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.scheduler.LuaTask;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class SchedulerLib extends LuaLibrary {
    
    public SchedulerLib() {
        super("scheduler",
                new registerTask(),
                new cancelTask(),
                new millis(),
                new sinceMillis());
    }
    
    static class registerTask extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            LuaTable impl = args.checktable(1);
            boolean active = args.isnil(2) ? false : args.checkboolean(2);
            
            LuaTask task = new LuaTask(impl);
            return LuaValue.valueOf(Undertailor.getScheduler().registerTask(task, active));
        }
    }
    
    static class cancelTask extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            int id = args.checkint(1);
            
            Undertailor.getScheduler().cancelTask(id);
            return LuaValue.NIL;
        }
    }
    
    static class millis extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            
            return LuaValue.valueOf(TimeUtils.millis());
        }
    }
    
    static class sinceMillis extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            long millis = args.checklong(1);
            return LuaValue.valueOf(TimeUtils.timeSinceMillis(millis));
        }
    }
}
