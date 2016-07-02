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
-- `pressdata` objects are databags for the individual state of
-- input for specific keyboard keys, typically managed by an
-- `inputdata` instance.
--
-- @classmod pressdata
---

local pressdata = {}

---
-- Returns the amount of time that had passed since this key was last
-- released, in seconds.
--
-- @return how long its been since this key was released, in seconds
--
function pressdata:getLastReleaseTime() end

---
-- Returns whether or not this key had been released within the last
-- given seconds of time.
--
-- If 0 is passed for time, this function returns whether or not the
-- key had been released in the current frame.
--
-- @return whether this key had been released within the last given
--   seconds, or within the current frame
--
function pressdata:justReleased(time) end

---
-- Returns the amount of time that had passed since this key was last
-- pressed, in seconds.
--
-- @return how long its been since this key was pressed, in seconds
--
function pressdata:getLastPressTime() end

---
-- Returns whether or not this key had been pressed within the last
-- given seconds of time.
--
-- If 0 is passed for time, this function returns whether or not the
-- key had been pressed in the current frame.
--
-- @return whether this key had been pressed within the last given
--   seconds, or within the current frame
--
function pressdata:justPressed(time) end

---
-- Returns how long this key has been held for if it is currently
-- pressed. If not currently pressed, returns how long it was held
-- for the last time it was pressed, or 0 if it was never pressed
-- prior to calling this function.
--
-- @return how long this key has, or had been pressed for, or 0 if
--   never pressed
--
function pressdata:getHoldTime() end

---
-- Returns whether or not this key is currently pressed.
--
-- @return if this key is currently pressed
--
function pressdata:isPressed() end
