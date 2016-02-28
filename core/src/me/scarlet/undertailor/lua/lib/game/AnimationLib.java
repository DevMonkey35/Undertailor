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

import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.Animation;
import me.scarlet.undertailor.gfx.AnimationData;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.wrappers.AnimationSetWrapper;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class AnimationLib extends LuaLibrary {
    
    public static LuaObjectValue<AnimationData> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_ANIMATION);
    }
    
    public static LuaObjectValue<AnimationData> create(AnimationData value) {
        return LuaObjectValue.of(value, Lua.TYPENAME_ANIMATION, LuaLibrary.asMetatable(Lua.LIB_ANIMATION));
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new createAnimation(),
            
            new getAnimationName(),
            new getOffset(),
            new setOffset(),
            new getRuntime(),
            new setRuntime(),
            new isPlaying(),
            new isLooping(),
            new setLooping(),
            new play(),
            new pause(),
            new stop(),
            new resume(),
            new drawCurrentFrame()
    };
    
    public AnimationLib() {
        super("animation", COMPONENTS);
    }
    
    static class getAnimationName extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            AnimationData data = check(args.arg1()).getObject();
            return LuaValue.valueOf(data.getReferenceAnimation().getName());
        }
    }
    
    static class createAnimation extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            String setName = args.checkjstring(1);
            String animName = args.checkjstring(2);
            AnimationSetWrapper wrapper = Undertailor.getAnimationManager().getAnimation(setName);
            if(wrapper == null) {
                return LuaValue.NIL;
            }
            
            Animation<?> anim = wrapper.getReference().getAnimation(animName);
            if(anim == null) {
                return LuaValue.NIL;
            }
            
            return create(new AnimationData(wrapper, anim));
        }
    }
    
    // object methods / metatable
    
    static class getOffset extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            AnimationData anim = check(args.arg1()).getObject();
            Vector2 pos = anim.getOffset();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(pos.x),
                    LuaValue.valueOf(pos.y)});
        }
    }
    
    static class setOffset extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 3);
            
            AnimationData anim = check(args.arg1()).getObject();
            Vector2 offset = anim.getOffset();
            float x = (float) args.optdouble(2, offset.x);
            float y = (float) args.optdouble(3, offset.y);
            
            anim.setOffset(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class getRuntime extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            AnimationData animation = check(args.arg(1)).getObject();
            return LuaValue.valueOf(animation.getRuntime());
        }
    }
    
    static class setRuntime extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            AnimationData animation = check(args.arg(1)).getObject();
            animation.setRuntime(args.checkdouble(2));
            return LuaValue.NIL;
        }
    }
    
    static class isPlaying extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            AnimationData animation = check(args.arg(1)).getObject();
            return LuaValue.valueOf(animation.isPlaying());
        }
    }
    
    static class play extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            AnimationData animation = check(args.arg(1)).getObject();
            animation.play();
            return LuaValue.NIL;
        }
    }
    
    static class pause extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            AnimationData animation = check(args.arg(1)).getObject();
            animation.pause();
            return LuaValue.NIL;
        }
    }
    
    static class stop extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            AnimationData animation = check(args.arg(1)).getObject();
            animation.stop();
            return LuaValue.NIL;
        }
    }
    
    static class resume extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            AnimationData animation = check(args.arg(1)).getObject();
            animation.resume();
            return LuaValue.NIL;
        }
    }
    
    static class isLooping extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            AnimationData animation = check(args.arg(1)).getObject();
            return LuaValue.valueOf(animation.isLooping());
        }
    }
    
    static class setLooping extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            AnimationData animation = check(args.arg(1)).getObject();
            animation.setLooping(args.checkboolean(2));
            return LuaValue.NIL;
        }
    }
    
    static class getSpriteSetName extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            AnimationData animation = check(args.arg(1)).getObject();
            return LuaValue.valueOf(animation.getSpriteSetName());
        }
    }
    
    static class setSpriteSetName extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            AnimationData animation = check(args.arg(1)).getObject();
            animation.setSpriteSetName(args.checkjstring(2));
            return LuaValue.NIL;
        }
    }
    
    static class drawCurrentFrame extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 3, 5);
            
            AnimationData animation = check(args.arg(1)).getObject();
            float posX = (float) args.checkdouble(2);
            float posY = (float) args.checkdouble(3);
            float scale = args.isnil(4) ? 2F : (float) args.checkdouble(4);
            float rotation = args.isnil(5) ? 0F : (float) args.checkdouble(5);
            
            animation.drawCurrentFrame(posX, posY, scale, rotation);
            return LuaValue.NIL;
        }
    }
}
