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

package me.scarlet.undertailor.scheduler;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.lib.meta.LuaInputDataMeta;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class LuaTask implements Task {
    
    public static final String VAR_NAME = "name";
    public static final String IMPLMETHOD_PROCESS = "process";   // process(table, delta, input)
    public static final String IMPLMETHOD_ONFINISH = "onFinish"; // onFinish(bool)
    
    private String name;
    private LuaTable taskImpl;
    public LuaTask(LuaTable taskImpl) {
        this.taskImpl = taskImpl;
        if(!taskImpl.get(IMPLMETHOD_PROCESS).isfunction()) {
            throw new LuaError("task impl does not have a process method");
        }
        
        this.name = null;
        if(taskImpl.get(VAR_NAME).isstring()) {
            this.name = taskImpl.get(VAR_NAME).checkjstring();
        }
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public boolean process(float delta, InputData input) {
        LuaValue returned = taskImpl.get(IMPLMETHOD_PROCESS).call(taskImpl, LuaValue.valueOf(delta), LuaInputDataMeta.create(input));
        if(!returned.isboolean()) {
            Undertailor.instance.warn(Scheduler.MANAGER_TAG, "lua task was preemptively ended; task implementation returned invalid value (expected boolean)");
            return false;
        }
        
        return returned.checkboolean(); 
    }

    @Override
    public void onFinish(boolean forced) {
        if(taskImpl.get(IMPLMETHOD_ONFINISH).isfunction()) {
            taskImpl.get(IMPLMETHOD_ONFINISH).call(LuaValue.valueOf(forced));
        }
    }
}
