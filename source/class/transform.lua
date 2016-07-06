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
-- `transform` objects are databag classes used to dictate how a
-- `renderable` object should be drawn.
--
-- @classmod transform
---

local transform = {}

-- getters

---
-- Returns the horizontal scale to draw with.
--
-- @return the horizontal scale
--
function transform:getScaleX() end

---
-- Returns the vertical scale to draw with.
--
-- @return the vertical scale
--
function transform:getScaleY() end

---
-- Returns whether or not the rendered object should be flipped
-- horizontally.
--
-- @return if the rendered object is horizontally flipped
--
function transform:getFlipX() end

---
-- Returns whether or not the rendered object should be flipped
-- vertically.
--
-- @return if the rendered object is vertically flipped
--
function transform:getFlipY() end

---
-- Returns the rotation to draw with, in degrees.
--
-- @return the rotation
--
function transform:getRotation() end

-- setters

---
-- Sets the horizontal scale to draw with.
--
-- @number scale the horizontal scale
--
function transform:setScaleX(scale) end

---
-- Sets the vertical scale to draw with.
--
-- @number scale the vertical scale
--
function transform:setScaleY(scale) end

---
-- Sets whether or not the rendered object should be flipped
-- horizontally.
--
-- @bool flip if the rendered object should be horizontally flipped
--
function transform:setFlipX(flip) end

---
-- Sets whether or not the rendered object should be flipped
-- vertically.
--
-- @bool flip if the rendered object should be vertically flipped
--
function transform:setFlipY(flip) end

---
-- Sets the rotation to draw with, in degrees.
--
-- @number deg the rotation
--
function transform:setRotation(deg) end
