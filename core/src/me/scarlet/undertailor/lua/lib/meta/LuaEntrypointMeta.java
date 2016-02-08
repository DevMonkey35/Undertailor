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
import me.scarlet.undertailor.environment.overworld.WorldRoom.Entrypoint;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaEntrypointMeta extends LuaLibrary {
    
    public static LuaObjectValue<Entrypoint> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_ENTRYPOINT);
    }
    
    public static LuaObjectValue<Entrypoint> create(Entrypoint value) {
        return LuaObjectValue.of(value, Lua.TYPENAME_ENTRYPOINT, Lua.META_ENTRYPOINT);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = new LibraryFunction[] {
            new getBoundingBox(),
            new getSpawnPosition(),
            new setSpawnPosition(),
            new getRoomTarget(),
            new setRoomTarget(),
            new getPosition(),
            new setPosition()
    };
    
    public LuaEntrypointMeta() {
        super(null, COMPONENTS);
    }
    
    static class getSpawnPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Entrypoint entrypoint = check(args.arg(1)).getObject();
            Vector2 spawnpos = entrypoint.getSpawnPosition();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(spawnpos.x),
                    LuaValue.valueOf(spawnpos.y)
            });
        }
    }
    
    static class setSpawnPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 3);
            
            Entrypoint entrypoint = check(args.arg1()).getObject();
            Vector2 spawnpos = entrypoint.getSpawnPosition();
            float x = new Float(args.optdouble(2, spawnpos.x));
            float y = new Float(args.optdouble(3, spawnpos.y));
            
            entrypoint.setSpawnPosition(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class getPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Entrypoint entrypoint = check(args.arg(1)).getObject();
            Vector2 pos = entrypoint.getPosition();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(pos.x),
                    LuaValue.valueOf(pos.y)
            });
        }
    }
    
    static class setPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 3);
            
            Entrypoint entrypoint = check(args.arg1()).getObject();
            Vector2 pos = entrypoint.getPosition();
            float x = new Float(args.optdouble(2, pos.x));
            float y = new Float(args.optdouble(3, pos.y));
            
            entrypoint.setPosition(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class getRoomTarget extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Entrypoint entrypoint = check(args.arg1()).getObject();
            return LuaValue.valueOf(entrypoint.getRoomTarget());
        }
    }
    
    static class setRoomTarget extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            Entrypoint entrypoint = check(args.arg1()).getObject();
            String target = args.checkjstring(2);
            
            entrypoint.setRoomTarget(target);
            return LuaValue.NIL;
        }
    }
    
    static class getBoundingBox extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Entrypoint entrypoint = check(args.arg1()).getObject();
            return LuaBoundingBoxMeta.create(entrypoint.getBoundingBox());
        }
    }
}
