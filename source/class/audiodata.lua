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
-- `audiodata` objects hold audio clips with parameters that may be
-- controlled, such as volume, pitch, and pan.
-- 
-- @classmod audiodata
---

local audiodata = {}

---
-- Returns the volume set for this `audiodata`.
-- 
-- Volumes are between 0.0 and 1.0.
--
-- @return the volume of this `audiodata`
--
function audiodata:getVolume() end

---
-- Sets a volume of this `audiodata`.
--
-- The provided value is automatically bounded between 0.0 and 1.0.
-- 
-- @number volume the new volume
--
function audiodata:setVolume(volume) end

---
-- Returns the pitch value set for this `audiodata`.
--
-- Pitch values are between 0.5 and 2.0. Note that pitch also affects
-- speed.
--
-- @return the pitch of this `audiodata`
--
function audiodata:getPitch() end

---
-- Sets the pitch value of this `audiodata`.
--
-- The provided value is automatically bounded between 0.5 and 2.0.
-- Use 1.0 for default pitch/speed.
-- 
-- @number pitch the new pitch
--
function audiodata:setPitch(pitch) end

---
-- Returns the panning value set for this `audiodata`.
--
-- Panning values are between -1.0 and 1.0.
--
-- @return the pan of this `audiodata`
--
function audiodata:getPan() end

---
-- Sets the panning value of this `audiodata`.
--
-- The provided value is automatically bounded between -1.0 and 1.0.
-- Use 0.0 to center audio. Negative values allocate sound to the
-- left speaker, positives to the right.
--
-- @number pan the new pan
--
function audiodata:setPan(pan) end

---
-- Returns whether or not the audio of this `audiodata` is
-- looping.
--
-- @return if this `audiodata` is looping
--
function audiodata:isLooping() end

---
-- Sets whether or not the audio of this `audiodata` is
-- looping.
-- 
-- @param looping whether this `audiodata` should loop
--
function audiodata:setLooping(looping) end

---
-- Returns whether or not the audio of this `audiodata` is
-- currently playing.
--
-- @return if this `audiodata` is playing
--
function audiodata:isPlaying() end

---
-- Stops the audio associated with this `audiodata` from
-- playing.
--
function audiodata:stop() end
