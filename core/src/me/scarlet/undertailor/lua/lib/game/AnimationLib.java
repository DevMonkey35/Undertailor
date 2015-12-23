package me.scarlet.undertailor.lua.lib.game;

import com.badlogic.gdx.utils.TimeUtils;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.Animation;
import me.scarlet.undertailor.lua.LuaAnimation;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.wrappers.AnimationSetWrapper;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class AnimationLib extends TwoArgFunction {
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable animation = new LuaTable();
        
        animation.set("getAnimation", new _getAnimation());
        animation.set("getStartTime", new _getStartTime());
        animation.set("play", new _play());
        animation.set("stop", new _stop());
        animation.set("drawCurrentFrame", new _drawCurrentFrame());
        
        if(LuaAnimation.METATABLE == null) {
            LuaAnimation.METATABLE = LuaValue.tableOf(new LuaValue[] { INDEX, animation });
        }
        
        env.set("animation", animation);
        return animation;
    }
    
    static class _getAnimation extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            String setName = arg1.checkjstring();
            String animName = arg2.checkjstring();
            AnimationSetWrapper wrapper = Undertailor.getAnimationManager().getObject(setName);
            if(wrapper == null) {
                return LuaValue.NIL;
            }
            
            Animation<?> anim = wrapper.getReference().getAnimation(animName);
            if(anim == null) {
                return LuaValue.NIL;
            }
            
            return new LuaAnimation(anim);
        }
    }
    
    static class _getStartTime extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            Animation<?> animation = LuaAnimation.checkAnimation(arg).getAnimation();
            Undertailor.getAnimationManager().getObject(animation.getParentSet().getName()).getReference();
            return LuaValue.valueOf(animation.getStartTime());
        }
    }
    
    static class _play extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            Animation<?> animation = LuaAnimation.checkAnimation(arg1).getAnimation();
            Undertailor.getAnimationManager().getObject(animation.getParentSet().getName()).getReference();
            long startTime = arg2.isnil() ? TimeUtils.millis() : arg2.checklong();
            animation.start(startTime);
            return LuaValue.NIL;
        }
    }
    
    static class _stop extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            Animation<?> animation = LuaAnimation.checkAnimation(arg1).getAnimation();
            Undertailor.getAnimationManager().getObject(animation.getParentSet().getName()).getReference();
            animation.stop();
            return LuaValue.NIL;
        }
    }
    
    static class _drawCurrentFrame extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 3, 5);
            
            Animation<?> animation = LuaAnimation.checkAnimation(args.arg(1)).getAnimation();
            Undertailor.getAnimationManager().getObject(animation.getParentSet().getName()).getReference();
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
