package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.texts.Style;
import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LuaStyle extends LuaValue implements Style {
    
    public static final String TYPENAME = "tailor-textstyle";
    public static LuaValue METATABLE;
    
    public static final String IMPLMETHOD_ONNEXTTEXTRENDER = "onNextTextRender";
    public static final String IMPLMETHOD_APPLYCHARACTER = "applyCharacter";
    
    public static LuaStyle checkStyle(LuaValue value) {
        if(!value.typename().equals(LuaStyle.TYPENAME)) {
            throw new LuaError("bad argument: expected " + LuaStyle.TYPENAME + "; got" + value.typename());
        }
        
        return (LuaStyle) value;
    }
    
    public static LuaValue invokeStyleFunction(LuaFunction function, int charIndex, int textLength) {
        return function.call(LuaValue.valueOf(charIndex), LuaValue.valueOf(textLength));
    }
    
    public static Map<String, LuaFunction> checkImpl(LuaValue value) {
        Map<String, LuaFunction> returned = new HashMap<>();
        LuaValue val = value.get(IMPLMETHOD_APPLYCHARACTER);
        if(val.isfunction()) {
            returned.put(IMPLMETHOD_APPLYCHARACTER, val.checkfunction());
        }
        
        String[] optionalFuncs = new String[] {IMPLMETHOD_ONNEXTTEXTRENDER};
        for(String name : optionalFuncs) {
            LuaValue optional = value.get(name);
            if(optional.isfunction()) {
                returned.put(name, optional.checkfunction());
            }
        }
        
        return returned;
    }
    
    private Style style;
    private File originFile; // only for lua
    private Map<String, LuaFunction> functions; // only for lua
    public LuaStyle(File luaFile) throws LuaScriptException {
        this.originFile = luaFile;
        
        Globals globals = Undertailor.newGlobals();
        globals.loadfile(luaFile.getAbsolutePath()).invoke();
        
        this.functions = checkImpl(globals);
        if(functions.isEmpty() || !functions.containsKey(IMPLMETHOD_APPLYCHARACTER)) {
            throw new LuaScriptException("lua style implementation did not implement " + IMPLMETHOD_APPLYCHARACTER);
        }
    }
    
    public LuaStyle(Style style) {
        this.style = style;
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
    public LuaValue getmetatable() {
        return METATABLE;
    }
    
    @Override
    public DisplayMeta applyCharacter(int charIndex, int textLength) {
        if(style == null) {
            LuaFunction function = functions.get(IMPLMETHOD_APPLYCHARACTER);
            if(function != null) {
                try {
                    LuaValue luameta = invokeStyleFunction(function, charIndex, textLength);
                    if(luameta.isnil()) {
                        return null;
                    } else {
                        return LuaDisplayMeta.checkDisplayMeta(luameta).getDisplayMeta();
                    }
                } catch(LuaError e) {
                    Undertailor.error("lua", e.getMessage());
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
