package me.scarlet.undertailor.lua.lib.text;

import com.badlogic.gdx.graphics.Color;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.audio.SoundWrapper;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.lib.ColorsLib;
import me.scarlet.undertailor.lua.lib.TextLib;
import me.scarlet.undertailor.lua.lib.game.AudioLib;
import me.scarlet.undertailor.lua.lib.meta.LuaStyleMeta;
import me.scarlet.undertailor.texts.Font;
import me.scarlet.undertailor.texts.Style;
import me.scarlet.undertailor.texts.TextComponent;
import me.scarlet.undertailor.texts.TextComponent.Text;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class TextComponentLib extends LuaLibrary {
    
    @SuppressWarnings("unchecked")
    public static LuaObjectValue<TextComponent> check(LuaValue value) {
        if(LuaUtil.isOfType(value, Lua.TYPENAME_TEXTCOMPONENT) || LuaUtil.isOfType(value, Lua.TYPENAME_TEXT)) {
            return (LuaObjectValue<TextComponent>) value;
        }
        
        throw new LuaError("expected " + Lua.TYPENAME_TEXTCOMPONENT + " or " + Lua.TYPENAME_TEXT + ", got " + value.typename());
    }
    
    public static LuaObjectValue<TextComponent> create(TextComponent value) {
        return LuaObjectValue.of(value, Lua.TYPENAME_TEXTCOMPONENT, LuaLibrary.asMetatable(Lua.LIB_TEXTCOMPONENT));
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new getColor(),
            new getDelay(),
            new getSegmentSize(),
            new getSound(),
            new getSpeed(),
            new getStyle(),
            new getText(),
            new newComponent(),
            new substring()
    };
    
    public TextComponentLib() {
        super("component", COMPONENTS);
    }
    
    @Override
    public void postinit(LuaValue env, LuaValue table) {
        LuaUtil.iterateTable((LuaTable) table, args -> {
            env.set(args.arg1(), args.arg(2)); // dupe the funcs
        });
        
        env.set("component", table);
    }
    
    static class newComponent extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 7);
            
            String text = args.arg(1).checkstring().tojstring();
            Font font = args.arg(2).isnil() ? null : Undertailor.getFontManager().getRoomObject(args.arg(2).checkstring().tojstring());
            Style style = args.arg(3).isnil() ? null : Undertailor.getStyleManager().getRoomObject(args.arg(3).checkstring().tojstring());
            Color color = args.arg(4).isnil() ? null : ColorsLib.check(args.arg(4)).getObject();
            SoundWrapper sound = args.arg(5).isnil() ? null : Undertailor.getAudioManager().getSoundManager().getResource(args.arg(5).checkstring().tojstring());
            int speed = args.arg(6).isnil() ? TextComponent.DEFAULT_SPEED : args.arg(6).checkint();
            int segsize = args.arg(7).isnil() ? 1 : args.arg(7).checkint();
            float wait = args.arg(8).isnil() ? 0F : new Float(args.arg(8).checkdouble());
            
            if(text.trim().isEmpty()) {
                throw new LuaError("bad argument: text cannot be empty or only have whitespace characters");
            }
            
            return create(new TextComponent(text, font, style, color, sound, speed, segsize, wait));
        }
    }
    
    static class getText extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            String text = check(args.arg1()).getObject().getText();
            if(text == null) {
                return LuaValue.NIL;
            }
            
            return LuaValue.valueOf(text);
        }
    }
    
    static class getSound extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            TextComponent component = check(args.arg1()).getObject();
            if(component.getSound() == null) {
                return LuaValue.NIL;
            }
            
            return AudioLib.createSound(component.getSound());
        }
    }
    
    static class getSpeed extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            TextComponent component = check(args.arg1()).getObject();
            return LuaValue.valueOf(component.getSpeed());
        }
    }
    
    static class getDelay extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            TextComponent component = check(args.arg1()).getObject();
            return LuaValue.valueOf(component.getDelay());
        }
    }
    
    static class getStyle extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            TextComponent component = check(args.arg1()).getObject();
            if(component.getStyle() == null) {
                return LuaValue.NIL;
            }
            
            return LuaStyleMeta.create(component.getStyle());
        }
    }
    
    static class getColor extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            TextComponent component = check(args.arg1()).getObject();
            if(component.getColor() == null) {
                return LuaValue.NIL;
            }
            
            return ColorsLib.create(component.getColor());
        }
    }
    
    static class getSegmentSize extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            TextComponent component = check(args.arg1()).getObject();
            return LuaValue.valueOf(component.getSegmentSize());
        }
    }
    
    static class substring extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 3);
            TextComponent component = check(args.arg1()).getObject();
            int bound1 = args.checkint(2);
            int bound2 = args.optint(3, -1);
            TextComponent returned;
            
            if(bound2 > 0) {
                returned = component.substring(bound1, bound2);
            } else {
                returned = component.substring(bound1);
            }
            
            if(returned instanceof Text) {
                return TextLib.create((Text) returned);
            } else {
                return create(returned);
            }
        }
    }
}
