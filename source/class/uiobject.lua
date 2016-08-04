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
-- `uiobject` are the main container objects for `uicomponents`, used
-- in the UI layer.
--
-- This class also holds the functions of the following classes:
--
-- * `eventlistener`
-- * `identifiable`
-- * `positionable`
-- * `destructible`
-- * `processable`
-- * `renderable`
--
-- @classmod uiobject
-- @alias uio
---

local uio = {}

---
-- Returns whether or not this `uiobject` is active.
--
-- @return if this uiobject is active
--
function uio:isActive() end

---
-- Returns the maximum lifetime this `uiobject` will live upon
-- registration, in seconds. A value of 0 or less indicates an
-- infinite lifetime.
--
-- @return the maximum lifetime of this uiobject in seconds
--
function uio:getMaxLifetime() end

---
-- Sets the maximum lifetime this `uiobject` will live upon
-- registration, in seconds.
--
-- @number seconds the new max lifetime for this uiobject, in seconds
--
function uio:setMaxLifetime(seconds) end

---
-- Returns whether or not this `uiobject` is past is lifetime. This
-- usually is only true for an object that had already been removed
-- from a `uicontroller`.
--
-- @return if this uiobject is past its max lifetime, and is
--   scheduled for removal
--
function uio:isPastLifetime() end

---
-- Returns the current lifetime of this `uiobject`, in seconds. This
-- initially represents time since registration to a `uicontroller`,
-- however the `uio:resetLifetime` function can be called to reset
-- lifetime back to 0.
--
-- @return the current lifetime of this `uiobject` in seconds
--
function uio:getLifetime() end

---
-- Resets the current lifetime of this `uiobject` to 0.
--
function uio:resetLifetime() end

---
-- Registers the provided `uicomponent` into this `uiobject`.
--
-- @tparam uicomponent component the uicomponent to register
--
function uio:registerComponent(component) end

---
-- Removes this `uiobject` from processing.
--
-- This function differs from calling `destructible:destroy` in that
-- it allows the uiobject to be reusable.
--
function uio:remove() end
