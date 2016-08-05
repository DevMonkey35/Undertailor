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

import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.engine.ui.UIComponent;
import me.scarlet.undertailor.engine.ui.UIObject;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.gfx.Transform;
import me.scarlet.undertailor.lua.LuaImplementable;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.ScriptManager;
import me.scarlet.undertailor.util.LuaUtil;

import java.io.File;
import java.io.FileNotFoundException;

public class LuaUIComponent extends UIComponent implements LuaImplementable<UIComponent> {

    public static final String FUNC_CREATE = "create";
    public static final String FUNC_RENDER = "render";
    public static final String FUNC_PROCESS = "process";
    public static final String FUNC_ONCLAIM = "onClaim";

    private LuaObjectValue<UIComponent> luaObj;

    public LuaUIComponent() {
        this.luaObj = LuaObjectValue.of(this);
    }

    public LuaUIComponent(ScriptManager manager, File luaFile, Object... params)
        throws FileNotFoundException, LuaScriptException {
        this(manager, luaFile, params.length > 0 ? LuaUtil.varargsOf(params) : null);
    }

    public LuaUIComponent(ScriptManager manager, File luaFile, Varargs params)
        throws FileNotFoundException, LuaScriptException {
        this();
        this.luaObj.load(manager, luaFile);

        if (this.hasFunction(FUNC_CREATE)) {
            this.invokeSelf(FUNC_CREATE, params);
        }
    }

    @Override
    public LuaObjectValue<UIComponent> getObjectValue() {
        return this.luaObj;
    }

    @Override
    public Class<UIComponent> getPrimaryIdentifyingClass() {
        return UIComponent.class;
    }

    @Override
    public void onClaim(UIObject parent) {
        if (this.hasFunction(FUNC_ONCLAIM)) {
            this.invokeSelf(FUNC_ONCLAIM, orNil(parent));
        }
    }

    @Override
    protected boolean processComponent() {
        if (this.hasFunction(FUNC_PROCESS)) {
            return this.invokeSelf(FUNC_PROCESS).toboolean(1);
        }

        return false;
    }

    @Override
    public void render(float x, float y, Transform transform) {
        if (this.hasFunction(FUNC_RENDER)) {
            this.invokeSelf(FUNC_RENDER, valueOf(x), valueOf(y), orNil(transform));
        }
    }
}
