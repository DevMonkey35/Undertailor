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
-- `music` objects hold long audio tracks.
--
-- This class also contains the functions of the following classes:
--
-- * `audio`
-- * `audiodata`
-- * `audioplayable`
--
-- @classmod music
---

local music = {}

---
-- Returns the point in playback at which this `music` will reset to
-- upon completion.
--
-- 0 will loop from the beginning, a negative number signifies no
-- looping.
--
-- This value is also affected by `audiodata:setLooping`, which sets
-- the looping point to 0 or -1, depending on if the data was set to
-- loop.
--
-- @return the loop point of this music
--
function music:getLoopPoint() end

---
-- Sets the point in playback at which this `music` will reset to
-- upon completion.
--
-- @number loopPoint the looping point to set
--
function music:setLoopPoint(loopPoint) end
