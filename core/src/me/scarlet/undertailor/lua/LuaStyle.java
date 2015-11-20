package me.scarlet.undertailor.lua;

import com.badlogic.gdx.Gdx;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.texts.Style;
import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.util.HashMap;
import java.util.Map;

public class LuaStyle extends LuaValue implements Style {
    
    public static final String TYPENAME = "tailor-textstyle";
    
    public static final String IMPLMETHOD_ONNEXTTEXTRENDER = "onNextTextRender";
    public static final String IMPLMETHOD_APPLYCHARACTER = "applyCharacter";
    public static final String IMPLMETHOD_ONNEWTEXT = "onNewText";
    
    public static void checkStyle(LuaValue value) {
        if(!value.typename().equals(LuaStyle.TYPENAME)) {
            throw new LuaError("bad argument: expected " + LuaStyle.TYPENAME + "; got" + value.typename());
        }
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
    
    private Map<String, LuaFunction> functions;
    public LuaStyle(LuaValue impl) throws LuaScriptException {
        this.functions = checkImpl(impl);
        if(functions.isEmpty() || !functions.containsKey(IMPLMETHOD_APPLYCHARACTER)) {
            throw new LuaScriptException("lua style implementation did not implement or incorrectly implemented method " + IMPLMETHOD_APPLYCHARACTER);
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
        LuaFunction function = functions.get(IMPLMETHOD_APPLYCHARACTER);
        if(function != null) {
            LuaDisplayMeta luameta = (LuaDisplayMeta) invokeStyleFunction(function, charIndex, textLength);
            return luameta.getDisplayMeta();
        } else {
            return null;
        }
    }
    
    @Override
    public void onNextTextRender(float delta) {
        LuaFunction function = functions.get(IMPLMETHOD_ONNEXTTEXTRENDER);
        if(function != null) {
            function.call(LuaValue.valueOf(delta));
        }
    }
}
