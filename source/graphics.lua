--[[
	
	The MIT License (MIT)

	Copyright (c) 2016 Tellerva, Marc Lawrence

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
	
]]--


---
-- `graphics` library for interfacing with graphics and drawing
-- shapes.
--
-- @module game.graphics
-- @alias graphics
---

local graphics = {}

---
-- Retrieval functions.
-- @section
--

---
-- Returns the time passed since the last frame, in seconds.
--
-- @return the delta time in seconds
--
function graphics.getDeltaTime() end

---
-- Returns the `sprite` from the provided sheet and index.
--
-- @string sheetName the name of the sheet to pull from
-- @string name the name of the sprite in the sheet
--
-- @return the target sprite, or nil if unknown
--
function graphics.getSprite(sheetName, name) end

---
-- Returns the default `transform` object.
--
-- @return the default transform
--
function graphics.getDefaultTransform() end

---
-- Color functions.
-- @section
--

---
-- Returns the game's current clear `color`.
--
-- Essentially, the game's background color. On each frame rendered,
-- the screen is cleared using this color before redrawing everything
-- else.
--
-- Defaults to black (#000000).
--
-- @return the current clear color
--
function graphics.getClearColor() end

---
-- Sets the game's current clear `color`.
--
-- See `graphics.getClearColor` for a brief description of clear
-- color.
--
-- @tparam color color the color to set
--
function graphics.setClearColor(color) end

---
-- Returns the game's current sprite `color`.
--
-- Sprite color applies a shade on any sprites drawn in the future,
-- should the color be anything other than white.
--
-- Defaults to white (#FFFFFF).
--
-- @return the current sprite color
--
function graphics.getSpriteColor() end

---
-- Sets the game's current sprite `color`.
--
-- See `graphics.getSpriteColor` for a brief description of sprite
-- color.
--
-- @tparam color color the color to set
--
function graphics.setSpriteColor(color) end

---
-- Returns the game's current shape `color`.
--
-- Shape color will be the color for any shapes drawn in the future.
--
-- Defaults to white (#FFFFFF).
--
-- @return the current shape color
--
function graphics.getShapeColor() end

---
-- Sets the game's current shape `color`.
--
-- See `graphics.getShapeColor` for a brief description of shape
-- color.
--
-- @tparam color color the color to set
--
function graphics.setShapeColor(color) end

---
-- Resets all graphic colors to their default values.
--
-- This is always called at the start of every frame before drawing.
-- Do not call this function whenever something starts to get
-- rendered, if a specific color is needed simply set the target
-- color, the function will check on its own whether or not it needs
-- to change the color. Using this function to ensure the default
-- color will slow down rendering, as it causes the game to flush any
-- graphics to be drawn, which is slower than sending graphics all at
-- once.
--
function graphics.resetColors() end

---
-- Shape drawing.
-- @section
--

---
-- Draws a line between the given points.
--
-- @number x1 the x-coordinate of the first point
-- @number y1 the y-coordinate of the first point
-- @number x2 the x-coordinate of the second point
-- @number y2 the y-coordinate of the second point
-- @number[opt=1.0] thickness the thickness of the line, in pixels
--
function graphics.drawLine(x1, y1, x2, y2, thickness) end

---
-- Draws an outline of an arc centered at the given point.
--
-- @number x the x-coordinate of the arc's center
-- @number y the y-coordinate of the arc's center
-- @number radius the radius of the circle the arc outlines
-- @number startDeg the degrees at which the arc will start, with 0
--   being at the right of the circle
-- @number lengthDeg the length of the arc in degrees, starting from
--   `startDeg` going counter-clockwise
-- @number[opt=-1] segments the smoothness of the arc, or -1 to
--   automatically calculate
--
function graphics.drawArc(x, y, radius, startDeg, lengthDeg, segments) end

---
-- Draws a filled arc centered at the given point.
--
-- @number x the x-coordinate of the arc's center
-- @number y the y-coordinate of the arc's center
-- @number radius the radius of the circle the arc outlines
-- @number startDeg the degrees at which the arc will start, with 0
--   being at the right of the circle
-- @number lengthDeg the length of the arc in degrees, starting from
--   `startDeg` going counter-clockwise
-- @number[opt=-1] segments the smoothness of the arc, or -1 to
--   automatically calculate
--
function graphics.drawFilledArc(x, y, radius, startDeg, lengthDeg, segments) end

---
-- Draws an outline of a polygon. The outline is closed.
--
-- Points are provided by listing out values, alternating between
-- x and y.
--
-- The game will ignore draw calls attempting to draw polygons
-- with less than 3 edges (6 points).
--
-- @usage -- Drawing a triangle
-- graphics.drawPolygon(1.0, -- thickness
-- 0.0, 5.0,   -- top
-- -5.0, -5.0, -- bottom left
-- 5.0, -5.0)  -- bottom right
--
-- @number thickness the thickness of the outline, in pixels
-- @tparam ... vargs the points of the polygon
--
function graphics.drawPolygon(thickness, ...) end

---
-- Draws an outline of a polygon. The outline is left open, meaning
-- there is no line drawn between the first point and the last point.
--
-- Points are provided by listing out values, alternating between
-- x and y.
--
-- The game will ignore draw calls attempting to draw polygons
-- with less than 3 edges (6 points).
--
-- @usage -- Drawing a triangle
-- graphics.drawOpenPolygon(1.0, -- thickness
-- 0.0, 5.0,   -- top
-- -5.0, -5.0, -- bottom left
-- 5.0, -5.0)  -- bottom right
--
-- @number thickness the thickness of the outline, in pixels
-- @tparam ... vargs the points of the polygon
--
function graphics.drawOpenPolygon(thickness, ...) end

---
-- Draws a filled polygon.
--
-- Points are provided by listing out values, alternating between
-- x and y.
--
-- The game will ignore draw calls attempting to draw polygons
-- with less than 3 edges (6 points).
--
-- @usage -- Drawing a triangle
-- graphics.drawFilledPolygon(
-- 0.0, 5.0,   -- top
-- -5.0, -5.0, -- bottom left
-- 5.0, -5.0)  -- bottom right
--
-- @tparam ... vargs the points of the polygon
--
function graphics.drawFilledPolygon(...) end

---
-- Draws an outline of a rectangle.
--
-- @number x the x-coordinate of the bottom-left of the rectangle
-- @number y the y-coordinate of the bottom-left of the rectangle
-- @number width the width of the rectangle, in pixels
-- @number height the height of the rectangle, in pixels
-- @number[opt=1.0] thickness the thickness of the outline,
--   in pixels
--
function graphics.drawRectangle(x, y, width, height, thickness) end

---
-- Draws a filled rectangle.
--
-- @number x the x-coordinate of the bottom-left of the rectangle
-- @number y the y-coordinate of the bottom-left of the rectangle
-- @number width the width of the rectangle, in pixels
-- @number height the height of the rectangle, in pixels
--
function graphics.drawFilledRectangle(x, y, width, height) end

---
-- Draws an outline of a circle.
--
-- @number x the x-coordinate of the circle's center
-- @number y the y-coordinate of the circle's center
-- @number radius the radius of the circle
--
function graphics.drawCircle(x, y, radius) end

---
-- Draws a filled circle.
--
-- @number x the x-coordinate of the circle's center
-- @number y the y-coordinate of the circle's center
-- @number radius the radius of the circle
--
function graphics.drawFilledCircle(x, y, radius) end

---
-- Draws an outline of a triangle.
--
-- @number x1 the x-coordinate of the triangle's first point
-- @number y1 the y-coordinate of the triangle's first point
-- @number x2 the x-coordinate of the triangle's second point
-- @number y2 the y-coordinate of the triangle's second point
-- @number x3 the x-coordinate of the triangle's third point
-- @number y3 the y-coordinate of the triangle's third point
-- @number[opt=1.0] thickness the thickness of the outline, in pixels
--
function graphics.drawTriangle(x1, y1, x2, y2, x3, y3, thickness) end

---
-- Draws a filled triangle.
--
-- @number x1 the x-coordinate of the triangle's first point
-- @number y1 the y-coordinate of the triangle's first point
-- @number x2 the x-coordinate of the triangle's second point
-- @number y2 the y-coordinate of the triangle's second point
-- @number x3 the x-coordinate of the triangle's third point
-- @number y3 the y-coordinate of the triangle's third point
--
function graphics.drawFilledTriangle(x1, y1, x2, y2, x3, y3) end
