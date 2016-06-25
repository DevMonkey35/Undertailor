/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without
 * limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package me.scarlet.undertailor.lua.lib;

import static me.scarlet.undertailor.util.LuaUtil.asFunction;

import com.badlogic.gdx.Gdx;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.lib.game.AudioLib;
import me.scarlet.undertailor.lua.lib.game.ControlLib;
import me.scarlet.undertailor.lua.lib.game.GraphicsLib;

import java.util.HashSet;
import java.util.Set;

/**
 * Game library accessible through Lua.
 */
public class GameLib extends LuaLibrary {

    private Undertailor undertailor;

    private Set<LuaLibrary> childLibraries;

    public GameLib(Undertailor undertailor) {
        super("game");

        this.undertailor = undertailor;
        this.set("setTitle", asFunction(vargs -> {
            Gdx.graphics.setTitle(vargs.arg1().checkjstring());
            return LuaValue.NIL;
        }));

        
    }

    @Override
    public void postinit(LuaTable table, LuaValue environment) {
        this.childLibraries = new HashSet<>();
        
        childLibraries.add(new AudioLib(undertailor.getAssetManager().getAudioManager()));
        childLibraries.add(new GraphicsLib(undertailor.getRenderer()));
        childLibraries.add(new ControlLib(undertailor.getInput()));

        childLibraries.forEach(lib -> lib.call(null, table));
    }
}
