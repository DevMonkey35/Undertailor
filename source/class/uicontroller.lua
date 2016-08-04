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
-- `uicontroller`s are one of the three main subsystems running under
-- an `environment`. Holds control over the UI layer of an
-- environment, is the first to be rendered, and is the second to be
-- processed, before the `overworldcontroller` and after the
-- `scheduler`.
--
-- This class also holds the functions of the following classes:
--
-- * `subsystem`
-- * `processable`
-- * `destructible`
-- * `renderable`
--
-- @classmod uicontroller
-- @alias uicon
---

local uicon = {}

---
-- Returns the `uiobject` of the provided ID, if registered with this
-- `uicontroller`.
--
-- @int id the ID of the target uiobject
--
-- @return the uiobject owning the provided ID
--
function uicon:getUIObject(id) end

---
-- Registers the provided `uiobject` into this `uicontroller`.
--
-- @tparam uiobject object the uiobject to register
--
function uicon:registerUIObject(object) end

---
-- Removes the `uiobject` owning the specified ID, if registered with
-- this `uicontroller`.
--
-- @int id the id of the target uiobject
--
function uicon:removeUIObject(id) end

---
-- Removes the provided `uiobject` from this `uicontroller`, if
-- registered.
--
-- Removal of objects differ from destruction in that the uiobject
-- is reusable after removal, where as destruction will completely
-- destroy the uiobject rendering it no longer usable.
--
-- @tparam uiobject obj the object to remove
--
function uicon:removeUIObject(obj) end
