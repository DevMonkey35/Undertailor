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
-- `inputdata` objects are used to store information about the
-- current state of keyboard input.
--
-- There is usually only one instance ever stored, and is constantly
-- updated every frame.
--
-- @classmod inputdata
---

local inputdata = {}

---
-- Returns the `pressdata` for the provided key ID.
--
-- @int key the key ID to query
--
-- @return the pressdata for the provided key 
--
function inputdata:getPressData(key) end

---
-- Returns whether or not this `inputdata` has been consumed by
-- another processing object for the current frame.
--
-- Implementations should be aware of this flag; if this flag returns
-- true, one can assume another object has processed input and wishes
-- to take primary control. Players should naturally be controlling
-- only one object at a time, so this flag should be respected by
-- anything the player may be given control over.
--
-- @return whether or not this inputdata has been consumed
--
function inputdata:isConsumed() end

---
-- Consumes this `inputdata` for the current frame.
--
-- See `inputdata:isConsumed` for details on input consumption.
--
function inputdata:consume() end
