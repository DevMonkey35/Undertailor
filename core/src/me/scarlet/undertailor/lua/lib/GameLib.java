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

import static me.scarlet.undertailor.lua.LuaObjectValue.orNil;
import static me.scarlet.undertailor.util.LuaUtil.asFunction;

import com.badlogic.gdx.Gdx;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import me.scarlet.undertailor.AssetManager;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.engine.Environment;
import me.scarlet.undertailor.engine.EnvironmentManager;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.ScriptManager;
import me.scarlet.undertailor.lua.impl.LuaWorldObject;
import me.scarlet.undertailor.lua.impl.LuaWorldRoom;
import me.scarlet.undertailor.lua.lib.game.AudioLib;
import me.scarlet.undertailor.lua.lib.game.ControlLib;
import me.scarlet.undertailor.lua.lib.game.GraphicsLib;
import me.scarlet.undertailor.lua.meta.LuaEnvironmentMeta;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Game library accessible through Lua.
 */
public class GameLib extends LuaLibrary {

    private Set<LuaLibrary> childLibraries;

    public GameLib(Undertailor undertailor) {
        super("game");

        EnvironmentManager envMan = undertailor.getEnvironmentManager();
        ScriptManager scriptMan = undertailor.getAssetManager().getScriptManager();

        // game.setTitle(title)
        set("setTitle", asFunction(vargs -> {
            Gdx.graphics.setTitle(vargs.arg1().checkjstring());
            return LuaValue.NIL;
        }));

        // game.getGlobalScheduler()
        set("getGlobalScheduler", asFunction(vargs -> {
            return orNil(envMan.getGlobalScheduler());
        }));

        // game.getEnvironment(name)
        set("getEnvironment", asFunction(vargs -> {
            return orNil(envMan.getEnvironment(vargs.checkjstring(1)));
        }));

        // game.destroyEnvironment(name)
        set("destroyEnvironment", asFunction(vargs -> {
            envMan.destroyEnvironment(vargs.checkjstring(1));
            return NIL;
        }));

        // game.getActiveEnvironment()
        set("getActiveEnvironment", asFunction(vargs -> {
            return orNil(envMan.getActiveEnvironment());
        }));

        // game.setActiveEnvironment(env)
        set("setActiveEnvironment", asFunction(vargs -> {
            Environment env = LuaEnvironmentMeta.convert(vargs.checknotnil(1)).getObject();
            envMan.setActiveEnvironment(env);
            return NIL;
        }));

        // ---------------- object gen ----------------

        // game.newWorldObject(scriptPath)
        set("newWorldObject", asFunction(vargs -> {
            String filePath = vargs.checkjstring(1);
            LuaWorldObject obj;
            try {
                obj = new LuaWorldObject(scriptMan,
                    new File(AssetManager.rootDirectory, filePath));
            } catch (Exception e) {
                /*LuaError thrown = new LuaError("failed to load world object script at " + filePath);
                thrown.initCause(e);
                throw thrown;*/
                throw new LuaError(e);
            }

            return obj.getObjectValue();
        }));

        // game.newWorldRoom(scriptPath)
        set("newWorldRoom", asFunction(vargs -> {
            String filePath = vargs.checkjstring(1);
            LuaWorldRoom obj;
            try {
                obj = new LuaWorldRoom(scriptMan,
                    new File(AssetManager.rootDirectory, filePath));
            } catch (Exception e) {
                /*LuaError thrown = new LuaError("failed to load world room script at " + filePath);
                thrown.initCause(e);
                throw thrown;*/
                throw new LuaError(e);
            }

            return obj.getObjectValue();
        }));

        // ---------------- child lib ----------------

        this.childLibraries = new HashSet<>();

        childLibraries.add(new AudioLib(undertailor.getAssetManager().getAudioManager()));
        childLibraries.add(new GraphicsLib(undertailor.getRenderer()));
        childLibraries.add(new ControlLib(undertailor.getInput()));
    }

    @Override
    public void postinit(LuaTable table, LuaValue environment) {
        childLibraries.forEach(lib -> lib.call(null, table));
    }
}
