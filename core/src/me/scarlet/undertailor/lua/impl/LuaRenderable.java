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

import static me.scarlet.undertailor.lua.LuaObjectValue.orNil;
import static org.luaj.vm2.LuaValue.valueOf;

import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;
import me.scarlet.undertailor.lua.LuaImplementable;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.ScriptManager;

import java.io.File;
import java.io.FileNotFoundException;

public class LuaRenderable implements LuaImplementable<Renderable>, Renderable {

    public static final String FUNC_CREATE = "create";
    public static final String FUNC_DRAW = "draw";

    private Transform transform;
    private LuaObjectValue<Renderable> luaObj;

    public LuaRenderable() {
        this.transform = new Transform();
        this.luaObj = LuaObjectValue.of(this);
    }

    public LuaRenderable(ScriptManager manager, File luaFile)
        throws FileNotFoundException, LuaScriptException {
        this();
        this.luaObj.load(manager, luaFile);

        if(this.hasFunction(FUNC_CREATE)) {
            this.invokeSelf(FUNC_CREATE);
        }
    }

    @Override
    public LuaObjectValue<Renderable> getObjectValue() {
        return this.luaObj;
    }

    @Override
    public Class<Renderable> getPrimaryIdentifyingClass() {
        return Renderable.class;
    }

    @Override
    public Transform getTransform() {
        return this.transform;
    }

    @Override
    public void setTransform(Transform transform) {
        Transform.setOrDefault(this.transform, transform);
    }

    @Override
    public void draw(float x, float y, Transform transform) {
        if(this.hasFunction(FUNC_DRAW)) {
            this.invokeSelf(FUNC_DRAW, valueOf(x), valueOf(y), orNil(transform));
        }
    }

}
