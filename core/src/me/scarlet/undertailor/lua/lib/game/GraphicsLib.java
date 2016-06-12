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

import static me.scarlet.undertailor.lua.LuaObjectValue.of;

import com.badlogic.gdx.graphics.Color;

import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.meta.LuaColorMeta;

public class GraphicsLib extends LuaLibrary {

    public GraphicsLib(MultiRenderer renderer) {
        super("graphics");

        // ---------------- renderer functions ----------------

        // graphics.getClearColor()
        registerFunction("getClearColor", vargs -> {
            return of(renderer.getClearColor());
        });

        // graphics.setClearColor(color)
        registerFunction("setClearColor", vargs -> {
            Color color = Lua.<Color>checkType(vargs.arg1(), LuaColorMeta.class).getObject();
            renderer.setClearColor(color);
            return NIL;
        });

        // ---------------- draw color functions ----------------

        // graphics.getBatchColor()
        registerFunction("getBatchColor", vargs -> {
            return of(renderer.getBatchColor());
        });

        // graphics.setBatchColor(color)
        registerFunction("setBatchColor", vargs -> {
            Color color = Lua.<Color>checkType(vargs.arg1(), LuaColorMeta.class).getObject();
            renderer.setBatchColor(color);
            return NIL;
        });

        // graphics.getShapeColor()
        registerFunction("getShapeColor", vargs -> {
            return of(renderer.getShapeColor());
        });

        // graphics.setShapeColor(color)
        registerFunction("setShapeColor", vargs -> {
            Color color = Lua.<Color>checkType(vargs.arg1(), LuaColorMeta.class).getObject();
            renderer.setShapeColor(color);
            return NIL;
        });

        // ---------------- shape draw functions ----------------

        // graphics.drawLine(x1, y1, x2, y2[, thickness])
        registerFunction("drawLine", vargs -> {
            float x1 = vargs.checknumber(1).tofloat();
            float y1 = vargs.checknumber(2).tofloat();
            float x2 = vargs.checknumber(3).tofloat();
            float y2 = vargs.checknumber(4).tofloat();
            float thickness = vargs.optnumber(5, valueOf(1)).tofloat();

            renderer.drawLine(x1, y1, x2, y2, thickness);
            return NIL;
        });

        // graphics.drawArc(x, y, radius, start, degrees[, segments])
        registerFunction("drawArc", vargs -> {
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
        });

        // graphics.drawFilledPolygon(...)
        registerFunction("drawFilledPolygon", vargs -> {
            float[] points = new float[vargs.narg()];
            for (int i = 0; i < vargs.narg(); i++) {
                points[i] = vargs.checknumber(i + 1).tofloat();
            }

            renderer.drawFilledPolygon(points);
            return NIL;
        });

        // graphics.drawPolygon(thickness, ...)
        registerFunction("drawPolygon", vargs -> {
            float thickness = vargs.checknumber(1).tofloat();
            float[] points = new float[vargs.narg() - 1];
            for (int i = 0; i < vargs.narg() - 1; i++) {
                points[i] = vargs.checknumber(i + 2).tofloat();
            }

            renderer.drawPolygon(thickness, points);
            return NIL;
        });

        // graphics.drawOpenPolygon(thickness, ...)
        registerFunction("drawOpenPolygon", vargs -> {
            float thickness = vargs.checknumber(1).tofloat();
            float[] points = new float[vargs.narg() - 1];
            for (int i = 0; i < vargs.narg() - 1; i++) {
                points[i] = vargs.checknumber(i + 2).tofloat();
            }

            renderer.drawOpenPolygon(thickness, points);
            return NIL;
        });

        // graphics.drawRectangle(x, y, width, height[, thickness])
        registerFunction("drawRectangle", vargs -> {
            float x = vargs.checknumber(1).tofloat();
            float y = vargs.checknumber(2).tofloat();
            float width = vargs.checknumber(3).tofloat();
            float height = vargs.checknumber(4).tofloat();
            float thickness = vargs.optnumber(5, valueOf(1)).tofloat();

            renderer.drawRectangle(x, y, width, height, thickness);
            return NIL;
        });

        // graphics.drawFilledRectangle(x, y, width, height)
        registerFunction("drawFilledRectangle", vargs -> {
            float x = vargs.checknumber(1).tofloat();
            float y = vargs.checknumber(2).tofloat();
            float width = vargs.checknumber(3).tofloat();
            float height = vargs.checknumber(4).tofloat();

            renderer.drawFilledRectangle(x, y, width, height);
            return NIL;
        });

        // graphics.drawCircle(x, y, radius)
        registerFunction("drawCircle", vargs -> {
            float x = vargs.checknumber(1).tofloat();
            float y = vargs.checknumber(2).tofloat();
            float radius = vargs.checknumber(3).tofloat();

            renderer.drawCircle(x, y, radius);
            return NIL;
        });

        // graphics.drawFilledCircle(x, y, radius)
        registerFunction("drawFilledCircle", vargs -> {
            float x = vargs.checknumber(1).tofloat();
            float y = vargs.checknumber(2).tofloat();
            float radius = vargs.checknumber(3).tofloat();

            renderer.drawFilledCircle(x, y, radius);
            return NIL;
        });

        // graphics.drawTriangle(x1, y1, x2, y2, x3, y3[, thickness])
        registerFunction("drawTriangle", vargs -> {
            float x1 = vargs.checknumber(1).tofloat();
            float y1 = vargs.checknumber(2).tofloat();
            float x2 = vargs.checknumber(3).tofloat();
            float y2 = vargs.checknumber(4).tofloat();
            float x3 = vargs.checknumber(5).tofloat();
            float y3 = vargs.checknumber(6).tofloat();
            float thickness = vargs.optnumber(7, valueOf(1)).tofloat();

            renderer.drawTriangle(x1, y1, x2, y2, x3, y3, thickness);
            return NIL;
        });

        // graphics.drawFilledTriangle(x1, y1, x2, y2, x3, y3[, thickness])
        registerFunction("drawTriangle", vargs -> {
            float x1 = vargs.checknumber(1).tofloat();
            float y1 = vargs.checknumber(2).tofloat();
            float x2 = vargs.checknumber(3).tofloat();
            float y2 = vargs.checknumber(4).tofloat();
            float x3 = vargs.checknumber(5).tofloat();
            float y3 = vargs.checknumber(6).tofloat();

            renderer.drawFilledTriangle(x1, y1, x2, y2, x3, y3);
            return NIL;
        });
    }
}
