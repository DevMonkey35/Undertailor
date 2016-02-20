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
import me.scarlet.undertailor.environment.ui.UIComponent;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaUIComponentMeta extends LuaLibrary {
    
    public static LuaObjectValue<UIComponent> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_UICOMPONENT);
    }
    
    public static LuaObjectValue<UIComponent> create(UIComponent value) {
        return LuaObjectValue.of(value, Lua.TYPENAME_UICOMPONENT, Lua.META_UICOMPONENT);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = new LibraryFunction[] {
            new getParent(),
            new getRealPosition(),
            new getPosition(),
            new setPosition(),
            new setAlwaysActive(),
            new setRenderWhenInactive(),
            new destroy(),
            new destroyParent(),
            new getComponentTypeName()
    };
    
    public LuaUIComponentMeta() {
        super(null, COMPONENTS);
    }
    
    static class getParent extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIComponent component = check(args.arg1()).getObject();
            if(component.getParent() == null) {
                return LuaValue.NIL;
            } else {
                return LuaUIObjectMeta.create(component.getParent());
            }
        }
    }
    
    static class getRealPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIComponent component = check(args.arg1()).getObject();
            Vector2 pos = component.getRealPosition();
            
            return LuaValue.varargsOf(new LuaValue[] {LuaValue.valueOf(pos.x), LuaValue.valueOf(pos.y)});
        }
    }
    
    static class getPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIComponent component = check(args.arg1()).getObject();
            Vector2 pos = component.getPosition();
            
            return LuaValue.varargsOf(new LuaValue[] {LuaValue.valueOf(pos.x), LuaValue.valueOf(pos.y)});
        }
    }
    
    static class setPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 3);
            
            UIComponent component = check(args.arg1()).getObject();
            Vector2 pos = component.getPosition();
            float x = (float) args.optdouble(2, pos.x);
            float y = (float) args.optdouble(3, pos.y);
            
            component.setPosition(new Vector2(x, y));
            return LuaValue.NIL;
        }
    }
    
    static class setAlwaysActive extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            UIComponent component = check(args.arg1()).getObject();
            boolean flag = args.checkboolean(2);
            
            component.setAlwaysActive(flag);
            return LuaValue.NIL;
        }
    }
    
    static class setRenderWhenInactive extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            UIComponent component = check(args.arg1()).getObject();
            boolean flag = args.checkboolean(2);
            
            component.setRenderWhenInactive(flag);
            return LuaValue.NIL;
        }
    }
    
    static class destroy extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIComponent component = check(args.arg1()).getObject();
            
            try {
                component.destroy();
            } catch(Exception e) {
                throw new LuaError(e.getMessage());
            }
            
            return LuaValue.NIL;
        }
    }
    
    static class destroyParent extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            try {
                UIComponent component = check(args.arg1()).getObject();
                component.destroyObject();
            } catch(IllegalArgumentException e) {
                throw new LuaError(e.getMessage());
            }
            
            return LuaValue.NIL;
        }
    }
    
    static class getComponentTypeName extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            UIComponent component = check(args.arg1()).getObject();
            return LuaValue.valueOf(component.getComponentTypeName());
        }
    }
}
