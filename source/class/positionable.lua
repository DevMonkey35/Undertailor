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
-- `positionable` objects hold their position within their
-- containers.
--
-- @classmod positionable
---

local positionable = {}

---
-- Returns the position of this `positionable`.
--
-- @return[1] the x coordinate of this positionable
-- @return[1] the y coordinate of this positionable
--
function positionable:getPosition() end

---
-- Sets the position of this `positionable`.
--
-- @param x the new x coordinate of this positionable
-- @param y the new y coordinate of this positionable
--
function positionable:setPosition(x, y) end

---
-- Returns the height of this `positionable`.
--
-- Height determines the Y offset for a positionable object in the
-- Overworld, as objects in the Overworld are usually rendered in an
-- order based on their current Y position. To allow objects to have
-- an illusion of altitude without needing to go to an unnecessarily
-- high Y value in order to avoid an object jumping in front of it,
-- height allows the visual Y position of an object to be offset
-- without disturbing the render order.
--
-- Height is only used by world objects. This function returns 0 on
-- any other type.
--
-- @return the height of this positionable
--
function positionable:getHeight() end

---
-- Sets the height of this `positionable`.
--
-- See `positionable:getHeight` for an explanation on height.
--
-- @number height the new height of this positionable
--
function positionable:setHeight(height) end
