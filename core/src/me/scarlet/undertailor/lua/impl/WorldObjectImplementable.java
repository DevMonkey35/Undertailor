package me.scarlet.undertailor.lua.impl;

import me.scarlet.undertailor.collision.Collider;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.LuaImplementable;
import me.scarlet.undertailor.lua.LuaImplementation;
import me.scarlet.undertailor.lua.impl.WorldObjectImplementable.WorldObjectImplementation;
import me.scarlet.undertailor.lua.lib.meta.LuaEntrypointMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaInputDataMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaWorldObjectMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaWorldRoomMeta;
import me.scarlet.undertailor.overworld.WorldObject;
import me.scarlet.undertailor.overworld.WorldRoom;
import me.scarlet.undertailor.overworld.WorldRoom.Entrypoint;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WorldObjectImplementable implements LuaImplementable<File, WorldObjectImplementation> {
    
    public static final String IMPLFUNCTION_CREATE = "create";         // create(obj, ...)
    public static final String IMPLFUNCTION_PROCESS = "process";       // process(delta, input)
    public static final String IMPLFUNCTION_ONPAUSE = "onPause";       // onPause()
    public static final String IMPLFUNCTION_ONRESUME = "onResume";     // onResume()
    public static final String IMPLFUNCTION_ONRENDER = "onRender";     // onRender()
    public static final String IMPLFUNCTION_ONPERSIST = "onPersist";   // onPersist()
    public static final String IMPLFUNCTION_ONCOLLIDE = "onCollide";   // onCollide(object)
    public static final String IMPLFUNCTION_ONINTERACT = "onInteract"; // onInteract(object)
    
    public static final String[] REQUIRED_FUNCTIONS = {IMPLFUNCTION_CREATE};
    public static final String[] FUNCTIONS = {IMPLFUNCTION_CREATE, IMPLFUNCTION_PROCESS, IMPLFUNCTION_ONRENDER, IMPLFUNCTION_ONCOLLIDE, IMPLFUNCTION_ONINTERACT, IMPLFUNCTION_ONPERSIST, IMPLFUNCTION_ONPAUSE, IMPLFUNCTION_ONRESUME};
    
    public static class WorldObjectImplementation extends WorldObject implements LuaImplementation {
        
        // implementable will replace these variables
        private LuaImplementable<?, ?> impl;
        private Map<String, LuaFunction> functions;
        private String objName;
        
        // generic impl of LuaImplementation; screw readability they're one-liners
        @Override public LuaImplementable<?, ?> getImplementable() { return impl; }
        @Override public void setImplementable(LuaImplementable<?, ?> impl) { this.impl = impl; }
        @Override public Map<String, LuaFunction> getFunctions() { return new HashMap<String, LuaFunction>(functions); }
        @Override public void setFunctions(Map<String, LuaFunction> functions) { this.functions = functions; }
        @Override public void setFunction(String name, LuaFunction function) { this.functions.put(name, function); }

        @Override
        public String getObjectName() {
            return objName;
        }
        
        @Override
        public void process(float delta, InputData input) {
            LuaUtil.invokeNonNull(functions.get(IMPLFUNCTION_PROCESS), LuaValue.valueOf(delta), LuaInputDataMeta.create(input));
        }
        
        @Override
        public void onRender() {
            LuaUtil.invokeNonNull(functions.get(IMPLFUNCTION_ONRENDER));
        }

        @Override
        public void onCollide(Collider collider) {
            if(collider instanceof WorldObject) {
                LuaUtil.invokeNonNull(functions.get(IMPLFUNCTION_ONCOLLIDE), LuaWorldObjectMeta.create((WorldObject) collider));
            }
        }
        
        @Override
        public void onPersist(WorldRoom newRoom, Entrypoint entrypoint) {
            LuaUtil.invokeNonNull(functions.get(IMPLFUNCTION_ONPERSIST), LuaWorldRoomMeta.create(newRoom), entrypoint == null ? LuaValue.NIL : LuaEntrypointMeta.create(entrypoint));
        }
        
        @Override
        public void onPause() {
            LuaUtil.invokeNonNull(functions.get(IMPLFUNCTION_ONPAUSE));
        }
        
        @Override
        public void onResume() {
            LuaUtil.invokeNonNull(functions.get(IMPLFUNCTION_ONRESUME));
        }
    }
    
    @Override
    public String[] getRequiredFunctions() {
        return REQUIRED_FUNCTIONS;
    }

    @Override
    public String[] getFunctions() {
        return FUNCTIONS;
    }

    @Override
    public void onLoad(File scriptFile, WorldObjectImplementation baseObject) {
        baseObject.objName = scriptFile.getName().split("\\.")[0];
        baseObject.getFunctions().get(IMPLFUNCTION_CREATE).call(LuaWorldObjectMeta.create(baseObject));
    }

    @Override
    public WorldObjectImplementation load(File scriptFile, Globals globals) throws LuaScriptException {
        try {
            WorldObjectImplementation impl = LuaImplementable.loadFile(this, scriptFile, globals, new WorldObjectImplementation());
            this.onLoad(scriptFile, impl);
            return impl;
        } catch(LuaScriptException e) {
            throw e;
        } catch(Exception e) {
            LuaScriptException thrown = new LuaScriptException("internal error");
            thrown.initCause(e);
            throw thrown;
        }
    }
}
