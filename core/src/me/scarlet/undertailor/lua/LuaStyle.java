package me.scarlet.undertailor.lua;

import com.badlogic.gdx.Gdx;
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
    public static final String IMPLMETHOD_ONNEWTEXT = "onNewText";
    
    public static LuaStyle checkStyle(LuaValue value) {
        if(!value.typename().equals(LuaStyle.TYPENAME)) {
            throw new LuaError("bad argument: expected " + LuaStyle.TYPENAME + "; got" + value.typename());
        }
        
        return (LuaStyle) value;
    }
    
    public static LuaValue invokeStyleFunction(LuaFunction function, int charIndex, int textLength) {
        return function.call(LuaValue.valueOf(charIndex), LuaValue.valueOf(textLength)).arg1();
    }
    
    public static Map<String, LuaFunction> checkImpl(LuaValue value) {
        Map<String, LuaFunction> returned = new HashMap<>();
        LuaValue val = value.get(IMPLMETHOD_APPLYCHARACTER);
        if(val.isfunction()) {
            if(LuaDisplayMeta.isDisplayMeta(invokeStyleFunction((LuaFunction) val, 0, 1).arg1())) {
                returned.put(IMPLMETHOD_APPLYCHARACTER, (LuaFunction) val);
            } else {
                Gdx.app.error("styleman", "ignoring incorrectly implemented method \"" + IMPLMETHOD_APPLYCHARACTER + "\" (did not return a " + LuaDisplayMeta.TYPENAME + ")");
            }
        }
        
        String[] optionalFuncs = new String[] {IMPLMETHOD_ONNEXTTEXTRENDER, IMPLMETHOD_ONNEWTEXT};
        for(String name : optionalFuncs) {
            LuaValue optional = value.get(name);
            if(optional.isfunction()) {
                returned.put(name, (LuaFunction) optional);
            }
        }
        
        return returned;
    }
    
    private Style style;
    private File originFile; // only for lua
    private Map<String, LuaFunction> functions; // only for lua
    public LuaStyle(File luaFile) throws LuaScriptException {
        this.originFile = luaFile;
        
        Globals impl = Undertailor.newGlobals();
        impl.loadfile(luaFile.getAbsolutePath()).invoke();
        this.functions = checkImpl(impl);
        if(functions.isEmpty() || !functions.containsKey(IMPLMETHOD_APPLYCHARACTER)) {
            throw new LuaScriptException("lua style implementation did not implement or incorrectly implemented method " + IMPLMETHOD_APPLYCHARACTER);
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
                LuaDisplayMeta luameta = (LuaDisplayMeta) invokeStyleFunction(function, charIndex, textLength);
                return luameta.getDisplayMeta();
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
