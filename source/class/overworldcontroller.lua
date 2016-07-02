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
-- `overworldcontroller`, aka, the Overworld, is a subsystem of an
-- `environment` processing routine tasks that occur in an overworld.
--
-- @classmod overworldcontroller
-- @alias oc
---

local oc = {}

---
-- Returns whether or not the provided `worldobject` is this
-- `overworldcontroller`'s character object.
--
-- It is recommended to use this instead of comparing the target
-- object with `oc:getCharacter`, due to the way Lua integration
-- works. This function simply compares the IDs of the two objects,
-- as the IDs of worldobjects are generated per instance.
--
-- @tparam worldobject object the object to query
--
-- @return if the provided worldobject is this overworldcontroller's
--   character object
--
function oc:isCharacter(object) end

---
-- Returns the character `worldobject` set for this
-- `overworldcontroller`.
--
-- @return the character object for this overworldcontroller, or nil
--   if not set or destroyed
--
function oc:getCharacter() end

---
-- Sets the character `worldobject` for this `overworldcontroller`.
--
-- @tparam worldobject object the object to set
--
function oc:setCharacter(object) end

---
-- Returns the current `worldroom` set for this
-- `overworldcontroller`.
--
-- @return this overworldcontroller's worldroom, or nil if not set
--
function oc:getRoom() end

---
-- Sets the current `worldroom` for this `overworldcontroller`.
--
-- @tparam worldroom room the room to set
-- @string[opt=nil] targetEntrypoint the name of the entrypoint to
--   spawn the character object at in the room to be set
-- @bool[opt=isPlayingTransitions] transitions whether or not to play
--   transitions when to the target room
--
function oc:setRoom(room, targetEntrypoint, transitions) end

---
-- Returns whether or not this `overworldcontroller` will play
-- transitions by default whenever its `worldroom` is changed.
--
-- @return if this overworldcontroller plays room transitions by
--   default
--
function oc:isPlayingTransitions() end

---
-- Sets whether or not this `overworldcontroller` will play
-- transitions by default whenever its `worldroom` is changed.
--
-- @bool playingTransitions if this overworldcontroller should play
--   transitions by default
--
function oc:setPlayingTransitions(playingTransitions) end

---
-- Sets the entry transition for whenever this `overworldcontroller`
-- changes `worldroom`s.
--
-- @tab task a table implementing a task for entry transitions
--
function oc:setEntryTransition(task) end

---
-- Sets the exit transition for whenever this `overworldcontroller`
-- changes `worldroom`s.
--
-- @tab task a table implementing a task for exit transitions
--
function oc:setExitTransition(task) end

---
-- Camera control functions.
-- @section
--

---
-- Returns the current position of this `overworldcontroller`'s
-- camera.
--
-- The camera's origin point is in its center.
--
-- @return[1] the current x position of the camera
-- @return[1] the current y position of the camera
--
function oc:getCameraPosition() end

---
-- Sets the current position of this `overworldcontroller`'s camera.
--
-- @number x the new x position of the camera
-- @number y the new y position of the camera
--
function oc:setCameraPosition(x, y) end

---
-- Returns the current positional offset applied to this
-- `overworldcontroller`'s camera.
--
-- @return[1] the current x offset of the camera
-- @return[1] the current y offset of the camera
--
function oc:getCameraOffset() end

---
-- Sets the current positional offset applied to this
-- `overworldcontroller`'s camera.
--
-- @number x the new x offset of the camera
-- @number y the new y offset of the camera
--
function oc:setCameraOffset(x, y) end

---
-- Returns the current zoom level of this `overworldcontroller`'s
-- camera.
--
-- The zoom level is equivalent to the area a single pixel takes on
-- the screen, with the default value of 1 resolving to a 1:1 output
-- on a 640x480 window.
--
-- A negative zoom level follows the same rules, but the screen will
-- be inverted.
--
-- @return the zoom level of the camera
--
function oc:getCameraZoom() end

---
-- Sets the current zoom level of this `overworldcontroller`'s
-- camera.
--
-- @number zoom the new zoom level to set
--
function oc:setCameraZoom(zoom) end

---
-- Returns whether or not this `overworldcontroller`'s camera is
-- constantly fixing its position.
--
-- Camera fixing ensures that the camera will never show areas of the
-- overworld that is considered out of bounds. An area is considered
-- out of bounds when size of the tilemap for the current room does
-- not cover that area. By default, this value is true.
--
function oc:isCameraFixing() end

---
-- Sets whether or not this `overworldcontroller`'s camera is
-- constantly fixing its position.
--
function oc:setCameraFixing(fixing) end
