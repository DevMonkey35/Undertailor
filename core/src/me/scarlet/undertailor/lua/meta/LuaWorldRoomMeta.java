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
import static me.scarlet.undertailor.util.LuaUtil.arrayOf;
import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static me.scarlet.undertailor.util.LuaUtil.varargsOf;
import static org.luaj.vm2.LuaValue.NIL;
import static org.luaj.vm2.LuaValue.valueOf;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.AssetManager;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.engine.overworld.Entrypoint;
import me.scarlet.undertailor.engine.overworld.WorldObject;
import me.scarlet.undertailor.engine.overworld.WorldRoom;
import me.scarlet.undertailor.engine.overworld.map.ObjectLayer.ShapeData;
import me.scarlet.undertailor.engine.overworld.map.TilemapFactory.Tilemap;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.impl.LuaWorldRoom;
import me.scarlet.undertailor.util.LuaUtil;

import java.io.File;
import java.util.function.Supplier;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link WorldRoom} objects.
 */
public class LuaWorldRoomMeta implements LuaObjectMeta {

    public static final String METAKEY_MAP = "__WROOM_MAP";

    public static LuaObjectValue<WorldRoom> convert(LuaValue value) {
        return Lua.checkType(value, LuaWorldRoomMeta.class);
    }

    static WorldRoom obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaWorldRoomMeta(Undertailor tailor) {
        this.metatable = new LuaTable();

        // worldRoom:getOverworld()
        set("getOverworld", asFunction(vargs -> {
            return orNil(obj(vargs).getOverworld());
        }));

        // worldRoom:registerEntrypoint(defPoint[, target room file, target entrypoint])
        // worldRoom:registerEntrypoint(defPoint[, target room file, target x, target y])
        set("registerEntrypoint", asFunction(vargs -> {
            String defPoint = vargs.checkjstring(2);
            String targetRoom = vargs.optjstring(3, null);

            String targetPoint = null;
            // or
            float spawnPointX = 0F;
            float spawnPointY = 0F;

            Supplier<WorldRoom> targetRoomSup = null;
            if (vargs.narg() == 5) { // named their own point
                spawnPointX = vargs.checknumber(4).tofloat();
                spawnPointY = vargs.checknumber(5).tofloat();
            } else { // named a def point
                targetPoint = vargs.optjstring(4, null);
            }

            if (targetRoom != null) {
                targetRoomSup = () -> {
                    try {
                        return new LuaWorldRoom(tailor.getAssetManager().getScriptManager(),
                            new File(AssetManager.rootDirectory, targetRoom));
                    } catch (Exception e) {
                        Lua.error("Failed to load WorldRoom", e);
                    }

                    return null;
                };
            }

            Entrypoint point;
            if (targetRoom == null) {
                point = new Entrypoint(defPoint, defPoint);
            } else {
                if (targetPoint != null) {
                    point = new Entrypoint(defPoint, defPoint, targetRoomSup, targetPoint);
                } else {
                    point =
                        new Entrypoint(defPoint, defPoint, targetRoomSup, spawnPointX, spawnPointY);
                }
            }

            obj(vargs).registerEntrypoint(point);
            return NIL;
        }));

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

        // worldRoom:getLayerOpacity(layer)
        set("getLayerOpacity", asFunction(vargs -> {
            return valueOf(obj(vargs).getLayerOpacity(vargs.checknumber(2).toshort()));
        }));

        // worldRoom:setLayerOpacity(layer, opacity)
        set("setLayerOpacity", asFunction(vargs -> {
            obj(vargs).setLayerOpacity(vargs.checknumber(2).toshort(),
                vargs.checknumber(3).tofloat());
            return NIL;
        }));

        // lua-only functions

        /*
         * Shape data is weird in Lua, as we don't wanna
         * just make it another LuaObjectValue with its own
         * meta because its just a databag class.
         * 
         * We just try to represent it as a string and 4
         * values. The values are listed here.
         * 
         * When returned as vargs, the first value is the
         * type of shape. Possible values include rectangle,
         * circle, polygon, polyline.
         * 
         * The second and third value are the X and Y
         * positions of the shape's origin point, as
         * presented in Tiled. Y value is unlikely to match
         * the one defined by Tiled, and is instead
         * converted, as the screen origin for libGDX is on
         * the bottom left while Tiled puts it on the top
         * left.
         * 
         * The fourth value can be a number or a table. It
         * is only a number if the shape is a perfect
         * circle, that is, its height matches its width.
         * The number represents the radius of the perfect
         * circle. If the shape was not a perfect circle,
         * then the second value is a table containing the
         * vertices of the shape.
         */
        // worldRoom:getMapDefinedShape(defShapeName)
        set("getMapDefinedShape", asFunction(vargs -> {
            String[] shapeName = vargs.checkjstring(2).split(":");
            if (shapeName.length < 2) {
                throw new LuaError("shape name must be in \"defLayerName:shapeName\" form");
            }

            WorldRoom room = obj(vargs);
            if (room == null) {
                return NIL;
            }

            Tilemap map = room.getMap();
            if (map == null) {
                return NIL;
            }

            ShapeData shape = map.getDefinedShape(shapeName[0], shapeName[1]);
            if (shape == null) {
                return NIL;
            }

            String shapeTypeName;
            LuaValue fourth;
            if (shape.getType() == Shape.Type.Circle) {
                shapeTypeName = "circle";
                fourth = valueOf(shape.getRadius());
            } else {
                if (shape.getType() == null) {
                    shapeTypeName = "rectangle";
                } else if (shape.getType() == Shape.Type.Polygon) {
                    shapeTypeName = "polygon";
                } else { // chain/edge
                    shapeTypeName = "polyline";
                }

                float[] vertices = shape.getVertices();
                LuaValue[] lVertices = new LuaValue[vertices.length];
                for (int i = 0; i < vertices.length; i++) {
                    lVertices[i] = valueOf(vertices[i]);
                }

                fourth = LuaUtil.arrayOf(lVertices);
            }

            return arrayOf(valueOf(shapeTypeName), valueOf(shape.getPosition().x),
                valueOf(shape.getPosition().y), fourth);
        }));

        // worldRoom:getMapDefinedPoint(pointName)
        set("getMapDefinedPoint", asFunction(vargs -> {
            String[] pointName = vargs.checkjstring(2).split(":");
            if (pointName.length < 2) {
                throw new LuaError("point name must be in \"defLayerName:pointName\" form");
            }
            WorldRoom room = obj(vargs);
            if (room == null) {
                return NIL;
            }

            Tilemap map = room.getMap();
            if (map == null) {
                return NIL;
            }

            Vector2 point = map.getDefinedPoint(pointName[0], pointName[1]);
            if (point == null) {
                return NIL;
            }

            return varargsOf(point.x, point.y);
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
        return "worldroom";
    }
}
