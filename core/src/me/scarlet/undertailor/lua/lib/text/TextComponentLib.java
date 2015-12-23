package me.scarlet.undertailor.lua.lib.text;

import com.badlogic.gdx.graphics.Color;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaColor;
import me.scarlet.undertailor.lua.LuaSound;
import me.scarlet.undertailor.lua.LuaStyle;
import me.scarlet.undertailor.lua.LuaText;
import me.scarlet.undertailor.lua.LuaTextComponent;
import me.scarlet.undertailor.texts.Font;
import me.scarlet.undertailor.texts.Style;
import me.scarlet.undertailor.texts.TextComponent;
import me.scarlet.undertailor.texts.TextComponent.Text;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.wrappers.SoundWrapper;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class TextComponentLib extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable component = new LuaTable();
        component.set("newComponent", new _newComponent());
        component.set("substring", new _substring());
        component.set("getDelay", new _getDelay());
        component.set("getSound", new _getSound());
        component.set("getSpeed", new _getSpeed());
        component.set("getStyle", new _getStyle());
        component.set("getColor", new _getColor());
        component.set("getText", new _getText());
        
        if(LuaTextComponent.METATABLE == null) {
            LuaTextComponent.METATABLE = LuaValue.tableOf(new LuaValue[] {INDEX, component});
        }
        
        LuaUtil.iterateTable(component, args -> {
            if(!args.arg(1).toString().equals("newComponent")) {
                env.set(args.arg(1), args.arg(2));
            }
        });
        
        env.set("component", component);
        return component;
    }
    
    static class _newComponent extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            // text.component.newComponent(text, fontName, styleName, color, soundName, speed, waitTime)
            if(args.narg() < 1 || args.narg() > 7) {
                throw new LuaError("arguments insufficient or overflowing (min 1, max 8)");
            }
            
            String text = args.arg(1).checkstring().tojstring();
            Font font = args.arg(2).isnil() ? null : Undertailor.getFontManager().getObject(args.arg(2).checkstring().tojstring());
            Style style = args.arg(3).isnil() ? null : Undertailor.getStyleManager().getObject(args.arg(3).checkstring().tojstring());
            Color color = args.arg(4).isnil() ? null : LuaColor.checkcolor(args.arg(4)).getColor();
            SoundWrapper sound = args.arg(5).isnil() ? null : Undertailor.getAudioManager().getSound(args.arg(5).checkstring().tojstring());
            int speed = args.arg(6).isnil() ? TextComponent.DEFAULT_SPEED : args.arg(6).checkint();
            int segsize = args.arg(7).isnil() ? 1 : args.arg(7).checkint();
            float wait = args.arg(8).isnil() ? 0F : new Float(args.arg(8).checkdouble());
            
            if(text.trim().isEmpty()) {
                throw new LuaError("bad argument: text cannot be empty or only have whitespace characters");
            }
            
            return new LuaTextComponent(new TextComponent(text, font, style, color, sound, speed, segsize, wait));
        }
    }
    
    static class _getText extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String text = LuaTextComponent.checkTextComponent(arg).getTextComponent().getText();
            if(text == null) {
                return LuaValue.NIL;
            }
            
            return LuaValue.valueOf(text);
        }
    }
    
    static class _getSound extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            TextComponent component = LuaTextComponent.checkTextComponent(arg).getTextComponent();
            if(component.getSound() == null) {
                return LuaValue.NIL;
            }
            
            return new LuaSound(component.getSound());
        }
    }
    
    static class _getSpeed extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            TextComponent component = LuaTextComponent.checkTextComponent(arg).getTextComponent();
            return LuaValue.valueOf(component.getSpeed());
        }
    }
    
    static class _getDelay extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            TextComponent component = LuaTextComponent.checkTextComponent(arg).getTextComponent();
            return LuaValue.valueOf(component.getDelay());
        }
    }
    
    static class _getStyle extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            TextComponent component = LuaTextComponent.checkTextComponent(arg).getTextComponent();
            if(component.getStyle() == null) {
                return LuaValue.NIL;
            }
            
            return component.getStyle() instanceof LuaStyle ? (LuaStyle) component.getStyle() : new LuaStyle(component.getStyle());
        }
    }
    
    static class _getColor extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            TextComponent component = LuaTextComponent.checkTextComponent(arg).getTextComponent();
            if(component.getColor() == null) {
                return LuaValue.NIL;
            }
            
            return new LuaColor(component.getColor());
        }
    }
    
    static class _getSegmentSize extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            TextComponent component = LuaTextComponent.checkTextComponent(arg).getTextComponent();
            return LuaValue.valueOf(component.getSegmentSize());
        }
    }
    
    static class _substring extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
            LuaTextComponent component = LuaTextComponent.checkTextComponent(arg1);
            int bound1 = arg2.checkint();
            int bound2 = arg3.isnil() ? -1 : arg3.checkint();
            TextComponent returned;
            
            if(bound2 > 0) {
                returned = component.getTextComponent().substring(bound1, bound2);
            } else {
                returned = component.getTextComponent().substring(bound1);
            }
            
            if(returned instanceof Text) {
                return new LuaText((Text) returned);
            } else {
                return new LuaTextComponent(returned);
            }
        }
    }
}
