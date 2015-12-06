package me.scarlet.undertailor.lua.lib.game;

import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaWorldRoom;
import me.scarlet.undertailor.overworld.WorldRoom;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class OverworldLib extends TwoArgFunction {
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable overworld = new LuaTable();
        overworld.set("newWorldRoom", new _newWorldRoom());
        overworld.set("newWorldObject", new _newWorldObject());
        overworld.set("isRendering", new _isRendering());
        overworld.set("setRendering", new _setRendering());
        overworld.set("isProcessing", new _isProcessing());
        overworld.set("setProcessing", new _setProcessing());
        overworld.set("isRenderingHitboxes", new _isRenderingHitboxes());
        overworld.set("setRenderingHitboxes", new _setRenderingHitboxes());
        overworld.set("getCameraPosition", new _getCameraPosition());
        overworld.set("setCameraPosition", new _setCameraPosition());
        overworld.set("getCameraZoom", new _getCameraZoom());
        overworld.set("setCameraZoom", new _setCameraZoom());
        overworld.set("getCurrentRoom", new _getCurrentRoom());
        overworld.set("setCurrentRoom", new _setCurrentRoom());
        
        env.set("overworld", overworld);
        return overworld;
    }
    
    static class _newWorldRoom extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            try {
                return new LuaWorldRoom(Undertailor.getRoomManager().getObject(arg.checkjstring()));
            } catch(Exception e) {
                e.printStackTrace();
                throw new LuaError("failed to load room: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }
    }
    
    static class _newWorldObject extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            return Undertailor.getOverworldController().getObjectLoader().newWorldObject(arg.tojstring());
        }
    }
    
    static class _isRendering extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaValue.valueOf(Undertailor.getOverworldController().isRendering());
        }
    }
    
    static class _setRendering extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            boolean flag = arg.checkboolean();
            Undertailor.getOverworldController().setRendering(flag);
            return LuaValue.NIL;
        }
    }
    
    static class _isProcessing extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaValue.valueOf(Undertailor.getOverworldController().isProcessing());
        }
    }
    
    static class _setProcessing extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            boolean flag = arg.checkboolean();
            Undertailor.getOverworldController().setProcessing(flag);
            return LuaValue.NIL;
        }
    }
    
    static class _isRenderingHitboxes extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaValue.valueOf(Undertailor.getOverworldController().isRenderingHitboxes());
        }
    }
    
    static class _setRenderingHitboxes extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            boolean flag = arg.checkboolean();
            Undertailor.getOverworldController().setRenderingHitboxes(flag);
            return LuaValue.NIL;
        }
    }
    
    static class _getCameraPosition extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            Vector2 position = Undertailor.getOverworldController().getCameraPosition();
            return LuaValue.varargsOf(new LuaValue[] {LuaValue.valueOf(position.x), LuaValue.valueOf(position.y)});
        }
    }
    
    static class _setCameraPosition extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            Vector2 position = Undertailor.getOverworldController().getCameraPosition();
            float posX = args.isnil(1) ? position.x : new Float(args.checkdouble(1));
            float posY = args.isnil(2) ? position.y : new Float(args.checkdouble(2));
            Undertailor.getOverworldController().setCameraPosition(posX, posY);
            
            return LuaValue.NIL;
        }
    }
    
    static class _getCameraZoom extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaValue.valueOf(Undertailor.getOverworldController().getCameraZoom());
        }
    }
    
    static class _setCameraZoom extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            float zoom = new Float(arg.checkdouble());
            Undertailor.getOverworldController().setCameraZoom(zoom);
            return LuaValue.NIL;
        }
    }
    
    static class _getCurrentRoom extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return new LuaWorldRoom(Undertailor.getOverworldController().getCurrentRoom());
        }
    }
    
    static class _setCurrentRoom extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            WorldRoom room = LuaWorldRoom.checkWorldRoom(arg1).getRoom();
            boolean transitions = arg2.isnil() ? true : arg2.checkboolean();
            Undertailor.getOverworldController().setCurrentRoom(room, transitions);
            return LuaValue.NIL;
        }
    }
}
