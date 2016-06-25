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

package me.scarlet.undertailor.lua.meta;

import static me.scarlet.undertailor.lua.LuaObjectValue.orNil;
import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static me.scarlet.undertailor.util.LuaUtil.varargsOf;
import static org.luaj.vm2.LuaValue.NIL;
import static org.luaj.vm2.LuaValue.valueOf;

import com.badlogic.gdx.math.Vector2;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.engine.overworld.OverworldCamera;
import me.scarlet.undertailor.engine.overworld.OverworldController;
import me.scarlet.undertailor.engine.overworld.WorldObject;
import me.scarlet.undertailor.engine.overworld.WorldRoom;
import me.scarlet.undertailor.engine.scheduler.Task;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link OverworldController} objects.
 * 
 * <p>Shoves both OverworldController and
 * {@link OverworldCamera} together.</p>
 */
public class LuaOverworldControllerMeta implements LuaObjectMeta {

    public static LuaObjectValue<OverworldController> convert(LuaValue value) {
        return Lua.checkType(value, LuaOverworldControllerMeta.class);
    }

    static OverworldController obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }
    
    private LuaTable metatable;
    
    public LuaOverworldControllerMeta() {
        this.metatable = new LuaTable();

        // overworld:isCharacter(worldObject)
        set("isCharacter", asFunction(vargs -> {
            WorldObject obj = LuaWorldObjectMeta.convert(vargs.checknotnil(2)).getObject();
            return valueOf(obj(vargs).isCharacter(obj));
        }));

        // overworld:setCharacter(worldObject)
        set("setCharacter", asFunction(vargs -> {
            WorldObject obj = LuaWorldObjectMeta.convert(vargs.checknotnil(2)).getObject();
            obj(vargs).setCharacter(obj);
            return NIL;
        }));

        // overworld:getRoom()
        set("getRoom", asFunction(vargs -> {
            return orNil(obj(vargs).getRoom());
        }));

        // overworld:setRoom(room[, targetEntrypoint, transitions])
        set("setRoom", asFunction(vargs -> {
            OverworldController obj = obj(vargs);
            WorldRoom room = LuaWorldRoomMeta.convert(vargs.checknotnil(2)).getObject();
            String targetPoint = vargs.optjstring(3, null);
            boolean transitions = vargs.optboolean(4, obj.isPlayingTransitions());
            obj.setRoom(room, targetPoint, transitions);
            return NIL;
        }));

        // overworld:isPlayingTransitions()
        set("isPlayingTransitions", asFunction(vargs -> {
            return valueOf(obj(vargs).isPlayingTransitions());
        }));

        // overworld:setPlayingTransitions(playingTransitions)
        set("setPlayingTransitions", asFunction(vargs -> {
            obj(vargs).setPlayingTransitions(vargs.checkboolean(2));
            return NIL;
        }));

        // overworld:setEntryTransition(task)
        set("setEntryTransition", asFunction(vargs -> {
            Task task = vargs.isnil(2) ? null : Task.asLuaTask(vargs.arg(2));
            obj(vargs).setEntryTransition(task);
            return NIL;
        }));

        // overworld:setExitTransition(task)
        set("setExitTransition", asFunction(vargs -> {
            Task task = vargs.isnil(2) ? null : Task.asLuaTask(vargs.arg(2));
            obj(vargs).setExitTransition(task);
            return NIL;
        }));

        // ---------------- camera ----------------

        // overworld:getCameraPosition()
        set("getCameraPosition", asFunction(vargs -> {
            Vector2 pos = obj(vargs).getCamera().getPosition();
            return varargsOf(pos.x, pos.y);
        }));

        // overworld:setCameraPosition(x, y)
        set("setCameraPosition", asFunction(vargs -> {
            float x = vargs.checknumber(2).tofloat();
            float y = vargs.checknumber(3).tofloat();
            obj(vargs).getCamera().setPosition(x, y);
            return NIL;
        }));

        // overworld:getCameraOffset()
        set("getCameraOffset", asFunction(vargs -> {
            Vector2 pos = obj(vargs).getCamera().getOffset();
            return varargsOf(pos.x, pos.y);
        }));

        // overworld:setCameraOffset(x, y)
        set("setCameraOffset", asFunction(vargs -> {
            float x = vargs.checknumber(2).tofloat();
            float y = vargs.checknumber(3).tofloat();
            obj(vargs).getCamera().setOffset(x, y);
            return NIL;
        }));

        // overworld:getCameraZoom()
        set("getCameraZoom", asFunction(vargs -> {
            return valueOf(obj(vargs).getCamera().getZoom());
        }));

        // overworld:setCameraZoom(zoom)
        set("setCameraZoom", asFunction(vargs -> {
            float zoom = vargs.checknumber(2).tofloat();
            obj(vargs).getCamera().setZoom(zoom);
            return NIL;
        }));

        // overworld:isCameraFixing()
        set("isCameraFixing", asFunction(vargs -> {
            return valueOf(obj(vargs).getCamera().isFixing());
        }));

        // overworld:setCameraFixing(fixing)
        set("setCameraFixing", asFunction(vargs -> {
            boolean fixing = vargs.checkboolean(2);
            obj(vargs).getCamera().setFixing(fixing);
            return NIL;
        }));
    }

    @Override
    public boolean isPrimaryType() {
        return true;
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return OverworldController.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "overworldcontroller";
    }
}
