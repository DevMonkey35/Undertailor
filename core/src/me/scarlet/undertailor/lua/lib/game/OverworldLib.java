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
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.impl.WorldRoomImplementable;
import me.scarlet.undertailor.lua.lib.meta.LuaRoomMapMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaWorldRoomMeta;
import me.scarlet.undertailor.manager.ScriptManager;
import me.scarlet.undertailor.overworld.WorldRoom;
import me.scarlet.undertailor.scheduler.LuaTask;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class OverworldLib extends LuaLibrary {
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new newWorldRoom(),
            new newWorldObject(),
            new newWorldMap(),
            new isRendering(),
            new setRendering(),
            new isProcessing(),
            new setProcessing(),
            new isRenderingHitboxes(),
            new setRenderingHitboxes(),
            new getCameraPosition(),
            new setCameraPosition(),
            new getCameraZoom(),
            new setCameraZoom(),
            new getCurrentRoom(),
            new setCurrentRoom(),
            new getCharacterID(),
            new setCharacterID(),
            new setEntryTransition(),
            new setExitTransition(),
            new isCameraFixing(),
            new setCameraFixing()
    };
    
    public OverworldLib() {
        super("overworld", COMPONENTS);
    }
    
    static class newWorldRoom extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            try {
                ScriptManager scriptMan = Undertailor.getScriptManager();
                WorldRoomImplementable impl = scriptMan.getImplementable(WorldRoomImplementable.class);
                return impl.load(args.checkjstring(1), Undertailor.getOverworldController().getRoomLoader().getRoomScript(args.checkjstring(1))).getObjectValue();
            } catch(Exception e) {
                LuaError thrown = new LuaError("failed to load room: " + LuaUtil.formatJavaException(e));
                e.printStackTrace();
                thrown.initCause(e);
                throw thrown;
            }
        }
    }
    
    static class newWorldMap extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            System.out.println("CALLED");
            return LuaRoomMapMeta.create(Undertailor.getOverworldController().getRoomLoader().getRoom(args.checkjstring(1)));
        }
    }
    
    static class newWorldObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, -1);
            
            return Undertailor.getOverworldController().getObjectLoader().newWorldObject(args.checkjstring(1), args.subargs(2));
        }
    }
    
    static class isRendering extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            
            return LuaValue.valueOf(Undertailor.getOverworldController().isRendering());
        }
    }
    
    static class setRendering extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            boolean flag = args.checkboolean(1);
            Undertailor.getOverworldController().setRendering(flag);
            return LuaValue.NIL;
        }
    }
    
    static class isProcessing extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            
            return LuaValue.valueOf(Undertailor.getOverworldController().isProcessing());
        }
    }
    
    static class setProcessing extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            boolean flag = args.checkboolean(1);
            Undertailor.getOverworldController().setProcessing(flag);
            return LuaValue.NIL;
        }
    }
    
    static class isRenderingHitboxes extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            return LuaValue.valueOf(Undertailor.getOverworldController().isRenderingHitboxes());
        }
    }
    
    static class setRenderingHitboxes extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            boolean flag = args.checkboolean(1);
            Undertailor.getOverworldController().setRenderingHitboxes(flag);
            return LuaValue.NIL;
        }
    }
    
    static class isCameraFixing extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            return LuaValue.valueOf(Undertailor.getOverworldController().isCameraFixing());
        }
    }
    
    static class setCameraFixing extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            boolean flag = args.checkboolean(1);
            Undertailor.getOverworldController().setCameraFixing(flag);
            return LuaValue.NIL;
        }
    }
    
    static class getCameraPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            Vector2 position = Undertailor.getOverworldController().getCameraPosition();
            return LuaUtil.asVarargs(LuaValue.valueOf(position.x), LuaValue.valueOf(position.y));
        }
    }
    
    static class setCameraPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            Vector2 position = Undertailor.getOverworldController().getCameraPosition();
            float posX = args.isnil(1) ? position.x : new Float(args.checkdouble(1));
            float posY = args.isnil(2) ? position.y : new Float(args.checkdouble(2));
            Undertailor.getOverworldController().setCameraPosition(posX, posY);
            
            return LuaValue.NIL;
        }
    }
    
    static class getCameraZoom extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            return LuaValue.valueOf(Undertailor.getOverworldController().getCameraZoom());
        }
    }
    
    static class setCameraZoom extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            float zoom = new Float(args.checkdouble(1));
            Undertailor.getOverworldController().setCameraZoom(zoom);
            return LuaValue.NIL;
        }
    }
    
    static class getCurrentRoom extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            return LuaWorldRoomMeta.create(Undertailor.getOverworldController().getCurrentRoom());
        }
    }
    
    static class setCurrentRoom extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 4);
            
            WorldRoom room = LuaWorldRoomMeta.check(args.arg(1)).getObject();
            boolean transitions = args.isnil(2) ? true : args.checkboolean(2);
            String exitpoint = args.isnil(3) ? null : args.checkjstring(3);
            String entrypoint = args.isnil(4) ? null : args.checkjstring(4);
            Undertailor.getOverworldController().setCurrentRoom(room, transitions, exitpoint, entrypoint);
            return LuaValue.NIL;
        }
    }
    
    static class getCharacterID extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            return LuaValue.valueOf(Undertailor.getOverworldController().getCharacterID());
        }
    }
    
    static class setCharacterID extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            long id = args.checklong(1);
            Undertailor.getOverworldController().setCharacterID(id);
            return LuaValue.NIL;
        }
    }
    
    static class setEntryTransition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            LuaTable task = args.checktable(1);
            Undertailor.getOverworldController().setEntryTransition(new LuaTask(task));
            return LuaValue.NIL;
        }
    }
    
    static class setExitTransition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            LuaTable task = args.checktable(1);
            Undertailor.getOverworldController().setExitTransition(new LuaTask(task));
            return LuaValue.NIL;
        }
    }
}
