package me.scarlet.undertailor.lua.impl;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaImplementable;
import me.scarlet.undertailor.lua.LuaImplementation;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.impl.StyleImplementable.StyleImplementation;
import me.scarlet.undertailor.texts.Style;
import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class StyleImplementable implements LuaImplementable<File, StyleImplementation>{
    
    public static final String IMPLFUNCTION_ONNEXTTEXTRENDER = "onNextTextRender";
    public static final String IMPLFUNCTION_APPLYCHARACTER = "applyCharacter";
    
    public static final String[] REQUIRED_FUNCTIONS = new String[] {IMPLFUNCTION_APPLYCHARACTER};
    public static final String[] FUNCTIONS = new String[] {IMPLFUNCTION_APPLYCHARACTER, IMPLFUNCTION_ONNEXTTEXTRENDER};
    
    public static class StyleImplementation implements LuaImplementation, Style {
        
        // implementable will replace these variables
        private LuaImplementable<?, ?> impl;
        private Map<String, LuaFunction> functions;
        
        // generic impl of LuaImplementation; screw readability they're one-liners
        @Override public LuaImplementable<?, ?> getImplementable() { return impl; }
        @Override public void setImplementable(LuaImplementable<?, ?> impl) { this.impl = impl; }
        @Override public Map<String, LuaFunction> getFunctions() { return new HashMap<String, LuaFunction>(functions); }
        @Override public void setFunctions(Map<String, LuaFunction> functions) { this.functions = functions; }
        @Override public void setFunction(String name, LuaFunction function) { this.functions.put(name, function); }
        
        private File sourceFile;
        
        @SuppressWarnings("unchecked")
        @Override
        public DisplayMeta applyCharacter(int charIndex, int textLength) {
            LuaValue returned = (LuaValue) LuaUtil.invokeNonNull(functions.get(IMPLFUNCTION_APPLYCHARACTER), LuaValue.valueOf(charIndex), LuaValue.valueOf(textLength));
            if(returned != null) {
                return ((LuaObjectValue<DisplayMeta>) LuaUtil.checkType(returned, Lua.TYPENAME_DISPLAYMETA)).getObject();
            } else {
                return null;
            }
        }
        
        @Override
        public void onNextTextRender(float delta) {
            LuaUtil.invokeNonNull(functions.get(IMPLFUNCTION_ONNEXTTEXTRENDER), LuaValue.valueOf(delta));
        }

        @Override
        public Style duplicate() {
            try {
                return Undertailor.getScriptManager().generateImplementation(StyleImplementable.class, sourceFile);
            } catch(Exception e) {
                RuntimeException thrown = new RuntimeException();
                thrown.initCause(e);
                throw thrown; // we don't expect this to happen, but if it does, lolhere
            }
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
    public void onLoad(File loaded, StyleImplementation baseObject) {
        baseObject.sourceFile = loaded;
    }

    @Override
    public StyleImplementation load(File loaded, Globals globals) throws LuaScriptException {
        try {
            StyleImplementation impl = LuaImplementable.loadFile(this, loaded, globals, new StyleImplementation());
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
