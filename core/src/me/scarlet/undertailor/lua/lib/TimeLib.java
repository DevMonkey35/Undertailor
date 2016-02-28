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
import com.badlogic.gdx.utils.TimeUtils;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class TimeLib extends LuaLibrary {
    
    private static double time;
    private static float delta;
    
    static {
        time = 0;
    }
    
    public static double getCurrentRuntime() {
        return time;
    }
    
    public static void advanceTime(long millis) {
        time += (double) (millis / 1000.0);
    }
    
    public static void advanceTime(double seconds) {
        time += seconds;
    }
    
    public static float getDeltaTime() {
        return delta;
    }
    
    public static void updateDeltaTime() {
        delta = Gdx.graphics.getDeltaTime();
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new millis(),
            new sinceMillis(),
            new seconds(),
            new sinceSeconds(),
            
            new eMillis(),
            new eSinceMillis(),
            new eSeconds(),
            new eSinceSeconds()
    };
    
    public TimeLib() {
        super("time", COMPONENTS);
    }
    
    static class delta extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            
            return LuaValue.valueOf(delta);
        }
    }
    
    static class millis extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            
            return LuaValue.valueOf((long) (time * 1000));
        }
    }
    
    static class sinceMillis extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            long millis = args.checklong(1);
            long currentMillis = (long) (time * 1000);
            return LuaValue.valueOf(currentMillis - millis);
        }
    }
    
    static class seconds extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            
            return LuaValue.valueOf(time);
        }
    }
    
    static class sinceSeconds extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            double seconds = args.checkdouble(1);
            return LuaValue.valueOf(time - seconds);
        }
    }
    
    static class eMillis extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            
            return LuaValue.valueOf(TimeUtils.millis());
        }
    }
    
    static class eSinceMillis extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            long millis = args.checklong(1);
            return LuaValue.valueOf(TimeUtils.timeSinceMillis(millis));
        }
    }
    
    static class eSeconds extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            
            return LuaValue.valueOf(TimeUtils.millis() / 1000.0);
        }
    }
    
    static class eSinceSeconds extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            double seconds = args.checkdouble(1);
            return LuaValue.valueOf(TimeUtils.timeSinceMillis((long) (seconds * 1000)) / 1000.0);
        }
    }
}
