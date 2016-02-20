/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor.lua.lib;

import com.badlogic.gdx.graphics.Color;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.audio.SoundWrapper;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.lib.game.AudioLib;
import me.scarlet.undertailor.lua.lib.meta.LuaStyleMeta;
import me.scarlet.undertailor.lua.lib.text.TextComponentLib;
import me.scarlet.undertailor.texts.Font;
import me.scarlet.undertailor.texts.Style;
import me.scarlet.undertailor.texts.TextComponent;
import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;
import me.scarlet.undertailor.texts.Text;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class TextLib extends LuaLibrary {
    
    public static LuaObjectValue<Text> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_TEXT);
    }
    
    public static LuaObjectValue<Text> create(Text text) {
        return LuaObjectValue.of(text, Lua.TYPENAME_TEXT, LuaLibrary.asMetatable(Lua.LIB_TEXT));
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new newDisplayMeta(),
            new newText(),
            new drawText(),
            new addComponent(),
            new addComponents(),
            new getComponentAtCharacter(),
            new getMembers(),
            new TextComponentLib()
    };
    
    public TextLib() {
        super("text", COMPONENTS);
    }
    
    static class newDisplayMeta extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 5);
            
            float offX = (float) args.optdouble(1, 0F);
            float offY = (float) args.optdouble(2, 0F);
            float scaleX = (float) args.optdouble(3, 1F);
            float scaleY = (float) args.optdouble(4, 1F);
            Color color = args.arg(5).isnil() ? null : ColorsLib.check(args.arg(5)).getObject();
            
            if(scaleX < 0F) {
                scaleX = 0F;
            }
            
            if(scaleY < 0F) {
                scaleY = 0F;
            }
            
            return LuaStyleMeta.createDisplayMeta(new DisplayMeta(offX, offY, scaleX, scaleY, color));
        }
    }
    
    static class newText extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 7);
            
            Font font = Undertailor.getFontManager().getFont(args.checkjstring(1));
            Style style = args.isnil(2) ? null : Undertailor.getStyleManager().getStyle(args.arg(2).checkjstring());
            Color color = args.isnil(3) ? null : ColorsLib.check(args.arg(3)).getObject();
            SoundWrapper sound = args.arg(4).isnil() ? null : (SoundWrapper) AudioLib.checkSound(args.arg(4)).getObject();
            int speed = args.optint(5, TextComponent.DEFAULT_SPEED);
            int segsize = args.optint(6, 1);
            float delay = (float) args.optdouble(7, 0F);
            
            if(font == null) {
                throw new LuaError("font cannot be nil");
            }
            
            return TextLib.create(Text.builder()
                    .setFont(font)
                    .setStyle(style)
                    .setColor(color)
                    .setTextSound(sound)
                    .setSpeed(speed)
                    .setSegmentSize(segsize)
                    .setDelay(delay)
                    .build());//new Text(font, style, color, sound, speed, segsize, delay));
        }
    }
    
    static class addComponent extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            Text text = TextLib.check(args.arg(1)).getObject();
            TextComponent added = TextComponentLib.check(args.arg(2)).getObject();
            
            text.addComponents(added);
            return args.arg(1);
        }
    }
    
    static class addComponents extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, -1);
            
            Text text = TextLib.check(args.arg(1)).getObject();
            TextComponent[] components = new TextComponent[args.narg() - 1];
            for(int i = 2; i <= args.narg(); i++) {
                components[i - 2] = TextComponentLib.check(args.arg(i)).getObject();
            }
            
            text.addComponents(components);
            return args.arg(1);
        }
    }
    
    static class getComponentAtCharacter extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            Text text = TextLib.check(args.arg(1)).getObject();
            int index = args.checkint(2);
            try {
                TextComponent component = text.getComponentAtCharacter(index - 1); // -1 since lua isn't 0-based
                if(component == null) {
                    return LuaValue.NIL;
                }
                
                return TextComponentLib.create(component);
            } catch(Exception e) {
                throw new LuaError("failed to execute internal method \"getComponentAtCharacter\": " + e.getMessage());
            }
        }
    }
    
    static class getMembers extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            LuaTable returned = new LuaTable();
            Text text = TextLib.check(args.arg(1)).getObject();
            for(int i = 0; i < text.getMembers().size(); i++) {
                returned.set(i, TextComponentLib.create(text.getMembers().get(i)));
            }
            
            return returned;
        }
    }
    
    static class drawText extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 3, 6);
            
            Text text = TextLib.check(args.arg(1)).getObject();
            float posX = (float) args.arg(2).checkdouble();
            float posY = (float) args.arg(3).checkdouble();
            float scaleX = (float) args.optdouble(4, 1.0);
            float scaleY = (float) args.optdouble(5, scaleX);
            float alpha = (float) args.optdouble(6, 1.0);
            Undertailor.getFontManager().write(text, posX, posY, scaleX, scaleY, alpha);
            return LuaValue.NIL;
        }
    }
}
