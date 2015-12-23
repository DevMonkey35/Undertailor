package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.lib.meta.LuaUIComponentMeta;
import me.scarlet.undertailor.manager.StyleManager;
import me.scarlet.undertailor.ui.UIComponent;
import me.scarlet.undertailor.ui.event.UIEvent;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.File;
import java.io.FileNotFoundException;
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
    public static final String[] METHODS = new String[] {IMPLMETHOD_CREATE, IMPLMETHOD_ONDESTROY, IMPLMETHOD_ONEVENT, IMPLMETHOD_PROCESS, IMPLMETHOD_RENDER};
    
    static {
        LuaUIComponentMeta.prepareMetatable();
    }
    
    public static LuaUIComponent checkUIComponent(LuaValue value) {
        if(!value.typename().equals(TYPENAME)) {
            throw new LuaError("bad argument: expected " + TYPENAME + ", got " + value.typename());
        }
        
        return (LuaUIComponent) value;
    }
    
    public static class LuaUIComponentImpl extends UIComponent {

        private String typename;
        private Map<String, LuaFunction> functions;
        public LuaUIComponentImpl(File luaFile) throws LuaScriptException {
            this.typename = luaFile.getName().split("\\.")[0];
            Globals globals = Undertailor.newGlobals();
            try {
                LuaUtil.loadFile(globals, luaFile);
            } catch(FileNotFoundException e) {
                Undertailor.instance.error(StyleManager.MANAGER_TAG, "failed to load style: file " + luaFile.getAbsolutePath() + " wasn't found");
            }
            
            functions = LuaUtil.checkImplementation(globals, luaFile, REQUIRED_METHODS);
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
        public void process(float delta, InputData input) {
            if(functions.containsKey(LuaUIComponent.IMPLMETHOD_PROCESS)) {
                functions.get(LuaUIComponent.IMPLMETHOD_PROCESS).call(LuaValue.valueOf(delta), new LuaInputData(input));
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
        this.setmetatable(METATABLE);
        this.component = component;
        prepareLuaComponent();
    }
    
    public LuaUIComponent(File luaFile, Varargs args) throws LuaScriptException {
        this.setmetatable(METATABLE);
        this.component = new LuaUIComponentImpl(luaFile);
        ((LuaUIComponentImpl) this.component).getFunctions().get(IMPLMETHOD_CREATE).invoke(this, args);
        prepareLuaComponent();
    }
    
    private void prepareLuaComponent() {
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
    public void rawset(LuaValue key, LuaValue value) {
        super.rawset(key, value);
        if(value != LuaValue.NIL && this.component instanceof LuaUIComponentImpl) {
            LuaUIComponentImpl luacom = (LuaUIComponentImpl) this.component;
            if(key.isstring()) {
                for(String method : METHODS) {
                    if(key.tojstring().equals(method)) {
                        luacom.getFunctions().put(method, value.checkfunction());
                        return;
                    }
                }
            }
        }
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
