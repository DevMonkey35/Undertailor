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

package me.scarlet.undertailor.lua.lib.game;

import static me.scarlet.undertailor.lua.LuaObjectValue.orNil;
import static me.scarlet.undertailor.util.LuaUtil.asFunction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.meta.LuaColorMeta;

/**
 * Graphics library accessible by Lua.
 * 
 * <p>Wraps around {@link MultiRenderer}.</p>
 */
public class GraphicsLib extends LuaLibrary {

    public GraphicsLib(MultiRenderer renderer) {
        super("graphics");

        // ---------------- lua util ----------------

        // graphics.getDeltaTime()
        set("getDeltaTime", asFunction(vargs -> {
            return valueOf(Gdx.graphics.getRawDeltaTime());
        }));

        // ---------------- renderer functions ----------------

        // graphics.getClearColor()
        set("getClearColor", asFunction(vargs -> {
            return orNil(renderer.getClearColor());
        }));

        // graphics.setClearColor(color)
        set("setClearColor", asFunction(vargs -> {
            Color color = Lua.<Color>checkType(vargs.arg1(), LuaColorMeta.class).getObject();
            renderer.setClearColor(color);
            return NIL;
        }));

        // ---------------- draw color functions ----------------

        // graphics.getBatchColor()
        set("getBatchColor", asFunction(vargs -> {
            return orNil(renderer.getBatchColor());
        }));

        // graphics.setBatchColor(color)
        set("setBatchColor", asFunction(vargs -> {
            Color color = Lua.<Color>checkType(vargs.arg1(), LuaColorMeta.class).getObject();
            renderer.setBatchColor(color);
            return NIL;
        }));

        // graphics.getShapeColor()
        set("getShapeColor", asFunction(vargs -> {
            return orNil(renderer.getShapeColor());
        }));

        // graphics.setShapeColor(color)
        set("setShapeColor", asFunction(vargs -> {
            Color color = Lua.<Color>checkType(vargs.arg1(), LuaColorMeta.class).getObject();
            renderer.setShapeColor(color);
            return NIL;
        }));

        // ---------------- shape draw functions ----------------

        // graphics.drawLine(x1, y1, x2, y2[, thickness])
        set("drawLine", asFunction(vargs -> {
            float x1 = vargs.checknumber(1).tofloat();
            float y1 = vargs.checknumber(2).tofloat();
            float x2 = vargs.checknumber(3).tofloat();
            float y2 = vargs.checknumber(4).tofloat();
            float thickness = vargs.optnumber(5, valueOf(1)).tofloat();

            renderer.drawLine(x1, y1, x2, y2, thickness);
            return NIL;
        }));

        // graphics.drawArc(x, y, radius, start, degrees[, segments])
        set("drawArc", asFunction(vargs -> {
            float x = vargs.checknumber(1).tofloat();
            float y = vargs.checknumber(2).tofloat();
            float radius = vargs.checknumber(3).tofloat();
            float start = vargs.checknumber(4).tofloat();
            float degrees = vargs.checknumber(5).tofloat();
            int segments = vargs.optint(6, 0);

            if (segments <= 0) {
                renderer.drawArc(x, y, radius, start, degrees);
            } else {
                renderer.drawArc(x, y, radius, start, degrees, segments);
            }

            return NIL;
        }));

        // graphics.drawFilledPolygon(...)
        set("drawFilledPolygon", asFunction(vargs -> {
            float[] points = new float[vargs.narg()];
            for (int i = 0; i < vargs.narg(); i++) {
                points[i] = vargs.checknumber(i + 1).tofloat();
            }

            renderer.drawFilledPolygon(points);
            return NIL;
        }));

        // graphics.drawPolygon(thickness, ...)
        set("drawPolygon", asFunction(vargs -> {
            float thickness = vargs.checknumber(1).tofloat();
            float[] points = new float[vargs.narg() - 1];
            for (int i = 0; i < vargs.narg() - 1; i++) {
                points[i] = vargs.checknumber(i + 2).tofloat();
            }

            renderer.drawPolygon(thickness, points);
            return NIL;
        }));

        // graphics.drawOpenPolygon(thickness, ...)
        set("drawOpenPolygon", asFunction(vargs -> {
            float thickness = vargs.checknumber(1).tofloat();
            float[] points = new float[vargs.narg() - 1];
            for (int i = 0; i < vargs.narg() - 1; i++) {
                points[i] = vargs.checknumber(i + 2).tofloat();
            }

            renderer.drawOpenPolygon(thickness, points);
            return NIL;
        }));

        // graphics.drawRectangle(x, y, width, height[, thickness])
        set("drawRectangle", asFunction(vargs -> {
            float x = vargs.checknumber(1).tofloat();
            float y = vargs.checknumber(2).tofloat();
            float width = vargs.checknumber(3).tofloat();
            float height = vargs.checknumber(4).tofloat();
            float thickness = vargs.optnumber(5, valueOf(1)).tofloat();

            renderer.drawRectangle(x, y, width, height, thickness);
            return NIL;
        }));

        // graphics.drawFilledRectangle(x, y, width, height)
        set("drawFilledRectangle", asFunction(vargs -> {
            float x = vargs.checknumber(1).tofloat();
            float y = vargs.checknumber(2).tofloat();
            float width = vargs.checknumber(3).tofloat();
            float height = vargs.checknumber(4).tofloat();

            renderer.drawFilledRectangle(x, y, width, height);
            return NIL;
        }));

        // graphics.drawCircle(x, y, radius)
        set("drawCircle", asFunction(vargs -> {
            float x = vargs.checknumber(1).tofloat();
            float y = vargs.checknumber(2).tofloat();
            float radius = vargs.checknumber(3).tofloat();

            renderer.drawCircle(x, y, radius);
            return NIL;
        }));

        // graphics.drawFilledCircle(x, y, radius)
        set("drawFilledCircle", asFunction(vargs -> {
            float x = vargs.checknumber(1).tofloat();
            float y = vargs.checknumber(2).tofloat();
            float radius = vargs.checknumber(3).tofloat();

            renderer.drawFilledCircle(x, y, radius);
            return NIL;
        }));

        // graphics.drawTriangle(x1, y1, x2, y2, x3, y3[, thickness])
        set("drawTriangle", asFunction(vargs -> {
            float x1 = vargs.checknumber(1).tofloat();
            float y1 = vargs.checknumber(2).tofloat();
            float x2 = vargs.checknumber(3).tofloat();
            float y2 = vargs.checknumber(4).tofloat();
            float x3 = vargs.checknumber(5).tofloat();
            float y3 = vargs.checknumber(6).tofloat();
            float thickness = vargs.optnumber(7, valueOf(1)).tofloat();

            renderer.drawTriangle(x1, y1, x2, y2, x3, y3, thickness);
            return NIL;
        }));

        // graphics.drawFilledTriangle(x1, y1, x2, y2, x3, y3[, thickness])
        set("drawTriangle", asFunction(vargs -> {
            float x1 = vargs.checknumber(1).tofloat();
            float y1 = vargs.checknumber(2).tofloat();
            float x2 = vargs.checknumber(3).tofloat();
            float y2 = vargs.checknumber(4).tofloat();
            float x3 = vargs.checknumber(5).tofloat();
            float y3 = vargs.checknumber(6).tofloat();

            renderer.drawFilledTriangle(x1, y1, x2, y2, x3, y3);
            return NIL;
        }));
    }
}
