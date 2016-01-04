package me.scarlet.undertailor.lua.lib.meta;

import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.texts.Style;
import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaStyleMeta extends LuaLibrary {
    
    public static LuaObjectValue<Style> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_STYLE);
    }
    
    public static LuaObjectValue<Style> create(Style style) {
        return LuaObjectValue.of(style, Lua.TYPENAME_STYLE, Lua.META_STYLE);
    }
    
    public static LuaObjectValue<DisplayMeta> createDisplayMeta(DisplayMeta value) {
        return LuaObjectValue.of(value, Lua.TYPENAME_DISPLAYMETA);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = new LibraryFunction[] {
            new applyCharacter(),
            new onNextTextRender()
    };
    
    public LuaStyleMeta() {
        super(null, COMPONENTS);
    }
    
    static class applyCharacter extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 3, 3);
            
            Style style = check(args.arg1()).getObject();
            int charIndex = args.checkint(2);
            int textLength = args.checkint(3);
            
            return createDisplayMeta(style.applyCharacter(charIndex, textLength));
        }
    }
    
    static class onNextTextRender extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            Style style = check(args.arg1()).getObject();
            float delta = new Float(args.checkdouble(2));
            
            style.onNextTextRender(delta);
            return LuaValue.NIL;
        }
    }
}
