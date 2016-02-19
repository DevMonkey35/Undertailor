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

package me.scarlet.undertailor.lua.lib.meta;

import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.environment.OverworldController;
import me.scarlet.undertailor.environment.overworld.WorldRoom;
import me.scarlet.undertailor.environment.scheduler.LuaTask;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.lib.game.EnvironmentLib;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaOverworldControllerMeta extends LuaLibrary {
    
    public static LuaObjectValue<OverworldController> create(OverworldController controller) {
        return LuaObjectValue.of(controller, Lua.TYPENAME_OVERWORLDCONTROLLER, Lua.META_OVERWORLDCONTROLLER);
    }
    
    public static LuaObjectValue<OverworldController> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_OVERWORLDCONTROLLER);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new getOwningEnvironment(),
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
    
    public LuaOverworldControllerMeta() {
        super(null, COMPONENTS);
    }
    
    static class getOwningEnvironment extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            OverworldController controller = check(args.arg1()).getObject();
            
            return EnvironmentLib.create(controller.getEnvironment());
        }
    }
    
    static class isRendering extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            OverworldController controller = check(args.arg1()).getObject();
            
            return LuaValue.valueOf(controller.isRendering());
        }
    }
    
    static class setRendering extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);

            OverworldController controller = check(args.arg1()).getObject();
            boolean flag = args.checkboolean(2);
            
            controller.setRendering(flag);
            return LuaValue.NIL;
        }
    }
    
    static class isProcessing extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            OverworldController controller = check(args.arg1()).getObject();
            
            return LuaValue.valueOf(controller.isProcessing());
        }
    }
    
    static class setProcessing extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            OverworldController controller = check(args.arg1()).getObject();
            boolean flag = args.checkboolean(2);
            
            controller.setProcessing(flag);
            return LuaValue.NIL;
        }
    }
    
    static class isRenderingHitboxes extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            OverworldController controller = check(args.arg1()).getObject();
            
            return LuaValue.valueOf(controller.isRenderingHitboxes());
        }
    }
    
    static class setRenderingHitboxes extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);

            OverworldController controller = check(args.arg1()).getObject();
            boolean flag = args.checkboolean(2);
            
            controller.setRenderingHitboxes(flag);
            return LuaValue.NIL;
        }
    }
    
    static class isCameraFixing extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            OverworldController controller = check(args.arg1()).getObject();
            
            return LuaValue.valueOf(controller.isCameraFixing());
        }
    }
    
    static class setCameraFixing extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            OverworldController controller = check(args.arg1()).getObject();
            boolean flag = args.checkboolean(2);
            
            controller.setCameraFixing(flag);
            return LuaValue.NIL;
        }
    }
    
    static class getCameraPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);

            OverworldController controller = check(args.arg1()).getObject();
            Vector2 position = controller.getCameraPosition();
            
            return LuaUtil.asVarargs(LuaValue.valueOf(position.x), LuaValue.valueOf(position.y));
        }
    }
    
    static class setCameraPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 3);

            OverworldController controller = check(args.arg1()).getObject();
            Vector2 position = controller.getCameraPosition();
            
            float posX = (float) args.optdouble(2, position.x); //args.isnil(1) ? position.x : new Float(args.checkdouble(1));
            float posY = (float) args.optdouble(3, position.y); // args.isnil(2) ? position.y : new Float(args.checkdouble(2));
            controller.setCameraPosition(posX, posY);
            
            return LuaValue.NIL;
        }
    }
    
    static class getCameraZoom extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            OverworldController controller = check(args.arg1()).getObject();
            
            return LuaValue.valueOf(controller.getCameraZoom());
        }
    }
    
    static class setCameraZoom extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            OverworldController controller = check(args.arg1()).getObject();
            float zoom = (float) args.checkdouble(2);
            controller.setCameraZoom(zoom);
            return LuaValue.NIL;
        }
    }
    
    static class getCurrentRoom extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);

            OverworldController controller = check(args.arg1()).getObject();
            return LuaWorldRoomMeta.create(controller.getCurrentRoom());
        }
    }
    
    static class setCurrentRoom extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 5);

            OverworldController controller = check(args.arg1()).getObject();
            WorldRoom room = LuaWorldRoomMeta.check(args.arg(2)).getObject();
            boolean transitions = args.optboolean(3, true);
            String exitpoint = args.optjstring(4, null);
            String entrypoint = args.optjstring(5, null);
            
            controller.setCurrentRoom(room, transitions, exitpoint, entrypoint);
            return LuaValue.NIL;
        }
    }
    
    static class getCharacterID extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            OverworldController controller = check(args.arg1()).getObject();
            return LuaValue.valueOf(controller.getCharacterID());
        }
    }
    
    static class setCharacterID extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            OverworldController controller = check(args.arg1()).getObject();
            long id = args.checklong(2);
            controller.setCharacterID(id);
            return LuaValue.NIL;
        }
    }
    
    static class setEntryTransition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            OverworldController controller = check(args.arg1()).getObject();
            LuaTable task = args.checktable(2);
            
            controller.setEntryTransition(new LuaTask(task));
            return LuaValue.NIL;
        }
    }
    
    static class setExitTransition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            OverworldController controller = check(args.arg1()).getObject();
            LuaTable task = args.checktable(2);
            controller.setExitTransition(new LuaTask(task));
            return LuaValue.NIL;
        }
    }
}
