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

    public LuaRenderable(ScriptManager manager, File luaFile)
        throws FileNotFoundException, LuaScriptException {
        this.transform = new Transform();
        this.luaObj = LuaObjectValue.of(this);
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
