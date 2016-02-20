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

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.environment.Environment;
import me.scarlet.undertailor.environment.ui.UIObject;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.impl.UIComponentImplementable;
import me.scarlet.undertailor.lua.impl.UIComponentImplementable.UIComponentImplementation;
import me.scarlet.undertailor.lua.impl.WorldObjectImplementable;
import me.scarlet.undertailor.lua.impl.WorldObjectImplementable.WorldObjectImplementation;
import me.scarlet.undertailor.lua.impl.WorldRoomImplementable;
import me.scarlet.undertailor.lua.impl.WorldRoomImplementable.WorldRoomImplementation;
import me.scarlet.undertailor.lua.lib.meta.LuaOverworldControllerMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaRoomMapMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaSchedulerMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaUIComponentMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaUIControllerMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaUIObjectMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaWorldObjectMeta;
import me.scarlet.undertailor.lua.lib.meta.LuaWorldRoomMeta;
import me.scarlet.undertailor.manager.EnvironmentManager;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class EnvironmentLib extends LuaLibrary {
    
    public static LuaObjectValue<Environment> create(Environment environment) {
        return LuaObjectValue.of(environment, Lua.TYPENAME_ENVIRONMENT, LuaLibrary.asMetatable(Lua.LIB_ENVIRONMENT));
    }
    
    public static LuaObjectValue<Environment> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_ENVIRONMENT);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new hasEnvironment(),
            new getEnvironment(),
            new destroyEnvironment(),
            new getActiveEnvironment(),
            new setActiveEnvironment(),
            
            new newUIComponent(),
            new newUIObject(),
            
            new newWorldObject(),
            new newWorldRoom(),
            new newWorldMap(),
            
            new getOverworldController(),
            new getUIController(),
            new getScheduler(),
            new getName()
    };
    
    public EnvironmentLib() {
        super("environment", COMPONENTS);
    }
    
    // --- Class utility methods.
    
    static EnvironmentManager getEnvironmentManager() {
        return Undertailor.getEnvironmentManager();
    }
    
    // --- Environment library methods.
    
    // =-- Access methods.
    
    // hasEnvironment
    static class hasEnvironment extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            return LuaValue.valueOf(getEnvironmentManager().hasEnvironment(args.checkjstring(1)));
        }
    }
    
    // getEnvironment
    static class getEnvironment extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            return EnvironmentLib.create(getEnvironmentManager().getEnvironment(args.checkjstring(1)));
        }
    }
    
    // destroyEnvironment
    static class destroyEnvironment extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            getEnvironmentManager().destroyEnvironment(args.checkjstring(1));
            return LuaValue.NIL;
        }
    }
    
    // getActiveEnvironment
    static class getActiveEnvironment extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            
            Environment env = getEnvironmentManager().getActiveEnvironment();
            if(env != null) {
                return EnvironmentLib.create(env);
            } else {
                return LuaValue.NIL;
            }
        }
    }
    
    // setActiveEnvironment
    static class setActiveEnvironment extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            getEnvironmentManager().setActiveEnvironment(EnvironmentLib.check(args.arg1()).getObject());
            return LuaValue.NIL;
        }
    }
    
    // =-- Creation methods.
    
    static class newUIComponent extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, -1);
            String componentName = args.optjstring(1, null);
            
            if(componentName == null) {
                UIComponentImplementation impl = new UIComponentImplementation();
                impl.setImplementable(Undertailor.getScriptManager().getImplementable(UIComponentImplementable.class));
                impl.setObjectValue(LuaUIComponentMeta.create(impl));
                return impl.getObjectValue();
            } else {
                try {
                    return getEnvironmentManager().getUIComponentLoader().newLuaComponent(componentName, args.subargs(2));
                } catch(LuaError e) {
                    throw new LuaError("\n" + e.getMessage(), 2);
                }
            }
        }
    }
    
    static class newUIObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 1);
            
            boolean headless = args.optboolean(1, false);
            return LuaUIObjectMeta.create(new UIObject(headless));
        }
    }
    
    static class newWorldRoom extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 1);
            String roomName = args.optjstring(1, null);
            
            if(roomName == null) {
                WorldRoomImplementation impl = new WorldRoomImplementation();
                impl.setImplementable(Undertailor.getScriptManager().getImplementable(WorldRoomImplementable.class));
                impl.setObjectValue(LuaWorldRoomMeta.create(impl));
                return impl.getObjectValue();
            } else {
                try {
                    return getEnvironmentManager().getRoomLoader().newRoomMap(roomName, args.subargs(2));
                } catch(LuaError e) {
                    throw new LuaError("\n" + e.getMessage(), 2);
                }
            }
        }
    }
    
    static class newWorldMap extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            return LuaRoomMapMeta.create(getEnvironmentManager().getRoomLoader().getRoom(args.checkjstring(1)));
        }
    }
    
    static class newWorldObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, -1);
            String objectName = args.optjstring(1, null);
            
            if(objectName == null) {
                WorldObjectImplementation impl = new WorldObjectImplementation();
                impl.setImplementable(Undertailor.getScriptManager().getImplementable(WorldObjectImplementable.class));
                impl.setObjectValue(LuaWorldObjectMeta.create(impl));
                return impl.getObjectValue();
            } else {
                try {
                    return getEnvironmentManager().getWorldObjectLoader().newWorldObject(objectName, args.subargs(2));
                } catch(LuaError e) {
                    throw new LuaError("\n" + e.getMessage(), 2);
                }
            }
        }
    }
    
    // --- Environment object methods.
    
    static class getOverworldController extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Environment env = check(args.arg(1)).getObject();
            return LuaOverworldControllerMeta.create(env.getOverworldController());
        }
    }
    
    static class getUIController extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Environment env = check(args.arg(1)).getObject();
            return LuaUIControllerMeta.create(env.getUIController());
        }
    }
    
    static class getScheduler extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Environment env = check(args.arg1()).getObject();
            
            return LuaSchedulerMeta.create(env.getScheduler());
        }
    }
    
    static class getName extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Environment env = check(args.arg1()).getObject();
            
            return LuaValue.valueOf(env.getName());
        }
    }
}
