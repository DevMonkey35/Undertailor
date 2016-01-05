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

package me.scarlet.undertailor.lua.lib.game;

import com.badlogic.gdx.utils.TimeUtils;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.Animation;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.wrappers.AnimationSetWrapper;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class AnimationLib extends LuaLibrary {
    
    public static LuaObjectValue<Animation<?>> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_ANIMATION);
    }
    
    public static LuaObjectValue<Animation<?>> create(Animation<?> value) {
        return LuaObjectValue.of(value, Lua.TYPENAME_ANIMATION, LuaLibrary.asMetatable(Lua.LIB_ANIMATION));
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new getAnimation(),
            new getStartTime(),
            new play(),
            new stop(),
            new drawCurrentFrame()
    };
    
    public AnimationLib() {
        super("animation", COMPONENTS);
    }
    
    static class getAnimation extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            String setName = args.checkjstring(1);
            String animName = args.checkjstring(2);
            AnimationSetWrapper wrapper = Undertailor.getAnimationManager().getStyle(setName);
            if(wrapper == null) {
                return LuaValue.NIL;
            }
            
            Animation<?> anim = wrapper.getReference().getAnimation(animName);
            if(anim == null) {
                return LuaValue.NIL;
            }
            
            return create(anim);
        }
    }
    
    static class getStartTime extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Animation<?> animation = check(args.arg(1)).getObject();
            Undertailor.getAnimationManager().getStyle(animation.getParentSet().getName()).getReference();
            return LuaValue.valueOf(animation.getStartTime());
        }
    }
    
    static class play extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            Animation<?> animation = check(args.arg(1)).getObject();
            Undertailor.getAnimationManager().getStyle(animation.getParentSet().getName()).getReference();
            long startTime = args.isnil(2) ? TimeUtils.millis() : args.checklong(2);
            animation.start(startTime);
            return LuaValue.NIL;
        }
    }
    
    static class stop extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Animation<?> animation = check(args.arg(1)).getObject();
            Undertailor.getAnimationManager().getStyle(animation.getParentSet().getName()).getReference();
            animation.stop();
            return LuaValue.NIL;
        }
    }
    
    static class drawCurrentFrame extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 3, 5);
            
            Animation<?> animation = check(args.arg(1)).getObject();
            Undertailor.getAnimationManager().getStyle(animation.getParentSet().getName()).getReference();
            float posX = new Float(args.checkdouble(2));
            float posY = new Float(args.checkdouble(3));
            System.out.println(posX + ", " + posY);
            float scale = args.isnil(4) ? 2F : new Float(args.checkdouble(4));
            float rotation = args.isnil(5) ? 0F : new Float(args.checkdouble(5));
            
            animation.drawCurrentFrame(posX, posY, scale, rotation);
            return LuaValue.NIL;
        }
    }
}
