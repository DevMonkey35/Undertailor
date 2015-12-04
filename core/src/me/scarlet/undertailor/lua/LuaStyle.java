package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.lib.meta.LuaStyleMeta;
import me.scarlet.undertailor.texts.Style;
import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LuaStyle extends LuaTable implements Style {
    
    public static final String TYPENAME = "tailor-textstyle";
    public static LuaValue METATABLE;
    
    public static final String IMPLMETHOD_ONNEXTTEXTRENDER = "onNextTextRender";
    public static final String IMPLMETHOD_APPLYCHARACTER = "applyCharacter";
    
    public static final String[] REQUIRED_METHODS = new String[] {IMPLMETHOD_APPLYCHARACTER};
    public static final String[] METHODS = new String[] {IMPLMETHOD_APPLYCHARACTER, IMPLMETHOD_ONNEXTTEXTRENDER};
    
    static {
        LuaStyleMeta.prepareMetatable();
    }
    
    public static LuaStyle checkStyle(LuaValue value) {
        if(!value.typename().equals(LuaStyle.TYPENAME)) {
            throw new LuaError("bad argument: expected " + LuaStyle.TYPENAME + "; got" + value.typename());
        }
        
        return (LuaStyle) value;
    }
    
    public static Map<String, LuaFunction> checkImpl(Globals value, File scriptFile) throws LuaScriptException {
        return LuaUtil.checkImplementation(value, scriptFile, REQUIRED_METHODS);
    }
    
    private Style style;
    private File originFile; // only for lua
    private Map<String, LuaFunction> functions; // only for lua
    public LuaStyle(File luaFile) throws LuaScriptException {
        this.originFile = luaFile;
        
        Globals globals = Undertailor.newGlobals();
        globals.loadfile(luaFile.getAbsolutePath()).invoke();
        
        try {
            this.functions = checkImpl(globals, luaFile);
        } catch(LuaScriptException e) {
            this.functions = new HashMap<>();
            throw new LuaScriptException("failed to load style implementation: " + e.getMessage());
        }
        
        for(Map.Entry<String, LuaFunction> entry : functions.entrySet()) {
            this.set(entry.getKey(), entry.getValue());
        }
        
        prepareLuaStyle();
    }
    
    public LuaStyle(Style style) {
        prepareLuaStyle();
        this.style = style;
    }
    
    private void prepareLuaStyle() {
        this.setmetatable(METATABLE);
        for(Map.Entry<String, LuaFunction> entry : functions.entrySet()) {
            this.set(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public int type() {
        return LuaValue.TVALUE;
    }

    @Override
    public String typename() {
        return LuaStyle.TYPENAME;
    }
    
    @Override
    public DisplayMeta applyCharacter(int charIndex, int textLength) {
        if(style == null) {
            LuaFunction function = functions.get(IMPLMETHOD_APPLYCHARACTER);
            if(function != null) {
                try {
                    LuaValue luameta = function.call(LuaValue.valueOf(charIndex), LuaValue.valueOf(textLength));
                    if(luameta.isnil()) {
                        return null;
                    } else {
                        return LuaDisplayMeta.checkDisplayMeta(luameta).getDisplayMeta();
                    }
                } catch(LuaError e) {
                    Undertailor.instance.error("lua", e.getMessage());
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return style.applyCharacter(charIndex, textLength);
        }
    }
    
    @Override
    public void rawset(LuaValue key, LuaValue value) {
        super.rawset(key, value);
        if(value != LuaValue.NIL && this.style == null) {
            if(key.isstring()) {
                for(String method : METHODS) {
                    if(key.tojstring().equals(method)) {
                        functions.put(method, value.checkfunction());
                        return;
                    }
                }
            }
        }
    }
    
    @Override
    public void onNextTextRender(float delta) {
        if(style == null) {
            LuaFunction function = functions.get(IMPLMETHOD_ONNEXTTEXTRENDER);
            if(function != null) {
                function.call(LuaValue.valueOf(delta));
            }
        } else {
            style.onNextTextRender(delta);
        }
    }

    @Override
    public Style duplicate() {
        if(style == null) {
            try {
                return new LuaStyle(originFile);
            } catch(LuaScriptException ignore) {}
        } else {
            return style.duplicate();
        }
        
        return null;
    }
}
