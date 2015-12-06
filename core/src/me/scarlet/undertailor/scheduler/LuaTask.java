package me.scarlet.undertailor.scheduler;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaInputData;
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
        LuaValue returned = taskImpl.get(IMPLMETHOD_PROCESS).call(taskImpl, LuaValue.valueOf(delta), new LuaInputData(input));
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
