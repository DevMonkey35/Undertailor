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

import me.scarlet.undertailor.environment.ui.UIComponent;
import me.scarlet.undertailor.environment.ui.event.UIEvent;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.LuaImplementable;
import me.scarlet.undertailor.lua.LuaImplementation;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.impl.UIComponentImplementable.UIComponentImplementation;
import me.scarlet.undertailor.lua.lib.meta.LuaInputDataMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaUIComponentMeta;
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

public class UIComponentImplementable implements LuaImplementable<File, UIComponentImplementation> {

    public static final String IMPLFUNCTION_ONDESTROY = "onDestroy"; // onDestroy(boolean)
    public static final String IMPLFUNCTION_ONEVENT = "onEvent";     // onEvent(uievent)
    public static final String IMPLFUNCTION_PROCESS = "process";     // process(delta)
    public static final String IMPLFUNCTION_CREATE = "create";       // create(uicomponent, args..)
    public static final String IMPLFUNCTION_RENDER = "render";       // render()
    public static final String[] REQUIRED_FUNCTIONS = new String[] {IMPLFUNCTION_CREATE};
    public static final String[] FUNCTIONS = new String[] {IMPLFUNCTION_CREATE, IMPLFUNCTION_ONDESTROY, IMPLFUNCTION_ONEVENT, IMPLFUNCTION_PROCESS, IMPLFUNCTION_RENDER};
    
    public static class UIComponentImplementation extends UIComponent implements LuaImplementation {
        
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
            this.obj = new WeakReference<>(obj);
            if(this.functions != null) {
                functions.keySet().forEach(key -> obj.set(key, functions.get(key)));
            }
        }

        @Override
        public String getComponentTypeName() {
            return objName;
        }
        
        @Override
        public void onDestroy(boolean object) {
            LuaUtil.invokeNonNull(obj.get(), IMPLFUNCTION_ONDESTROY, obj.get(), LuaValue.valueOf(object));
        }
        
        @Override
        public void process(float delta, InputData input) {
            LuaUtil.invokeNonNull(obj.get(), IMPLFUNCTION_PROCESS, obj.get(), LuaValue.valueOf(delta), LuaInputDataMeta.create(input));
        }
        
        @Override
        public void render(float parentAlpha) {
            LuaUtil.invokeNonNull(obj.get(), IMPLFUNCTION_RENDER, obj.get(), LuaValue.valueOf(parentAlpha));
        }
        
        @Override
        public void onEvent(UIEvent event) {
            super.onEvent(event); // TODO uievent stuff
        }
    }
    
    private Map<String, File> loadedFiles;
    private Map<String, Map<String, LuaFunction>> loadedMapping;
    
    public UIComponentImplementable() {
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
    public UIComponentImplementation load(String scriptId, Varargs args) throws LuaScriptException {
        if(loadedMapping.containsKey(scriptId) && loadedFiles.containsKey(scriptId)) {
            UIComponentImplementation impl = new UIComponentImplementation();
            impl.setFunctions(loadedMapping.get(scriptId));
            impl.setImplementable(this);
            impl.setObjectValue(LuaUIComponentMeta.create(impl));
            
            File loadData = loadedFiles.get(scriptId);
            impl.objName = loadData.getName().split("\\.")[0];
            impl.getFunctions().get(IMPLFUNCTION_CREATE).invoke(impl.getObjectValue(), args);
            return impl;
        }
        
        return null;
    }
}
