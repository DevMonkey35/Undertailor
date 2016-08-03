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
-- An expansion of the base `os` library of Lua.
--
-- This library includes the functions that come with Lua's normal
-- `os` lib, and the functions listed below. Exceptions are the
-- following blacklisted functions:
--
-- * execute
-- * exit
-- * remove
-- * rename
-- * setlocale
-- * tmpname
--
-- @module os
---

local os = {}

---
-- Returns how much time has passed since the midnight of January 1,
-- 1970, in milliseconds.
--
-- @return basically os.time(), except in milliseconds
--
function os.timeMillis() end

---
-- Returns how much time has passed since the provided time value, in
-- milliseconds.
--
-- @int millis a point in time, usually returned by `os.timeMillis()`
--   in milliseconds
--
-- @return how much time has passed since `time`
--
function os.sinceMillis(millis)
