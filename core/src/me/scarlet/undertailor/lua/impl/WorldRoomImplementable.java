package me.scarlet.undertailor.lua.impl;

import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.LuaImplementable;
import me.scarlet.undertailor.lua.LuaImplementation;
import me.scarlet.undertailor.lua.impl.WorldRoomImplementable.WorldRoomImplementation;
import me.scarlet.undertailor.lua.lib.meta.LuaEntrypointMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaInputDataMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaWorldRoomMeta;
import me.scarlet.undertailor.overworld.WorldRoom;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.wrappers.RoomDataWrapper;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WorldRoomImplementable implements LuaImplementable<RoomDataWrapper, WorldRoomImplementation> {
    
    public static final String IMPLFUNCTION_CREATE = "create";
    public static final String IMPLFUNCTION_PROCESS = "process";
    public static final String IMPLFUNCTION_ONPAUSE = "onPause";
    public static final String IMPLFUNCTION_ONRESUME = "onResume";
    public static final String IMPLFUNCTION_ONENTER = "onEnter";
    public static final String IMPLFUNCTION_ONEXIT = "onExit";
    
    public static String[] REQUIRED_FUNCTIONS = new String[] {IMPLFUNCTION_CREATE};
    public static String[] FUNCTIONS = new String[] {IMPLFUNCTION_CREATE, IMPLFUNCTION_PROCESS, IMPLFUNCTION_ONENTER, IMPLFUNCTION_ONEXIT, IMPLFUNCTION_ONPAUSE, IMPLFUNCTION_ONRESUME};
    
    public static class WorldRoomImplementation extends WorldRoom implements LuaImplementation {
        
        private LuaImplementable<?, ?> impl;
        private Map<String, LuaFunction> functions;
        
        public WorldRoomImplementation(RoomDataWrapper roomWrapper) {
            super(roomWrapper);
        }
        
        // generic impl of LuaImplementation; screw readability they're one-liners
        @Override public LuaImplementable<?, ?> getImplementable() { return impl; }
        @Override public void setImplementable(LuaImplementable<?, ?> impl) { this.impl = impl; }
        @Override public Map<String, LuaFunction> getFunctions() { return new HashMap<String, LuaFunction>(functions); }
        @Override public void setFunctions(Map<String, LuaFunction> functions) { this.functions = functions; }
        @Override public void setFunction(String name, LuaFunction function) { this.functions.put(name, function); }
        
        @Override
        public void onEnter(Entrypoint entrypoint) {
            LuaUtil.invokeNonNull(functions.get(IMPLFUNCTION_ONENTER), LuaEntrypointMeta.create(entrypoint));
        }
        
        @Override
        public void onExit(Entrypoint exitpoint) {
            LuaUtil.invokeNonNull(functions.get(IMPLFUNCTION_ONEXIT), LuaEntrypointMeta.create(exitpoint));
        }
        
        @Override
        public void onPause() {
            LuaUtil.invokeNonNull(functions.get(IMPLFUNCTION_ONPAUSE));
        }
        
        @Override
        public void onResume() {
            LuaUtil.invokeNonNull(functions.get(IMPLFUNCTION_ONRESUME));
        }
        
        @Override
        public void onProcess(float delta, InputData input) {
            LuaUtil.invokeNonNull(functions.get(IMPLFUNCTION_PROCESS), LuaValue.valueOf(delta), LuaInputDataMeta.create(input));
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
    public void onLoad(RoomDataWrapper loaded, WorldRoomImplementation baseObject) {
        baseObject.getFunctions().get(IMPLFUNCTION_CREATE).call(LuaWorldRoomMeta.create(baseObject));
    }

    @Override
    public WorldRoomImplementation load(RoomDataWrapper loaded, Globals globals) throws LuaScriptException {
        try {
            File scriptFile = loaded.getRoomScript();
            WorldRoomImplementation impl = LuaImplementable.loadFile(this, scriptFile, globals, new WorldRoomImplementation(loaded));
            this.onLoad(loaded, impl);
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
