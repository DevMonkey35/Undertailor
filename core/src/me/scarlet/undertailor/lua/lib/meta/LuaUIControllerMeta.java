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

import me.scarlet.undertailor.environment.UIController;
import me.scarlet.undertailor.environment.ui.UIObject;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.lib.game.EnvironmentLib;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaUIControllerMeta extends LuaLibrary {
    
    public static LuaObjectValue<UIController> create(UIController controller) {
        return LuaObjectValue.of(controller, Lua.TYPENAME_UICONTROLLER, Lua.META_UICONTROLLER);
    }
    
    public static LuaObjectValue<UIController> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_UICONTROLLER);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new getOwningEnvironment(),
            new registerObject(),
            new getObject(),
            new destroyObject()
    };
    
    public LuaUIControllerMeta() {
        super(null, COMPONENTS);
    }
    
    static class getOwningEnvironment extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIController controller = check(args.arg1()).getObject();
            
            return EnvironmentLib.create(controller.getEnvironment());
        }
    }
    
    static class registerObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            UIController controller = check(args.arg1()).getObject();
            UIObject obj = LuaUIObjectMeta.check(args.arg(2)).getObject();
            return LuaValue.valueOf(controller.registerObject(obj));
        }
    }
    
    static class getObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            UIController controller = check(args.arg1()).getObject();
            int id = args.checkint(2);
            UIObject obj = controller.getUIObject(id);
            if(obj == null) {
                return LuaValue.NIL;
            } else {
                return LuaUIObjectMeta.create(obj);
            }
        }
    }
    
    static class destroyObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);

            UIController controller = check(args.arg1()).getObject();
            int id = args.checkint(2);
            return LuaValue.valueOf(controller.destroyObject(id));
        }
    }
}
