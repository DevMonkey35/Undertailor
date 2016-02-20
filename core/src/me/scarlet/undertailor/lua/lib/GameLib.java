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

package me.scarlet.undertailor.lua.lib;

import com.badlogic.gdx.Gdx;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class GameLib extends LuaLibrary {
    
    public static final String DEFAULT_TITLE = "UNDERTAILOR";
    public static boolean defset = false;
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new setWindowTitle(),
            new setFrameCap(),
            
            Lua.LIB_AUDIO,
            Lua.LIB_LOGGER,
            Lua.LIB_GRAPHICS,
            Lua.LIB_ANIMATION,
            Lua.LIB_ENVIRONMENT
    };
    
    public GameLib() {
        super("game", COMPONENTS);
    }
    
    @Override
    public void postinit(LuaValue env, LuaValue game) {
        if(!defset) {
            Gdx.graphics.setTitle(DEFAULT_TITLE);
            defset = true;
        }
    }
    
    static class setWindowTitle extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 1);
            Gdx.graphics.setTitle(args.optjstring(1, DEFAULT_TITLE));
            return LuaValue.NIL;
        }
    }
    
    static class setFrameCap extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Undertailor.setFrameCap(args.checkint(1));
            return LuaValue.NIL;
        }
    }
}
