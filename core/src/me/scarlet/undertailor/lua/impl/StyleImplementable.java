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

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaImplementable;
import me.scarlet.undertailor.lua.LuaImplementation;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.impl.StyleImplementable.StyleImplementation;
import me.scarlet.undertailor.lua.lib.meta.LuaStyleMeta;
import me.scarlet.undertailor.texts.Style;
import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;
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

public class StyleImplementable implements LuaImplementable<File, StyleImplementation>{

    public static final String IMPLFUNCTION_CREATE = "create"; // create(self)
    public static final String IMPLFUNCTION_ONNEXTTEXTRENDER = "onNextTextRender"; // onNextTextRender(self, delta)
    public static final String IMPLFUNCTION_APPLYCHARACTER = "applyCharacter"; // applyCharacter(self, charIndex, textLength)
    
    public static final String[] REQUIRED_FUNCTIONS = new String[] {IMPLFUNCTION_CREATE, IMPLFUNCTION_APPLYCHARACTER};
    public static final String[] FUNCTIONS = new String[] {IMPLFUNCTION_CREATE, IMPLFUNCTION_APPLYCHARACTER, IMPLFUNCTION_ONNEXTTEXTRENDER};
    
    public static class StyleImplementation implements LuaImplementation, Style {
        
        // implementable will replace these variables
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
        
        private File sourceFile;
        
        @SuppressWarnings("unchecked")
        @Override
        public DisplayMeta applyCharacter(int charIndex, int textLength) {
            LuaValue returned = (LuaValue) LuaUtil.invokeNonNull(obj.get(), IMPLFUNCTION_APPLYCHARACTER, obj.get(), LuaValue.valueOf(charIndex), LuaValue.valueOf(textLength));
            if(returned != null) {
                return ((LuaObjectValue<DisplayMeta>) LuaUtil.checkType(returned, Lua.TYPENAME_DISPLAYMETA)).getObject();
            } else {
                return null;
            }
        }
        
        @Override
        public void onNextTextRender(float delta) {
            LuaUtil.invokeNonNull(obj.get(), IMPLFUNCTION_ONNEXTTEXTRENDER, obj.get(), LuaValue.valueOf(delta));
        }

        @Override
        public Style duplicate() {
            try {
                StyleImplementable impl = Undertailor.getScriptManager().getImplementable(StyleImplementable.class);
                return impl.load(sourceFile.getName().split("\\.")[0], sourceFile);
            } catch(Exception e) {
                RuntimeException thrown = new RuntimeException();
                thrown.initCause(e);
                throw thrown; // we don't expect this to happen, but if it does, lolhere
            }
        }
    }

    private Map<String, File> loadedFiles;
    private Map<String, Map<String, LuaFunction>> loadedMapping;
    
    public StyleImplementable() {
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
    public StyleImplementation load(String scriptId, Varargs args) throws LuaScriptException {
        if(loadedMapping.containsKey(scriptId) && loadedFiles.containsKey(scriptId)) {
            StyleImplementation impl = new StyleImplementation();
            impl.setFunctions(loadedMapping.get(scriptId));
            impl.setImplementable(this);
            impl.setObjectValue(LuaStyleMeta.create(impl));

            impl.sourceFile = loadedFiles.get(scriptId);
            impl.getFunctions().get(IMPLFUNCTION_CREATE).call(impl.getObjectValue());
            return impl;
        }
        
        return null;
    }
}
