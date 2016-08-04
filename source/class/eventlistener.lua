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
-- `eventlistener`s are objects that may receive certain types of
-- data in the occurence of a scenario to generate a reaction.
--
-- @classmod eventlistener
---

local eventlistener = {}

---
-- Pokes this `eventlistener` with an occurrence of the given event,
-- and returns true if any response was generated.
--
-- In their initial form, events are tables. When passing a new event
-- to this function, it should be presented as an array table holding
-- the ID of the event, followed by any of its parameters.
--
-- @usage
--   local listener -- our listener
--   local event = {"myEvent", "param1"}
--
--   -- any handlers listening for "myEvent" will be triggered
--   listener:callEvent(event)
--
-- @tparam table event the event
--
-- @return if any handler responded to the event and processed it
--
function eventlistener:callEvent(event) end

---
-- Registers a new handler for the specified event type ID.
--
-- Handlers are functions, receiving whether or not the event being
-- provided had previously been processed by another handler (a
-- handler before them returned true), followed by the parameters
-- associated with the event.
--
-- The following example continues off of the one seen in
-- `eventlistener:callEvent`.
--
-- @usage
--   local listener -- our listener
--
--   listener:onEvent("myEvent", function(processed, p1)
--     print(p1) -- will print param1
--   end)
--
-- @string eventId the event type ID to add a new handler for
-- @function handler the handler of the event
--
function eventlistener:onEvent(eventId, handler) end
