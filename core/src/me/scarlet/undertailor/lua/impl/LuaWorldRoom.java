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

package me.scarlet.undertailor.lua.impl;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.engine.overworld.WorldRoom;
import me.scarlet.undertailor.engine.overworld.map.TilemapFactory.Tilemap;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.LuaImplementable;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.ScriptManager;
import me.scarlet.undertailor.util.LuaUtil;

import java.io.File;
import java.io.FileNotFoundException;

public class LuaWorldRoom extends WorldRoom implements LuaImplementable<WorldRoom> {

    public static final String FIELD_TILEMAP = "__map";

    public static final String FUNC_CREATE = "create";
    public static final String FUNC_PROCESS = "process";
    public static final String FUNC_CATCHEVENT = "catchEvent";

    private LuaObjectValue<WorldRoom> luaObj;

    public LuaWorldRoom(ScriptManager manager, File luaFile, Object... params)
        throws FileNotFoundException, LuaScriptException {
        this(manager, luaFile, params.length > 0 ? LuaUtil.varargsOf(params) : null);
    }

    public LuaWorldRoom(ScriptManager manager, File luaFile, Varargs params)
        throws FileNotFoundException, LuaScriptException {
        super(null);

        this.luaObj = LuaObjectValue.of(this);
        this.luaObj.load(manager, luaFile);

        String loadedMap = this.luaObj.get(FIELD_TILEMAP).optjstring(null);
        if (loadedMap != null) {
            Tilemap map = Undertailor.getInstance().getAssetManager().getTilemapManager()
                .getTilemap(loadedMap);
            if (map == null) {
                throw new LuaError("unknown tilemap " + loadedMap);
            }

            super.tilemap = map;
        }
    }

    @Override
    public Class<WorldRoom> getPrimaryIdentifyingClass() {
        return WorldRoom.class;
    }

    @Override
    public LuaObjectValue<WorldRoom> getObjectValue() {
        return this.luaObj;
    }

    @Override
    public void onLoad() {
        if (this.hasFunction(FUNC_CREATE)) {
            this.invokeSelf(FUNC_CREATE);
        }
    };

    @Override
    public void processRoom() {
        if (this.hasFunction(FUNC_PROCESS)) {
            this.invokeSelf(FUNC_PROCESS);
        }
    }

    @Override
    public boolean catchEvent(String eventName, Object... data) {
        if (this.hasFunction(FUNC_CATCHEVENT)) {
            return this.invokeSelf(FUNC_CATCHEVENT, LuaUtil.varargsOf(data)).toboolean(1);
        }

        return false;
    }
}
