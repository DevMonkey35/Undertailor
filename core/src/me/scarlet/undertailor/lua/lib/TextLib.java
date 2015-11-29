package me.scarlet.undertailor.lua.lib;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaColor;
import me.scarlet.undertailor.lua.LuaDisplayMeta;
import me.scarlet.undertailor.lua.LuaSound;
import me.scarlet.undertailor.lua.LuaText;
import me.scarlet.undertailor.lua.LuaTextComponent;
import me.scarlet.undertailor.lua.lib.text.TextComponentLib;
import me.scarlet.undertailor.texts.Font;
import me.scarlet.undertailor.texts.Style;
import me.scarlet.undertailor.texts.TextComponent;
import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;
import me.scarlet.undertailor.texts.TextComponent.Text;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class TextLib extends TwoArgFunction {
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable text = new LuaTable();
        text.set("getComponentAtCharacter", new _getComponentAtCharacter());
        text.set("newDisplayMeta", new _newDisplayMeta());
        text.set("addComponents", new _addComponents());
        text.set("addComponent", new _addComponent());
        text.set("getMembers", new _getMembers());
        text.set("newText", new _newText());
        new TextComponentLib().call(LuaValue.valueOf(""), text);
        
        if(LuaText.METATABLE == null) LuaText.METATABLE = LuaValue.tableOf(new LuaValue[] {LuaValue.INDEX, text});
        env.set("text", text);
        return text;
    }
    
    static class _newDisplayMeta extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if(args.narg() < 0 || args.narg() > 5) {
                throw new LuaError("function arguments insufficient or overflowing (min 0, max 5)");
            }
            
            float offX = new Float(args.optdouble(1, 0F));
            float offY = new Float(args.optdouble(2, 0F));
            float scaleX = new Float(args.optdouble(1, 1F));
            float scaleY = new Float(args.optdouble(1, 1F));
            Color color = args.arg(5).isnil() ? Color.WHITE : LuaColor.checkcolor(args.arg(5)).getColor();
            
            if(scaleX < 0F) {
                scaleX = 0F;
            }
            
            if(scaleY < 0F) {
                scaleY = 0F;
            }
            
            return LuaValue.varargsOf(new LuaValue[] {new LuaDisplayMeta(new DisplayMeta(offX, offY, scaleX, scaleY, color))});
        }
    }
    
    static class _newText extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if(args.narg() < 1 || args.narg() > 6) {
                throw new LuaError("arguments insufficient or overflowing (min 1, max 6)");
            }
            
            Font font = Undertailor.getFontManager().getFont(args.checkjstring(1));
            Style style = args.isnil(2) ? null : Undertailor.getStyleManager().getStyle(args.arg(2).checkjstring());
            Color color = args.isnil(3) ? null : LuaColor.checkcolor(args.arg(3)).getColor();
            Sound sound = args.arg(4).isnil() ? null : LuaSound.checkSound(args.arg(4)).getSound();
            int speed = args.optint(5, TextComponent.DEFAULT_SPEED);;
            float delay = new Float(args.optdouble(6, 0F));
            
            return new LuaText(new Text(font, style, color, sound, speed, delay));
        }
    }
    
    static class _addComponent extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            Text text = LuaText.checkText(arg1).getText();
            TextComponent added = LuaTextComponent.checkTextComponent(arg2, true).getTextComponent();
            
            text.addComponents(added);
            return LuaValue.NIL;
        }
    }
    
    static class _addComponents extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if(args.narg() < 2) {
                throw new LuaError("arguments insufficient or overflowing (min 2)");
            }
            
            Text text = LuaText.checkText(args.arg(1)).getText();
            TextComponent[] components = new TextComponent[args.narg() - 1];
            for(int i = 2; i <= args.narg(); i++) {
                components[i - 2] = LuaTextComponent.checkTextComponent(args.arg(i), true).getTextComponent();
            }
            
            text.addComponents(components);
            return LuaValue.NIL;
        }
    }
    
    static class _getComponentAtCharacter extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            Text text = LuaText.checkText(arg1).getText();
            int index = arg2.checkint();
            try {
                TextComponent component = text.getComponentAtCharacter(index);
                if(component == null) {
                    return LuaValue.NIL;
                }
                
                return new LuaTextComponent(component);
            } catch(Exception e) {
                throw new LuaError("failed to execute internal method \"getComponentAtCharacter\": " + e.getMessage());
            }
        }
    }
    
    static class _getMembers extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            LuaTable returned = new LuaTable();
            Text text = LuaText.checkText(arg).getText();
            for(int i = 0; i < text.getMembers().size(); i++) {
                returned.set(i, new LuaTextComponent(text.getMembers().get(i)));
            }
            
            return returned;
        }
    }
    
    static class _drawText extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if(args.narg() < 1) {
                throw new LuaError("arguments insufficient or overflowing (min 3, max 6)");
            }
            
            Text text = LuaText.checkText(args.arg(1)).getText();
            float posX = new Float(args.arg(2).checkdouble());
            float posY = new Float(args.arg(3).checkdouble());
            float scaleX = new Float(args.optdouble(4, 1.0));
            float scaleY = new Float(args.optdouble(5, scaleX));
            float alpha = new Float(args.optdouble(6, 1.0));
            Undertailor.getFontManager().write(text, posX, posY, scaleX, scaleY, alpha);
            return LuaValue.NIL;
        }
    }
}
