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
-- `textcomponent`s are the individual segments of text in a `text
-- object.
--
-- Note that textcomponents are immutable.
--
-- @classmod textcomponent
-- @alias textcomp
---

local textcomp = {}

---
-- Returns the text contained by this `textcomponent`.
--
-- @return the text of this textcomponent
--
function textcomp:getText() end

---
-- Returns the `font` used to render this `textcomponent`.
--
-- @return the font of this textcomponent
--
function textcomp:getFont() end

---
-- Returns the `color` used to render this `textcomponent`.
--
-- @return the color of this textcomponent
--
function textcomp:getColor() end

---
-- Returns the `sound` played for each segment of this
-- `textcomponent` whenever it gets rendered.
--
-- @return the sound played for each segment of this textcomponent
--
function textcomp:getSound() end

---
-- Returns an array of text styles used when rendering this
-- `textcomponent`.
--
-- @return an array of text styles
--
function textcomp:getStyles() end

---
-- Returns the speed at which the text in this segment is played, in
-- characters per second.
--
-- @return the speed of this textcomponent's text
-- 
function textcomp:getTextSpeed() end

---
-- Returns how many characters are in each segment.
--
-- @return the count of characters in a segment when rendering this
--   textcomponent
--
function textcomp:getSegmentSize() end

---
-- Returns the amount of time to wait before rendering the text in
-- this `textcomponent`, in seconds.
--
-- @return the delay used before this textcomponent is rendered, in
--   seconds
--
function textcomp:getDelay() end
