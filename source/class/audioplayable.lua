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
-- `audioplayable` objects give control over the playback of their
-- contained audio track.
--
-- @classmod audioplayable
---

local audioplayable = {}

---
-- Plays this `audioplayable` and loops it, with the given
-- parameters.
--
-- @number[opt=1.0] volume the volume to play at
-- @number[opt=1.0] pitch the pitch to play at
-- @number[opt=0.0] pan the pan to play with
--
-- @return[1] `sounddata` - if this is a `sound`
-- @return[2] `nil` - in all other cases
--
function audioplayable:loop(volume, pitch, pan) end

---
-- Plays this `audioplayable` with the given parameters.
--
-- @number[opt=1.0] volume the volume to play at
-- @number[opt=1.0] pitch the pitch to play at
-- @number[opt=0.0] pan the pan to play with
--
-- @return[1] `sounddata` - if this is a `sound`
-- @return[2] `nil` - in all other cases
--
function audioplayable:play(volume, pitch, pan) end

---
-- Stops the audio associated with this `audioplayable` from playing.
-- 
function audioplayable:stop() end
