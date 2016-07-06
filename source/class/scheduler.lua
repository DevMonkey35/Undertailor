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
-- `scheduler`s are one of the three main subsystems running under an
-- `environment`. Used to schedule tasks to occur every frame in the
-- current environment, and is the first to be processed, before
-- the `uicontroller`.
--
-- This class also holds the functions of the following classes:
--
-- * `subsystem`
-- * `processable`
-- * `destructible`
--
-- @classmod scheduler
---

local scheduler = {}

---
-- Registers the provided task implementation into this `scheduler`.
--
-- @tab task the task implementation to register
-- @bool[opt=false] active whether or not the task will be active
--
-- @return the ID of the registered
--
function scheduler:registerTask(task, active) end

---
-- Cancels the task with the provided ID.
--
-- @int id the ID of the task to cancel
--
function scheduler:cancelTask(id) end

---
-- Returns whether or not the task of the provided ID is currently
-- being ran by this `scheduler`.
--
-- @int id the ID of the task to query
--
-- @return whether or not the task is still being processed
--
function scheduler:hasTask(id) end
