package me.scarlet.undertailor.lua.lib.meta;

import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.Animation;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.overworld.WorldObject;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.wrappers.AnimationSetWrapper;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaWorldObjectMeta extends LuaLibrary {
    
    public static LuaObjectValue<WorldObject> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_WORLDOBJECT);
    }
    
    public static LuaObjectValue<WorldObject> create(WorldObject value) {
        return LuaObjectValue.of(value, Lua.TYPENAME_WORLDOBJECT, Lua.META_WORLDOBJECT);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new getID(),
            new getZ(),
            new setZ(),
            new getVelocity(),
            new setVelocity(),
            new getPosition(),
            new setPosition(),
            new getAnimation(),
            new setAnimation(),
            new getBoundingBox(),
            new getScale(),
            new setScale(),
            new canCollide(),
            new setCanCollide(),
            new isVisible(),
            new setVisible(),
            new isSolid(),
            new setSolid(),
            new getRoom(),
            new removeFromRoom(),
            new isFocusCollide(),
            new setFocusCollide(),
            new isPersisting(),
            new setPersisting()
    };
    
    public LuaWorldObjectMeta() {
        super(null, COMPONENTS);
    }
    
    static class getID extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldObject object = check(args.arg1()).getObject();
            return LuaValue.valueOf(object.getId());
        }
    }
    
    static class getZ extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldObject object = check(args.arg1()).getObject();
            return LuaValue.valueOf(object.getZ());
        }
    }
    
    static class setZ extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            WorldObject object = check(args.arg1()).getObject();
            int z = args.checkint(2);
            object.setZ(z);
            return LuaValue.NIL;
        }
    }
    
    static class getVelocity extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldObject object = check(args.arg1()).getObject();
            Vector2 vel = object.getVelocity();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(vel.x),
                    LuaValue.valueOf(vel.y)});
        }
    }
    
    static class setVelocity extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 3);
            
            WorldObject object = check(args.arg(1)).getObject();
            Vector2 vel = object.getVelocity();
            float x = new Float(args.optdouble(2, vel.x));
            float y = new Float(args.optdouble(3, vel.y));
            
            object.setVelocity(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class getPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldObject object = check(args.arg1()).getObject();
            Vector2 pos = object.getPosition();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(pos.x),
                    LuaValue.valueOf(pos.y)});
        }
    }
    
    static class setPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 3);
            
            WorldObject object = check(args.arg1()).getObject();
            Vector2 pos = object.getPosition();
            float x = new Float(args.optdouble(2, pos.x));
            float y = new Float(args.optdouble(3, pos.y));
            
            object.setPosition(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class getAnimation extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldObject object = check(args.arg1()).getObject();
            Animation<?> anim = object.getCurrentAnimation();
            if(anim == null) {
                return LuaValue.NIL;
            } else {
                return LuaUtil.asVarargs(LuaValue.valueOf(anim.getParentSet().getName()), LuaValue.valueOf(anim.getName()));
            }
        }
    }
    
    static class setAnimation extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 3, 4);
            
            WorldObject object = check(args.arg(1)).getObject();
            String setName = args.checkjstring(2);
            String animName = args.checkjstring(3);
            int frame = args.isnil(4) ? 0 : args.checkint(4);
            
            if(setName == null) {
                object.setCurrentAnimation(null, null, 0);
            } else {
                AnimationSetWrapper set = Undertailor.getAnimationManager().getRoomObject(setName);
                object.setCurrentAnimation(set, set.getReference().getAnimation(animName), frame);
            }
            
            return LuaValue.NIL;
        }
    }
    
    static class getBoundingBox extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldObject object = check(args.arg1()).getObject();
            return LuaBoundingBoxMeta.create(object.getBoundingBox());
        }
    }
    
    static class getScale extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldObject object = check(args.arg1()).getObject();
            return LuaValue.valueOf(object.getScale());
        }
    }
    
    static class setScale extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            WorldObject object = check(args.arg1()).getObject();
            float scale = new Float(args.checkdouble(2));
            object.setScale(scale);
            return LuaValue.NIL;
        }
    }
    
    static class canCollide extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldObject object = check(args.arg1()).getObject();
            return LuaValue.valueOf(object.canCollide());
        }
    }
    
    static class setCanCollide extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            WorldObject object = check(args.arg1()).getObject();
            boolean flag = args.checkboolean(2);
            object.setCanCollide(flag);
            return LuaValue.NIL;
        }
    }
    
    static class isVisible extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldObject object = check(args.arg1()).getObject();
            return LuaValue.valueOf(object.isVisible());
        }
    }
    
    static class setVisible extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            WorldObject object = check(args.arg1()).getObject();
            boolean flag = args.checkboolean(2);
            object.setVisible(flag);
            return LuaValue.NIL;
        }
    }
    
    static class isSolid extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldObject object = check(args.arg1()).getObject();
            return LuaValue.valueOf(object.isSolid());
        }
    }
    
    static class setSolid extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            WorldObject object = check(args.arg1()).getObject();
            boolean flag = args.checkboolean(2);
            object.setSolid(flag);
            return LuaValue.NIL;
        }
    }
    
    static class getRoom extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldObject object = check(args.arg1()).getObject();
            if(object.getRoom() != null) {
                return LuaWorldRoomMeta.create(object.getRoom());
            } else {
                return LuaValue.NIL;
            }
        }
    }
    
    static class removeFromRoom extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldObject object = check(args.arg1()).getObject();
            object.removeFromRoom();
            return LuaValue.NIL;
        }
    }
    
    static class isFocusCollide extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldObject object = check(args.arg1()).getObject();
            return LuaValue.valueOf(object.focusCollide());
        }
    }
    
    static class setFocusCollide extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            WorldObject object = check(args.arg1()).getObject();
            boolean flag = args.checkboolean(2);
            object.setFocusCollide(flag);
            return LuaValue.NIL;
        }
    }
    
    static class isPersisting extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldObject object = check(args.arg1()).getObject();
            return LuaValue.valueOf(object.isPersisting());
        }
    }
    
    static class setPersisting extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            WorldObject object = check(args.arg1()).getObject();
            boolean flag = args.checkboolean(2);
            object.setPersisting(flag);
            return LuaValue.NIL;
        }
    }
}
