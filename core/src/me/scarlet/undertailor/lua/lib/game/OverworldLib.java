package me.scarlet.undertailor.lua.lib.game;

import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.util.LuaUtil;
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
        this.set("newWorldObject", new _newWorldObject());
        this.set("isRendering", new _isRendering());
        this.set("setRendering", new _setRendering());
        this.set("isProcessing", new _isProcessing());
        this.set("setProcessing", new _setProcessing());
        this.set("isRenderingHitboxes", new _isRenderingHitboxes());
        this.set("setRenderingHitboxes", new _setRenderingHitboxes());
        this.set("getCameraPosition", new _getCameraPosition());
        this.set("setCameraPosition", new _setCameraPosition());
        this.set("getCameraZoom", new _getCameraZoom());
        this.set("setCameraZoom", new _setCameraZoom());
        
        env.set("overworld", overworld);
        return overworld;
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
    
    // TODO setroom/getroom
}
