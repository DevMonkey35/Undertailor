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
-- `audio` library for interfacing with audio.
--
-- @module game.audio
-- @alias audio
---

local audio = {}

---
-- Master volume control.
--
-- All values are between `0.0` and `1.0`.
-- @section
--

---
-- Returns the current master volume.
--
-- @return the current master volume
--
function audio.getMasterVolume() end

---
-- Sets the current master volume.
--
-- @number vol the new master volume
--
function audio.setMasterVolume(vol) end

---
-- Returns the current music volume.
--
-- @return the current music volume
--
function audio.getMusicVolume() end

---
-- Sets the current music volume.
--
-- @number vol the new music volume
--
function audio.setMusicVolume(vol) end

---
-- Returns the current sound volume.
--
-- @return the current sound volume
--
function audio.getSoundVolume() end

---
-- Sets the current sound volume.
--
-- @number vol the new sound volume
--
function audio.setSoundVolume(vol) end

---
-- Audio retrieval.
-- @section
--

---
-- Returns the `music` under the given name.
--
-- @string name the name of the music to load
--
-- @return the music of the given name, or nil if unknown
--
function audio.getMusic(name) end

---
-- Returns the `sound` under the given name.
--
-- @string name the name of the sound to load
--
-- @return the sound of the given name, or nil if unknown
--
function audio.getSound(name) end

---
-- Misc functions.
-- @section
--

---
-- Kills any running `audio` instance from playing.
--
function audio.stopAllAudio() end
