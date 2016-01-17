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

import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.overworld.WorldObject;
import me.scarlet.undertailor.overworld.WorldRoom;
import me.scarlet.undertailor.overworld.WorldRoom.Entrypoint;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.wrappers.RoomDataWrapper;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaWorldRoomMeta extends LuaLibrary {
    
    public static LuaObjectValue<WorldRoom> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_WORLDROOM);
    }
    
    public static LuaObjectValue<WorldRoom> create(WorldRoom room) {
        return LuaObjectValue.of(room, Lua.TYPENAME_WORLDROOM, Lua.META_WORLDROOM);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new getRoomName(),
            new registerObject(),
            new getObject(),
            new setMap(),
            new removeObject(),
            new newEntrypoint(),
            new registerEntrypoint()
    };
    
    public LuaWorldRoomMeta() {
        super(null, COMPONENTS);
    }
    
    static class setMap extends LibraryFunction {
        @Override
        @SuppressWarnings("unchecked")
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            WorldRoom room = check(args.arg1()).getObject();
            LuaValue map = LuaUtil.checkType(args.arg(2), Lua.TYPENAME_WORLDMAP); // TODO fix after map meta
            room.setMap(((LuaObjectValue<RoomDataWrapper>) map).getObject());
            return LuaValue.NIL;
        }
    }
    
    static class getRoomName extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldRoom room = check(args.arg1()).getObject();
            return LuaValue.valueOf(room.getRoomName());
        }
    }
    
    static class registerObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            WorldRoom room = check(args.arg1()).getObject();
            WorldObject object = LuaWorldObjectMeta.check(args.arg(2)).getObject();
            return LuaValue.valueOf(room.registerObject(object));
        }
    }
    
    static class getObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            WorldRoom room = check(args.arg1()).getObject();
            int id = args.checkint(2);
            if(room.getObject(id) != null) {
                return LuaWorldObjectMeta.create(room.getObject(id));
            } else {
                return LuaValue.NIL;
            }
        }
    }
    
    static class registerEntrypoint extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 3, 3);
            
            WorldRoom room = check(args.arg1()).getObject();
            String id = args.checkjstring(2);
            Entrypoint entrypoint = LuaEntrypointMeta.check(args.arg(3)).getObject();
            
            room.registerEntrypoint(id, entrypoint);
            return LuaValue.NIL;
        }
    }
    
    static class removeObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            WorldRoom room = check(args.arg1()).getObject();
            int id = args.checkint(2);
            room.removeObject(id);
            return LuaValue.NIL;
        }
    }
    
    static class newEntrypoint extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            
            return LuaEntrypointMeta.create(new Entrypoint());
        }
    }
    
    // TODO map data access
}
