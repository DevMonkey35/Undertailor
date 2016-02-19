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

import me.scarlet.undertailor.collision.Collider;
import me.scarlet.undertailor.environment.overworld.WorldObject;
import me.scarlet.undertailor.environment.overworld.WorldRoom;
import me.scarlet.undertailor.environment.overworld.WorldRoom.Entrypoint;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.LuaImplementable;
import me.scarlet.undertailor.lua.LuaImplementation;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.impl.WorldObjectImplementable.WorldObjectImplementation;
import me.scarlet.undertailor.lua.impl.WorldRoomImplementable.WorldRoomImplementation;
import me.scarlet.undertailor.lua.lib.meta.LuaEntrypointMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaInputDataMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaWorldObjectMeta;
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

public class WorldObjectImplementable implements LuaImplementable<File, WorldObjectImplementation> {
    
    public static final String IMPLFUNCTION_CREATE = "create";         // create(self, ...)
    public static final String IMPLFUNCTION_PROCESS = "process";       // process(self, delta, input)
    public static final String IMPLFUNCTION_ONPAUSE = "onPause";       // onPause(self)
    public static final String IMPLFUNCTION_ONRESUME = "onResume";     // onResume(self)
    public static final String IMPLFUNCTION_ONRENDER = "onRender";     // onRender(self)
    public static final String IMPLFUNCTION_ONPERSIST = "onPersist";   // onPersist(self)
    public static final String IMPLFUNCTION_ONCOLLIDE = "onCollide";   // onCollide(self, object)
    public static final String IMPLFUNCTION_ONINTERACT = "onInteract"; // onInteract(self, object)
    public static final String IMPLFUNCTION_ONREGISTER = "onRegister"; // onRegister(self, id, room)
    
    public static final String[] REQUIRED_FUNCTIONS = {IMPLFUNCTION_CREATE};
    public static final String[] FUNCTIONS = {IMPLFUNCTION_CREATE, IMPLFUNCTION_PROCESS, IMPLFUNCTION_ONRENDER, IMPLFUNCTION_ONCOLLIDE, IMPLFUNCTION_ONINTERACT, IMPLFUNCTION_ONPERSIST, IMPLFUNCTION_ONPAUSE, IMPLFUNCTION_ONRESUME};
    
    public static class WorldObjectImplementation extends WorldObject implements LuaImplementation {
        
        // implementable will replace these variables
        private LuaImplementable<?, ?> impl;
        private Map<String, LuaFunction> functions;
        private WeakReference<LuaObjectValue<?>> obj;
        private String objName;
        
        // generic impl of LuaImplementation; screw readability they're one-liners
        @Override public LuaImplementable<?, ?> getImplementable() { return impl; }
        @Override public void setImplementable(LuaImplementable<?, ?> impl) { this.impl = impl; }
        @Override public Map<String, LuaFunction> getFunctions() { if(this.functions != null) return new HashMap<>(functions); else return null; }
        @Override public void setFunctions(Map<String, LuaFunction> functions) { this.functions = functions; }
        
        @Override public LuaObjectValue<?> getObjectValue() { return obj.get(); }
        @Override public void setObjectValue(LuaObjectValue<?> obj) {
            if((obj.getObject() instanceof WorldRoomImplementation)) {
                throw new IllegalArgumentException("cannot accept object value (mismatching value)");
            }
            
            this.obj = new WeakReference<>(obj);
            functions.keySet().stream().filter(key -> this.functions != null)
                    .filter(key -> functions.containsKey(key)).forEach(key -> obj
                    .set(key, functions.get(key)));
        }

        @Override
        public String getObjectName() {
            return objName;
        }
        
        @Override
        public void process(float delta, InputData input) {
            super.process(delta, input);
            LuaUtil.invokeNonNull(obj.get(), IMPLFUNCTION_PROCESS, obj.get(), LuaValue.valueOf(delta), LuaInputDataMeta.create(input));
        }
        
        @Override
        public void onRender() {
            LuaUtil.invokeNonNull(obj.get(), IMPLFUNCTION_ONRENDER, obj.get());
        }

        @Override
        public void onCollide(Collider collider) {
            if(collider instanceof WorldObject) {
                LuaUtil.invokeNonNull(obj.get(), IMPLFUNCTION_ONCOLLIDE, obj.get(), LuaWorldObjectMeta.create((WorldObject) collider));
            }
        }
        
        @Override
        public void onPersist(WorldRoom newRoom, Entrypoint entrypoint) {
            LuaUtil.invokeNonNull(obj.get(), IMPLFUNCTION_ONPERSIST, obj.get(), LuaWorldRoomMeta.create(newRoom), entrypoint == null ? LuaValue.NIL : LuaEntrypointMeta.create(entrypoint));
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
        public void onRegister(long id, WorldRoom room) {
            LuaUtil.invokeNonNull(obj.get(), IMPLFUNCTION_ONREGISTER, obj.get());
        }
    }
    
    private Map<String, File> loadedFiles;
    private Map<String, Map<String, LuaFunction>> loadedMapping;
    
    public WorldObjectImplementable() {
        this.loadedFiles = new HashMap<>();
        this.loadedMapping = new HashMap<>();
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
        if(!this.loadedMapping.containsKey(scriptId) || replace) {
            try {
                loadedMapping.put(scriptId, LuaImplementable.loadFile(this, loaded, globals));
                loadedFiles.put(scriptId, loaded);
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
    public WorldObjectImplementation load(String scriptId, Varargs args) throws LuaScriptException {
        if(loadedMapping.containsKey(scriptId) && loadedFiles.containsKey(scriptId)) {
            WorldObjectImplementation impl = new WorldObjectImplementation();
            impl.setFunctions(loadedMapping.get(scriptId));
            impl.setImplementable(this);
            impl.setObjectValue(LuaWorldObjectMeta.create(impl));
            
            impl.objName = loadedFiles.get(scriptId).getName().split("\\.")[0];
            impl.getFunctions().get(IMPLFUNCTION_CREATE).call(impl.getObjectValue());
            return impl;
        }
        
        return null;
    }
}
