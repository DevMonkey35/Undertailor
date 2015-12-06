package me.scarlet.undertailor.lua.lib.meta;

import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.Animation;
import me.scarlet.undertailor.lua.LuaBoundingBox;
import me.scarlet.undertailor.lua.LuaWorldObject;
import me.scarlet.undertailor.lua.LuaWorldRoom;
import me.scarlet.undertailor.overworld.WorldObject;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.wrappers.AnimationSetWrapper;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaWorldObjectMeta extends LuaTable {
    
    public static void prepareMetatable() {
        if(LuaWorldObject.METATABLE == null) {
            LuaWorldObject.METATABLE = LuaValue.tableOf(new LuaValue[] {INDEX, new LuaWorldObjectMeta()});
        }
    }
    
    public LuaWorldObjectMeta() {
        this.set("getZ", new _getZ());
        this.set("setZ", new _setZ());
        this.set("getID", new _getID());
        this.set("getPosition", new _getPosition());
        this.set("setPosition", new _setPosition());
        this.set("getAnimation", new _getAnimation());
        this.set("setAnimation", new _setAnimation());
        this.set("getBoundingBox", new _getBoundingBox());
        this.set("getScale", new _getScale());
        this.set("setScale", new _setScale());
        this.set("canCollide", new _canCollide());
        this.set("setCanCollide", new _setCanCollide());
        this.set("focusCollide", new _focusCollide());
        this.set("setFocusCollide", new _setFocusCollide());
        this.set("isVisible", new _isVisible());
        this.set("setVisible", new _setVisible());
        this.set("isSolid", new _isSolid());
        this.set("setSolid", new _setSolid());
        this.set("getRoom", new _getRoom());
        this.set("removeFromRoom", new _removeFromRoom());
    }
    
    static class _getID extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg).getWorldObject();
            return LuaValue.valueOf(object.getId());
        }
    }
    
    static class _getZ extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg).getWorldObject();
            return LuaValue.valueOf(object.getZ());
        }
    }
    
    static class _setZ extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg1).getWorldObject();
            int z = arg2.checkint();
            object.setZ(z);
            return LuaValue.NIL;
        }
    }
    
    static class _getPosition extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            WorldObject object = LuaWorldObject.checkWorldObject(args.arg1()).getWorldObject();
            Vector2 pos = object.getPosition();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(pos.x),
                    LuaValue.valueOf(pos.y)});
        }
    }
    
    static class _setPosition extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg1).getWorldObject();
            Vector2 pos = object.getPosition();
            float x = arg2.isnil() ? pos.x : new Float(arg2.checkdouble());
            float y = arg3.isnil() ? pos.y : new Float(arg3.checkdouble());
            
            object.setPosition(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class _getAnimation extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            WorldObject object = LuaWorldObject.checkWorldObject(args.arg1()).getWorldObject();
            Animation<?> anim = object.getCurrentAnimation();
            if(anim == null) {
                return LuaValue.NIL;
            } else {
                return LuaValue.varargsOf(new LuaValue[] {LuaValue.valueOf(anim.getParentSet().getName()), LuaValue.valueOf(anim.getName())});
            }
        }
    }
    
    static class _setAnimation extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg1).getWorldObject();
            String setName = arg2.checkjstring();
            String animName = arg3.checkjstring();
            
            if(setName == null) {
                object.setCurrentAnimation(null, null);
            } else {
                AnimationSetWrapper set = Undertailor.getAnimationManager().getObject(setName);
                object.setCurrentAnimation(set, set.getReference().getAnimation(animName));
            }
            
            return LuaValue.NIL;
        }
    }
    
    static class _getBoundingBox extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg).getWorldObject();
            return new LuaBoundingBox(object.getBoundingBox());
        }
    }
    
    static class _getScale extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg).getWorldObject();
            return LuaValue.valueOf(object.getScale());
        }
    }
    
    static class _setScale extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg1).getWorldObject();
            float scale = new Float(arg2.checkdouble());
            object.setScale(scale);
            return LuaValue.NIL;
        }
    }
    
    static class _canCollide extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg).getWorldObject();
            return LuaValue.valueOf(object.canCollide());
        }
    }
    
    static class _setCanCollide extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg1).getWorldObject();
            boolean flag = arg2.checkboolean();
            object.setCanCollide(flag);
            return LuaValue.NIL;
        }
    }
    
    static class _isVisible extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg).getWorldObject();
            return LuaValue.valueOf(object.isVisible());
        }
    }
    
    static class _setVisible extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg1).getWorldObject();
            boolean flag = arg2.checkboolean();
            object.setVisible(flag);
            return LuaValue.NIL;
        }
    }
    
    static class _isSolid extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg).getWorldObject();
            return LuaValue.valueOf(object.isSolid());
        }
    }
    
    static class _setSolid extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg1).getWorldObject();
            boolean flag = arg2.checkboolean();
            object.setSolid(flag);
            return LuaValue.NIL;
        }
    }
    
    static class _getRoom extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg).getWorldObject();
            if(object.getRoom() != null) {
                return new LuaWorldRoom(object.getRoom());
            } else {
                return LuaValue.NIL;
            }
        }
    }
    
    static class _removeFromRoom extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg).getWorldObject();
            object.removeFromRoom();
            return LuaValue.NIL;
        }
    }
    
    static class _focusCollide extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg).getWorldObject();
            return LuaValue.valueOf(object.focusCollide());
        }
    }
    
    static class _setFocusCollide extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            WorldObject object = LuaWorldObject.checkWorldObject(arg1).getWorldObject();
            boolean flag = arg2.checkboolean();
            object.setFocusCollide(flag);
            return LuaValue.NIL;
        }
    }
}
