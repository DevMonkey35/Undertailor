package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.lib.meta.LuaUIComponentMeta;
import me.scarlet.undertailor.ui.UIComponent;
import me.scarlet.undertailor.ui.event.UIEvent;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.File;
import java.util.Map;

public class LuaUIComponent extends LuaTable {
    
    public static final String TYPENAME = "tailor-uicomponent";
    public static LuaValue METATABLE;

    public static final String IMPLMETHOD_ONDESTROY = "onDestroy"; // onDestroy(boolean)
    public static final String IMPLMETHOD_ONEVENT = "onEvent";     // onEvent(uievent)
    public static final String IMPLMETHOD_PROCESS = "process";     // process(delta)
    public static final String IMPLMETHOD_CREATE = "create";       // create(uicomponent, args..)
    public static final String IMPLMETHOD_RENDER = "render";       // render()
    public static final String[] REQUIRED_METHODS = new String[] {IMPLMETHOD_CREATE};
    public static final String[] OPTIONAL_METHODS = new String[] {IMPLMETHOD_ONDESTROY, IMPLMETHOD_ONEVENT, IMPLMETHOD_PROCESS, IMPLMETHOD_RENDER};
    
    static {
        LuaUIComponentMeta.prepareMetatable();
    }
    
    public static LuaUIComponent checkUIComponent(LuaValue value) {
        if(!value.typename().equals(TYPENAME)) {
            throw new LuaError("bad argument: expected " + TYPENAME + ", got " + value.typename());
        }
        
        return (LuaUIComponent) value;
    }
    
    public static Map<String, LuaFunction> checkImpl(Globals value, File scriptFile) throws LuaScriptException {
        try {
            return LuaUtil.checkImplementation(value, scriptFile, REQUIRED_METHODS);
        } catch(LuaScriptException e) {
            throw new LuaScriptException("failed to load uicomponent implementation: " + e.getMessage());
        }
    }
    
    public static class LuaUIComponentImpl extends UIComponent {
        
        private String typename;
        private Map<String, LuaFunction> functions;
        public LuaUIComponentImpl(LuaUIComponent parent, File luaFile, Varargs args) throws LuaScriptException {
            this.typename = luaFile.getName().split("\\.")[0];
            Globals globals = Undertailor.newGlobals();
            globals.loadfile(luaFile.getAbsolutePath()).invoke();
            functions = LuaUIComponent.checkImpl(globals, luaFile);
            functions.get(LuaUIComponent.IMPLMETHOD_CREATE).invoke(parent, args);
        }
        
        public Map<String, LuaFunction> getFunctions() {
            return functions;
        }
        
        @Override
        public void onDestroy(boolean object) {
            if(functions.containsKey(LuaUIComponent.IMPLMETHOD_ONDESTROY)) {
                functions.get(IMPLMETHOD_ONDESTROY).call(LuaValue.valueOf(object));
            }
        }
        
        @Override
        public void process(float delta) {
            if(functions.containsKey(LuaUIComponent.IMPLMETHOD_PROCESS)) {
                functions.get(LuaUIComponent.IMPLMETHOD_PROCESS).call(LuaValue.valueOf(delta));
            }
        }
        
        @Override
        public void render(float parentAlpha) {
            if(functions.containsKey(LuaUIComponent.IMPLMETHOD_RENDER)) {
                functions.get(LuaUIComponent.IMPLMETHOD_RENDER).call();
            }
        }
        
        @Override
        public void onEvent(UIEvent event) {
            if(functions.containsKey(LuaUIComponent.IMPLMETHOD_ONEVENT)) {
                // TODO impl uievent stuffs
            }
        }
        
        @Override
        public String getComponentTypeName() {
            return typename;
        }
    }
    
    private UIComponent component;
    public LuaUIComponent(UIComponent component) {
        this.component = component;
        prepareLuaComponent();
    }
    
    public LuaUIComponent(File luaFile, Varargs args) throws LuaScriptException {
        this.component = new LuaUIComponentImpl(this, luaFile, args);
        prepareLuaComponent();
    }
    
    private void prepareLuaComponent() {
        this.setmetatable(METATABLE);
        if(this.component instanceof LuaUIComponentImpl) {
            Map<String, LuaFunction> functions = ((LuaUIComponentImpl) this.component).getFunctions();
            for(Map.Entry<String, LuaFunction> entry : functions.entrySet()) {
                this.set(entry.getKey(), entry.getValue());
            }
        }
    }
    
    public UIComponent getComponent() {
        return component;
    }
    
    @Override
    public int type() {
        return LuaValue.TTABLE;
    }

    @Override
    public String typename() {
        return TYPENAME;
    }
}
