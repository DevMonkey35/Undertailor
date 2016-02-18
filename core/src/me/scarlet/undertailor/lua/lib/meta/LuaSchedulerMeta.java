/* 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor.lua.lib.meta;

import me.scarlet.undertailor.environment.Scheduler;
import me.scarlet.undertailor.environment.scheduler.LuaTask;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.lib.game.EnvironmentLib;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaSchedulerMeta extends LuaLibrary {
    
    public static LuaObjectValue<Scheduler> create(Scheduler scheduler) {
        return LuaObjectValue.of(scheduler, Lua.TYPENAME_SCHEDULER, Lua.META_SCHEDULER);
    }
    
    public static LuaObjectValue<Scheduler> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_SCHEDULER);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new getOwningEnvironment(),
            new registerTask(),
            new cancelTask(),
            new hasTask(),
            new generateTask()
    }; 
    
    public LuaSchedulerMeta() {
        super(null, COMPONENTS);
    }
    
    static class getOwningEnvironment extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Scheduler scheduler = check(args.arg1()).getObject();
            return EnvironmentLib.create(scheduler.getEnvironment());
        }
    }
    
    static class registerTask extends LibraryFunction { // TODO add task creation for consistency with other creation methods
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 3);
            
            Scheduler scheduler = check(args.arg1()).getObject();
            LuaTable impl = args.checktable(2);
            boolean active = args.optboolean(3, false);
            
            LuaTask task = new LuaTask(impl);
            return LuaValue.valueOf(scheduler.registerTask(task, active));
        }
    }
    
    static class cancelTask extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            Scheduler scheduler = check(args.arg1()).getObject();
            int id = args.checkint(2);
            
            scheduler.cancelTask(id);
            return LuaValue.NIL;
        }
    }
    
    static class hasTask extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            Scheduler scheduler = check(args.arg1()).getObject();
            int id = args.checkint(2);
            
            return LuaValue.valueOf(scheduler.hasTask(id));
        }
    }
    
    static class generateTask extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 3, 4);
            
            Scheduler scheduler = check(args.arg1()).getObject();
            String name = args.optjstring(2, null);
            LuaFunction func = args.checkfunction(3);
            boolean active = args.optboolean(4, true);
            
            LuaTable compile = new LuaTable();
            if(args.narg() > 3) {
                LuaUtil.iterateTable((LuaTable) args.subargs(4), vargs -> {
                    compile.set(vargs.arg(1), vargs.arg(2));
                });
            }
            
            compile.set("name", name);
            compile.set("process", func);
            
            return LuaValue.valueOf(scheduler.registerTask(new LuaTask(compile), active));
        }
    }
}
