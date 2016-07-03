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

import static me.scarlet.undertailor.lua.Lua.checkType;
import static me.scarlet.undertailor.lua.LuaObjectValue.orNil;
import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static org.luaj.vm2.LuaValue.NIL;
import static org.luaj.vm2.LuaValue.valueOf;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.engine.overworld.OverworldController;
import me.scarlet.undertailor.engine.overworld.WorldObject;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link WorldObject} objects.
 */
public class LuaWorldObjectMeta implements LuaObjectMeta {

    public static LuaObjectValue<WorldObject> convert(LuaValue value) {
        return Lua.checkType(value, LuaWorldObjectMeta.class);
    }

    static WorldObject obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaWorldObjectMeta() {
        this.metatable = new LuaTable();

        // worldObject:getRoom()
        set("getRoom", asFunction(vargs -> {
            return orNil(obj(vargs).getRoom());
        }));

        // worldObject:getActor()
        set("getActor", asFunction(vargs -> {
            return orNil(obj(vargs).getActor());
        }));

        // worldObject:setActor(actor)
        set("setActor", asFunction(vargs -> {
            Renderable renderable =
                (Renderable) checkType(vargs.arg(2), LuaRenderableMeta.class).getObject();
            obj(vargs).setActor(renderable);
            return NIL;
        }));

        // worldObject:isVisible()
        set("isVisible", asFunction(vargs -> {
            return valueOf(obj(vargs).isVisible());
        }));

        // worldObject:setVisible(visible)
        set("setVisible", asFunction(vargs -> {
            obj(vargs).setVisible(vargs.checkboolean(2));
            return NIL;
        }));

        // lua-only functions

        // bounding shape creation methods respect overworld scale
        // worldObject:addBoundingPolygon(...)
        set("addBoundingPolygon", asFunction(vargs -> {
            WorldObject obj = obj(vargs);
            if (vargs.narg() % 2 != 0) {
                throw new LuaError("uneven points");
            }

            if(vargs.narg() < 6) {
                throw new LuaError("Polygons must have at least 3 points");
            }

            if (vargs.narg() > 16) {
                throw new LuaError(
                    "libGDX's Box2D will not permit polygons with more than 8 vertices");
            }

            float[] vertices = new float[vargs.narg()];
            for (int i = 0; i < vargs.narg(); i++) {
                vertices[i] =
                    vargs.checknumber(i + 1).tofloat() * OverworldController.PIXELS_TO_METERS;
            }

            PolygonShape polygon = new PolygonShape();
            polygon.set(vertices);
            obj.queueBoundingShape(polygon);

            return NIL;
        }));

        // worldObject:addBoundingCircle(radius[, offsetX, offsetY])
        set("addBoundingCircle", asFunction(vargs -> {
            WorldObject obj = obj(vargs);
            float radius = vargs.checknumber(2).tofloat() * OverworldController.PIXELS_TO_METERS;
            Vector2 offset = new Vector2();
            offset.x = vargs.isnil(3) ? 0
                : (vargs.checknumber(3).tofloat() * OverworldController.PIXELS_TO_METERS);
            offset.y = vargs.isnil(4) ? 0
                : (vargs.checknumber(4).tofloat() * OverworldController.PIXELS_TO_METERS);

            CircleShape circle = new CircleShape();
            circle.setRadius(radius);
            circle.setPosition(offset);
            obj.queueBoundingShape(circle);

            return NIL;
        }));

        // worldObject:addBoundingBox(width, height[, offsetX, offsetY])
        set("addBoundingBox", asFunction(vargs -> {
            WorldObject obj = obj(vargs);
            float width = vargs.checknumber(2).tofloat() * OverworldController.PIXELS_TO_METERS;
            float height = vargs.checknumber(3).tofloat() * OverworldController.PIXELS_TO_METERS;
            Vector2 offset = new Vector2();
            offset.x = vargs.isnil(3) ? 0
                : (vargs.checknumber(3).tofloat() * OverworldController.PIXELS_TO_METERS);
            offset.y = vargs.isnil(4) ? 0
                : (vargs.checknumber(4).tofloat() * OverworldController.PIXELS_TO_METERS);
            // setAsBox seems to offset itself to have the box centered when used without an offset param, so we do this too
            offset.x -= width;

            PolygonShape box = new PolygonShape();
            box.setAsBox(width / 2F, height / 2F, offset, 0F);
            obj.queueBoundingShape(box);

            return NIL;
        }));

        // worldObject:addBoundingChain(...)
        set("addBoundingChain", asFunction(vargs -> {
            WorldObject obj = obj(vargs);

            float[] vertices = new float[vargs.narg()];
            for (int i = 0; i < vargs.narg(); i++) {
                vertices[i] =
                    vargs.checknumber(i + 1).tofloat() * OverworldController.PIXELS_TO_METERS;
            }

            ChainShape chain = new ChainShape();
            chain.createChain(vertices);
            obj.queueBoundingShape(chain);

            return NIL;
        }));

        // worldObject:addBoundingChainLoop(...)
        set("addBoundingChainLoop", asFunction(vargs -> {
            WorldObject obj = obj(vargs);

            float[] vertices = new float[vargs.narg()];
            for (int i = 0; i < vargs.narg(); i++) {
                vertices[i] =
                    vargs.checknumber(i + 1).tofloat() * OverworldController.PIXELS_TO_METERS;
            }

            ChainShape chain = new ChainShape();
            chain.createLoop(vertices);
            obj.queueBoundingShape(chain);

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
        return WorldObject.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "worldobject";
    }
}
