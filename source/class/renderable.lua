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
-- `renderable` objects have a function specifically for drawing onto
-- the screen.
--
-- @classmod renderable
---

local renderable = {}

---
-- Returns the `transform` of this `renderable`.
--
-- @return the transform of this renderable
--
function renderable:getTransform() end

---
-- Sets the `transform` of this `renderable`.
--
-- If a renderable possesses a transform, it should never allow its
-- transform to become nil. Implementations should query the default
-- transform to duplicate its values into this renderable's current
-- transform using `game.graphics.getDefaultTransform` should the
-- passed transform be nil.
--
-- @tparam transform transform the new transform to set
--
function renderable:setTransform(transform) end

---
-- Draws this `renderable` onto the screen.
--
-- @number[opt=0.0] x the x coordinate to draw at
-- @number[opt=0.0] y the y coordinate to draw at
-- @tparam[opt=renderable:getTransform] transform transform the
--   transform to draw with
--
function renderable:render(x, y, transform) end
