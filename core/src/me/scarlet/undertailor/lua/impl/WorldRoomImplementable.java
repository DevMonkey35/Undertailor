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

package me.scarlet.undertailor.lua.impl;

import me.scarlet.undertailor.environment.overworld.WorldRoom;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.LuaImplementable;
import me.scarlet.undertailor.lua.LuaImplementation;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.impl.WorldRoomImplementable.WorldRoomImplementation;
import me.scarlet.undertailor.lua.lib.meta.LuaEntrypointMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaInputDataMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaWorldRoomMeta;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class WorldRoomImplementable implements LuaImplementable<File, WorldRoomImplementation> {
    
    public static final String IMPLFUNCTION_CREATE = "create";     // create(self)
    public static final String IMPLFUNCTION_PROCESS = "process";   // process(self, delta, input)
    public static final String IMPLFUNCTION_ONPAUSE = "onPause";   // onPause(self)
    public static final String IMPLFUNCTION_ONRESUME = "onResume"; // onResume(self)
    public static final String IMPLFUNCTION_ONENTER = "onEnter";   // onEnter(self, entrypoint)
    public static final String IMPLFUNCTION_ONEXIT = "onExit";     // onExit(self, exitpoint)
    
    public static String[] REQUIRED_FUNCTIONS = new String[] {IMPLFUNCTION_CREATE};
    public static String[] FUNCTIONS = new String[] {IMPLFUNCTION_CREATE, IMPLFUNCTION_PROCESS, IMPLFUNCTION_ONENTER, IMPLFUNCTION_ONEXIT, IMPLFUNCTION_ONPAUSE, IMPLFUNCTION_ONRESUME};
    
    public static class WorldRoomImplementation extends WorldRoom implements LuaImplementation {
        
        private LuaImplementable<?, ?> impl;
        private Map<String, LuaFunction> functions;
        private WeakReference<LuaObjectValue<?>> obj;
        
        // generic impl of LuaImplementation; screw readability they're one-liners
        @Override public LuaImplementable<?, ?> getImplementable() { return impl; }
        @Override public void setImplementable(LuaImplementable<?, ?> impl) { this.impl = impl; }
        @Override public Map<String, LuaFunction> getFunctions() { if(this.functions != null) return new HashMap<>(functions); else return null; }
        @Override public void setFunctions(Map<String, LuaFunction> functions) { this.functions = functions; }
        
        @Override public LuaObjectValue<?> getObjectValue() { return obj.get(); }
        @Override public void setObjectValue(LuaObjectValue<?> obj) {
            this.obj = new WeakReference<>(obj);
            if(this.functions != null) {
                functions.keySet().forEach(key -> obj.set(key, functions.get(key)));
            }
        }
        
        @Override
        public void onEnter(Entrypoint entrypoint) {
            LuaUtil.invokeNonNull(obj.get(), IMPLFUNCTION_ONENTER, obj.get(), LuaEntrypointMeta.create(entrypoint));
        }
        
        @Override
        public void onExit(Entrypoint exitpoint) {
            LuaUtil.invokeNonNull(obj.get(), IMPLFUNCTION_ONEXIT, obj.get(), LuaEntrypointMeta.create(exitpoint));
        }
        
        @Override
        public void onPause() {
            LuaUtil.invokeNonNull(obj.get(), IMPLFUNCTION_ONPAUSE, obj.get());
        }
        
        @Override
        public void onResume() {
            LuaUtil.invokeNonNull(obj.get(), IMPLFUNCTION_ONRESUME, obj.get());
        }
        
        @Override
        public void onProcess(float delta, InputData input) {
            LuaUtil.invokeNonNull(obj.get(), IMPLFUNCTION_PROCESS, obj.get(), LuaValue.valueOf(delta), LuaInputDataMeta.create(input));
        }
    }
    
    private Map<String, File> loadedFiles;
    private Map<String, Map<String, LuaFunction>> loadedMapping;
    
    public WorldRoomImplementable() {
        this.loadedMapping = new HashMap<>();
        this.loadedFiles = new HashMap<>();
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
    public void loadFunctions(String scriptId, File loaded, Globals globals, boolean replace) throws LuaScriptException {
        if(!this.loadedMapping.containsKey(scriptId) // not registered?
                || (this.loadedMapping.containsKey(scriptId) && replace)) { // registered, but replace flag is true?
            try {
                loadedMapping.put(scriptId, LuaImplementable.loadFile(this, loaded, globals));
                loadedFiles.put(scriptId, loaded); // after so its not registered if script loading fails
            } catch(LuaScriptException | LuaError e) {
                throw new LuaError("\n\t" + e.getMessage());
            } catch(Exception e) {
                LuaScriptException thrown = new LuaScriptException("internal error");
                thrown.initCause(e);
                throw thrown;
            }
        }
    }
    
    @Override
    public WorldRoomImplementation load(String scriptId, Varargs args) throws LuaScriptException {
        if(loadedFiles.containsKey(scriptId) && loadedMapping.containsKey(scriptId)) {
            WorldRoomImplementation impl = new WorldRoomImplementation();
            impl.setImplementable(this);
            impl.setFunctions(loadedMapping.get(scriptId));
            impl.setObjectValue(LuaWorldRoomMeta.create(impl));
            
            impl.getFunctions().get(IMPLFUNCTION_CREATE).call(impl.getObjectValue());
            return impl;
        }
        
        return null;
    }
}
