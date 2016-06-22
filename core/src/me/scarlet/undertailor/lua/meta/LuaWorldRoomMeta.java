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

import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static org.luaj.vm2.LuaValue.NIL;
import static org.luaj.vm2.LuaValue.valueOf;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.engine.overworld.WorldObject;
import me.scarlet.undertailor.engine.overworld.WorldRoom;
import me.scarlet.undertailor.engine.overworld.map.TilemapFactory.Tilemap;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link WorldRoom} objects.
 */
public class LuaWorldRoomMeta implements LuaObjectMeta {

    public static final String METAKEY_MAP = "__WROOM_MAP";

    static LuaObjectValue<WorldRoom> convert(LuaValue value) {
        return Lua.checkType(value, LuaWorldRoomMeta.class);
    }

    static WorldRoom obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaWorldRoomMeta(Undertailor tailor) {
        this.metatable = new LuaTable();

        // worldRoom:registerObject(worldobject)
        set("registerObject", asFunction(vargs -> {
            WorldObject obj =
                Lua.<WorldObject>checkType(vargs.arg(2), LuaWorldObjectMeta.class).getObject();
            obj(vargs).registerObject(obj);
            return NIL;
        }));

        // worldRoom:removeObject(worldobject)
        // worldRoom:removeObject(id)
        set("removeObject", asFunction(vargs -> {
            if (vargs.isnumber(2)) { // id
                long id = vargs.checklong(2);
                obj(vargs).removeObject(id);
            } else { // object
                WorldObject obj =
                    Lua.<WorldObject>checkType(vargs.arg(2), LuaWorldObjectMeta.class).getObject();
                obj(vargs).removeObject(obj);
            }

            return NIL;
        }));

        // worldRoom:getCollisionLayerState(layerName)
        set("getCollisionLayerState", asFunction(vargs -> {
            return valueOf(obj(vargs).getCollisionLayerState(vargs.checkjstring(2)));
        }));

        // worldRoom:setCollisionLayerState(layerName, state)
        set("setCollisionLayerState", asFunction(vargs -> {
            obj(vargs).setCollisionLayerState(vargs.checkjstring(2), vargs.checkboolean(3));
            return NIL;
        }));

        // lua-only functions

        // worldRoom:setTilemap(tilemapName)
        set("setTilemap", asFunction(vargs -> {
            Tilemap tilemap =
                tailor.getAssetManager().getTilemapManager().getTilemap(vargs.checkjstring(2));
            convert(vargs.arg1()).getmetatable().set(LuaWorldRoomMeta.METAKEY_MAP,
                LuaValue.userdataOf(tilemap));
            return NIL;
        }));
    }

    @Override
    public void postMetaInit(LuaTable metatable) {
        metatable.set("process", NIL); // Processable
        metatable.set("catchEvent", NIL); // EventListener
    }

    @Override
    public boolean isPrimaryType() {
        return true;
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return WorldRoom.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "WorldRoom";
    }
}
