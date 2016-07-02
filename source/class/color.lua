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
-- `color` objects are data representations of colors. Tailor holds
-- colors stored in RGB format.
--
-- @classmod color
---

local color = {}

---
-- Returns the RGB values stored within this `color`.
--
-- Values are between 1 and 255, inclusive.
--
-- @return[1] the R value of the color
-- @return[1] the G value of the color
-- @return[1] the B value of the color
--
function color:getRGB() end

---
-- Sets the RGB values stored within this `color`.
--
-- Values are between 1 and 255, inclusive.
--
-- @int r the new red value of the color
-- @int g the new green value of the color
-- @int b the new blue value of the color
--
function color:set(r, g, b) end

---
-- Adds to the RGB values stored within this `color`.
--
-- Values are between 1 and 255, inclusive.
--
-- @int r the red value to add
-- @int g the green value to add
-- @int b the blue value to add
--
function color:add(r, g, b) end

---
-- Multiplies the RGB values within this `color`.
--
-- @int r the red value to multiply by
-- @int g the green value to multiply by
-- @int b the blue value to multiply by
--
function color:multiply(r, g, b) end

---
-- Subtracts from RGB values within this `color`.
--
-- @int r the red value to subtract by
-- @int g the green value to subtract by
-- @int b the blue value to subtract by
--
function color:subtract(r, g, b) end

---
-- Creates and returns a copy of this `color`.
--
-- @return a clone of this color
--
function color:clone() end

---
-- Returns the value of this `color` in hex color format
-- (#RRGGBB).
-- 
-- @return the hex representation of this color
--
function color:toHex() end
