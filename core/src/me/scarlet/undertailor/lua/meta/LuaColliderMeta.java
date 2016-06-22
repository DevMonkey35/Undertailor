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
import static me.scarlet.undertailor.util.LuaUtil.varargsOf;
import static org.luaj.vm2.LuaValue.NIL;
import static org.luaj.vm2.LuaValue.valueOf;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.engine.Collider;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link Collider} objects.
 */
public class LuaColliderMeta implements LuaObjectMeta {

    static LuaObjectValue<Collider> convert(LuaValue value) {
        return Lua.checkType(value, LuaColliderMeta.class);
    }

    static Collider obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaColliderMeta() {
        this.metatable = new LuaTable();

        // collider:getBodyType()
        set("getColliderType", asFunction(vargs -> {
            return valueOf(obj(vargs).getColliderType().getValue());
        }));

        // collider:setColliderType()
        set("setColliderType", asFunction(vargs -> {
            Collider col = obj(vargs);
            int type = vargs.arg(2).checkint();
            BodyType setType = BodyType.DynamicBody; // default
            for (BodyType typeEntry : BodyType.values()) {
                if (typeEntry.getValue() == type) {
                    setType = typeEntry;
                    break;
                }
            }

            col.setColliderType(setType);
            return NIL;
        }));

        // collider:getVelocity()
        set("getVelocity", asFunction(vargs -> {
            Vector2 vel = obj(vargs).getVelocity();
            return varargsOf(valueOf(vel.x), valueOf(vel.y));
        }));

        // collider:setVelocity(x, y)
        set("setVelocity", asFunction(vargs -> {
            Collider col = obj(vargs);
            Vector2 vel = col.getVelocity();
            float velX = vargs.isnil(2) ? vel.x : vargs.checknumber(2).tofloat();
            float velY = vargs.isnil(3) ? vel.y : vargs.checknumber(3).tofloat();
            col.setVelocity(velX, velY);
            return NIL;
        }));

        // collider:applyForce(forceX, forceY[, localX, localY])
        set("applyForce", asFunction(vargs -> {
            Collider col = obj(vargs);
            float forceX = vargs.checknumber(2).tofloat();
            float forceY = vargs.checknumber(3).tofloat();
            float localX = 0;
            float localY = 0;

            if (vargs.narg() > 3) {
                localX = vargs.checknumber(4).tofloat();
                localY = vargs.isnil(5) ? 0 : vargs.checknumber(5).tofloat();
            }

            col.applyForce(forceX, forceY, localX, localY);
            return NIL;
        }));

        // collider:applyImpulse(impX, impY[, localX, localY])
        set("applyImpulse", asFunction(vargs -> {
            Collider col = obj(vargs);
            float impX = vargs.checknumber(2).tofloat();
            float impY = vargs.checknumber(3).tofloat();
            float localX = 0;
            float localY = 0;

            if (vargs.narg() > 3) {
                localX = vargs.checknumber(4).tofloat();
                localY = vargs.isnil(5) ? 0 : vargs.checknumber(5).tofloat();
            }

            col.applyImpulse(impX, impY, localX, localY);
            return NIL;
        }));

        // collider:applyTorque(torque)
        set("applyTorque", asFunction(vargs -> {
            Collider col = obj(vargs);
            float torque = vargs.checknumber(2).tofloat();

            col.applyTorque(torque);
            return NIL;
        }));

        // collider:canCollide()
        set("canCollide", asFunction(vargs -> {
            return valueOf(obj(vargs).canCollide());
        }));

        // collider:setCanCollide(canCollide)
        set("setCanCollide", asFunction(vargs -> {
            boolean canCollide = vargs.checkboolean(2);
            obj(vargs).setCanCollide(canCollide);
            return NIL;
        }));

        // collider:isRotationFixed()
        set("isRotationFixed", asFunction(vargs -> {
            return valueOf(obj(vargs).isRotationFixed());
        }));

        // collider:setRotationFixed(rotationFixed)
        set("setRotationFixed", asFunction(vargs -> {
            obj(vargs).setRotationFixed(vargs.checkboolean(2));
            return NIL;
        }));

        // collider:getGroupId()
        set("getGroupId", asFunction(vargs -> {
            return valueOf(obj(vargs).getGroupId());
        }));

        // collider:setGroupId(id)
        set("setGroupId", asFunction(vargs -> {
            obj(vargs).setGroupId(vargs.arg(2).checkinteger().toshort());
            return NIL;
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return Collider.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "collider";
    }
}
