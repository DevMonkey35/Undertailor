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
import com.badlogic.gdx.utils.ObjectSet;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.engine.Environment;
import me.scarlet.undertailor.engine.EnvironmentManager;
import me.scarlet.undertailor.engine.events.Event;
import me.scarlet.undertailor.engine.ui.UIObject;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.ScriptManager;
import me.scarlet.undertailor.lua.impl.LuaRenderable;
import me.scarlet.undertailor.lua.impl.LuaUIComponent;
import me.scarlet.undertailor.lua.impl.LuaWorldObject;
import me.scarlet.undertailor.lua.impl.LuaWorldRoom;
import me.scarlet.undertailor.lua.lib.game.AudioLib;
import me.scarlet.undertailor.lua.lib.game.ControlLib;
import me.scarlet.undertailor.lua.lib.game.GraphicsLib;
import me.scarlet.undertailor.lua.meta.LuaEnvironmentMeta;
import me.scarlet.undertailor.util.LuaUtil;

import java.io.File;

/**
 * Game library accessible through Lua.
 */
public class GameLib extends LuaLibrary {

    private ObjectSet<LuaLibrary> childLibraries;

    public GameLib(Undertailor undertailor) {
        super("game");

        EnvironmentManager envMan = undertailor.getEnvironmentManager();
        ScriptManager scriptMan = undertailor.getAssetManager().getScriptManager();

        // game.setTitle(title)
        set("setTitle", asFunction(vargs -> {
            Gdx.graphics.setTitle(vargs.arg1().optjstring(1, "Undertailor"));
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

        // game.destroyEnvironment(env)
        // game.destroyEnvironment(name)
        set("destroyEnvironment", asFunction(vargs -> {
            if (vargs.isstring(1)) { // passed an env name
                envMan.destroyEnvironment(vargs.checkjstring(1));
            } else { // pased an env
                LuaEnvironmentMeta.convert(vargs.checknotnil(1)).getObject().destroy();
            }

            return NIL;
        }));

        // game.getActiveEnvironment()
        set("getActiveEnvironment", asFunction(vargs -> {
            return orNil(envMan.getActiveEnvironment());
        }));

        // game.setActiveEnvironment(env)
        // game.setActiveEnvironment(name)
        set("setActiveEnvironment", asFunction(vargs -> {
            if (vargs.isstring(1)) { // passed a name
                envMan.setActiveEnvironment(vargs.checkjstring(1));
            } else { // passed an env
                Environment env = LuaEnvironmentMeta.convert(vargs.checknotnil(1)).getObject();
                envMan.setActiveEnvironment(env);
            }

            return NIL;
        }));

        // game.callEvent(event)
        set("callEvent", asFunction(vargs -> {
            Event event = Event.asLuaEvent(vargs.checktable(1).unpack());
            return valueOf(envMan.callEvent(event));
        }));

        // game.onEvent(eventId, handler)
        set("onEvent", asFunction(vargs -> {
            envMan.getEventHelper().registerHandler(vargs.checkjstring(1), event -> {
                Varargs params = LuaUtil.unpack(event.asLua(), 3 + event.getParameters().length);
                return vargs.checkfunction(2).invoke(params.subargs(2))
                    .toboolean(1);
            });

            return NIL;
        }));

        // ---------------- object gen ----------------

        // game.newWorldObject([scriptPath, params])
        set("newWorldObject", asFunction(vargs -> {
            String filePath = vargs.optjstring(1, null);
            LuaWorldObject obj;
            try {
                if (filePath != null) {
                    obj = new LuaWorldObject(scriptMan,
                        new File(scriptMan.getScriptPath(), filePath), vargs.subargs(2));
                } else {
                    obj = new LuaWorldObject();
                }
            } catch (Exception e) {
                throw LuaUtil.causedError(e);
            }

            return obj.getObjectValue();
        }));

        // game.newWorldRoom([scriptPath, params])
        set("newWorldRoom", asFunction(vargs -> {
            String filePath = vargs.optjstring(1, null);
            LuaWorldRoom obj;
            try {
                if(filePath != null) {
                    obj = new LuaWorldRoom(scriptMan, new File(scriptMan.getScriptPath(), filePath),
                        vargs.subargs(2));
                } else {
                    obj = new LuaWorldRoom();
                }
            } catch (Exception e) {
                throw LuaUtil.causedError(e);
            }

            return obj.getObjectValue();
        }));

        // game.newUIComponent([scriptPath, params])
        set("newUIComponent", asFunction(vargs -> {
            String filePath = vargs.optjstring(1, null);
            LuaUIComponent obj;
            try {
                if (filePath != null) {
                    obj = new LuaUIComponent(scriptMan,
                        new File(scriptMan.getScriptPath(), filePath), vargs.subargs(2));
                } else {
                    obj = new LuaUIComponent();
                }
            } catch (Exception e) {
                throw LuaUtil.causedError(e);
            }

            return obj.getObjectValue();
        }));

        // game.newUIObject([active])
        set("newUIObject", asFunction(vargs -> {
            return orNil(new UIObject(vargs.optboolean(1, false)));
        }));

        // game.newRenderable([scriptPath, params])
        set("newRenderable", asFunction(vargs -> {
            String filePath = vargs.optjstring(1, null);
            LuaRenderable obj;
            try {
                if (filePath != null) {
                    obj = new LuaRenderable(scriptMan,
                        new File(scriptMan.getScriptPath(), filePath), vargs.subargs(2));
                } else {
                    obj = new LuaRenderable();
                }
            } catch (Exception e) {
                throw LuaUtil.causedError(e);
            }

            return obj.getObjectValue();
        }));

        // ---------------- child lib ----------------

        this.childLibraries = new ObjectSet<>();

        childLibraries.add(new AudioLib(undertailor.getAssetManager().getAudioManager()));
        childLibraries.add(new GraphicsLib(undertailor));
        childLibraries.add(new ControlLib(undertailor.getInput()));
    }

    @Override
    public void postinit(LuaTable table, LuaValue environment) {
        childLibraries.forEach(lib -> lib.call(null, table));
    }
}
