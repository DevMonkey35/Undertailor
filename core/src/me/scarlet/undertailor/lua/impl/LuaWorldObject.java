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

import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.engine.events.Event;
import me.scarlet.undertailor.engine.overworld.WorldObject;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.gfx.Transform;
import me.scarlet.undertailor.lua.LuaImplementable;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.ScriptManager;
import me.scarlet.undertailor.util.LuaUtil;

import java.io.File;
import java.io.FileNotFoundException;

public class LuaWorldObject extends WorldObject implements LuaImplementable<WorldObject> {

    public static final String FUNC_CREATE = "create";
    public static final String FUNC_PROCESS = "process";
    public static final String FUNC_STARTCOLLISION = "startCollision";
    public static final String FUNC_ENDCOLLISION = "endCollision";
    public static final String FUNC_PRERENDER = "preRender";
    public static final String FUNC_POSTRENDER = "postRender";
    public static final String FUNC_ONPERSIST = "onPersist";

    private LuaObjectValue<WorldObject> luaObj;

    public LuaWorldObject() {
        this.luaObj = LuaObjectValue.of(this);
        this.init();
    }

    public LuaWorldObject(ScriptManager manager, File luaFile, Object... params)
        throws FileNotFoundException, LuaScriptException {
        this(manager, luaFile, params.length > 0 ? LuaUtil.varargsOf(params) : null);
    }

    public LuaWorldObject(ScriptManager manager, File luaFile, Varargs params)
        throws FileNotFoundException, LuaScriptException {
        this();
        this.luaObj.load(manager, luaFile);

        if (this.hasFunction(FUNC_CREATE)) {
            this.invokeSelf(FUNC_CREATE, params);
        }

        this.init();
    }

    /**
     * Common initialization for implemented and plain
     * {@link LuaWorldObject}s.
     * 
     * <p>Usually, this method will only exist in an
     * implementation class should certain functions be
     * called indirectly, e.g. through an event handler.</p>
     */
    private void init() {
        this.getEventHelper().registerHandler(Event.EVT_PERSIST, evt -> {
            this.invokeSelf(FUNC_ONPERSIST, LuaUtil.varargsOf(evt.getParameters()));
            return false;
        });

        this.getEventHelper().registerHandler(Event.EVT_STARTCOLLIDE, evt -> {
            this.invokeSelf(FUNC_STARTCOLLISION, LuaUtil.varargsOf(evt.getParameters()));
            return false;
        });

        this.getEventHelper().registerHandler(Event.EVT_STOPCOLLIDE, evt -> {
            this.invokeSelf(FUNC_ENDCOLLISION, LuaUtil.varargsOf(evt.getParameters()));
            return false;
        });
    }

    @Override
    public LuaObjectValue<WorldObject> getObjectValue() {
        return this.luaObj;
    }

    @Override
    public Class<WorldObject> getPrimaryIdentifyingClass() {
        return WorldObject.class;
    }

    @Override
    public void render(float x, float y, Transform transform) {
        if (this.hasFunction(FUNC_PRERENDER)) {
            this.invokeSelf(FUNC_PRERENDER);
        }

        super.render(x, y, transform);

        if (this.hasFunction(FUNC_POSTRENDER)) {
            this.invokeSelf(FUNC_POSTRENDER);
        }
    }

    @Override
    public boolean processObject() {
        if (this.hasFunction(FUNC_PROCESS)) {
            return this.invokeSelf(FUNC_PROCESS).toboolean(1);
        }

        return false;
    }
}
