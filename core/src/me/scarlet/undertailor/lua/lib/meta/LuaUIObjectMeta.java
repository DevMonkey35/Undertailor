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
import me.scarlet.undertailor.environment.UIController;
import me.scarlet.undertailor.environment.ui.UIComponent;
import me.scarlet.undertailor.environment.ui.UIObject;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaUIObjectMeta extends LuaLibrary {
    
    public static LuaObjectValue<UIObject> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_UIOBJECT);
    }
    
    public static LuaObjectValue<UIObject> create(UIObject value) {
        return LuaObjectValue.of(value, Lua.TYPENAME_UIOBJECT, Lua.META_UIOBJECT);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = new LibraryFunction[] {
            new getOwningController(),
            new getID(),
            new isHeadless(),
            new getLifetime(),
            new isVisible(),
            new setVisible(),
            new getPosition(),
            new setPosition(),
            new registerComponent(),
            new destroy()
    };
    
    public LuaUIObjectMeta() {
        super(null, COMPONENTS);
    }
    
    static class getOwningController extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIObject obj = check(args.arg1()).getObject();
            UIController controller = obj.getOwningController();
            
            if(controller != null) {
                return LuaUIControllerMeta.create(controller);
            }
            
            return LuaValue.NIL;
        }
    }
    
    static class getID extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIObject obj = check(args.arg1()).getObject();
            return LuaValue.valueOf(obj.getId());
        }
    }
    
    static class isHeadless extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIObject obj = check(args.arg1()).getObject();
            return LuaValue.valueOf(obj.isHeadless());
        }
    }
    
    static class getLifetime extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIObject obj = check(args.arg1()).getObject();
            return LuaValue.valueOf(obj.getLifetime());
        }
    }
    
    static class isVisible extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIObject obj = check(args.arg1()).getObject();
            return LuaValue.valueOf(obj.isVisible());
        }
    }
    
    static class setVisible extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            UIObject obj = check(args.arg1()).getObject();
            boolean flag = args.checkboolean(2);
            obj.setVisible(flag);
            return LuaValue.NIL;
        }
    }
    
    static class getPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIObject obj = check(args.arg(1)).getObject();
            Vector2 pos = obj.getPosition();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(pos.x),
                    LuaValue.valueOf(pos.y)});
        }
    }
    
    static class setPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 3);
            
            UIObject obj = check(args.arg(1)).getObject();
            float x = (float) args.arg(2).checkdouble();
            float y = (float) args.arg(3).checkdouble();
            
            Vector2 pos = obj.getPosition();
            pos.set(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class destroy extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIObject obj = check(args.arg1()).getObject();
            obj.destroy();
            return LuaValue.NIL;
        }
    }
    
    static class registerComponent extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            UIObject obj = check(args.arg1()).getObject();
            UIComponent component = LuaUIComponentMeta.check(args.arg(2)).getObject();
            
            obj.registerChild(component);
            return LuaValue.NIL;
        }
    }
}
