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
-- `uicomponent`s are the main processing objects inside a
-- `uicontroller` subsystems.
--
-- This class also holds the functions of the following classes:
--
-- * `eventlistener`
-- * `positionable`
-- * `renderable`
-- * `processable`
--
-- @classmod uicomponent
-- @alias uicomp
---

local uicomp = {}

---
-- Returns the `uiobject` owning and processing this `uicomponent`.
--
-- @return the parent uiobject
--
function uicomp:getParent() end

---
-- Returns the screen position at which this `uicomponent` will be
-- drawn.
--
-- The screen position is a combination of the uicomponent's current
-- position and the parent `uiobject`'s position.
--
-- @return the screen position at which the origin of this
--   uicomponent position is drawn at
--
function uicomp:getScreenPosition() end
