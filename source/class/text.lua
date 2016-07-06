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
-- `text` objects are databags holding a series of characters and
-- parameters to form fully-customizable text objects.
--
-- This class also holds the functions of the following classes:
--
-- * `renderable`
--
-- @classmod text
---

local text = {}

---
-- Returns the text bounded between this `text`'s string bound
-- indices.
--
-- See `text:getStringBounds` for a brief description on string
-- boundaries.
--
-- @return this text's currently bounded text
--
function text:getBoundedText() end

---
-- Returns an array containing each component held by this `text`
-- object.
--
-- @return this text's current component array, read-only
--
function text:getComponents() end

---
-- Returns the space taken by this `text` in pixels, when rendered
-- on a normal scale.
--
-- String boundaries are taken into account.
--
-- @return[1] the width of the text
-- @return[1] the height of the text
--
function text:getSpaceTaken() end

---
-- Returns the current string boundaries set upon this `text`.
--
-- String boundaries are a performance-friendly way of isolating
-- certain segments of text within a text object. This serves as an
-- alternative to the `text:substring` function, which generates a
-- new text object on every call, which takes more time and memory
-- than simply modifying the scope of the text, which this function
-- does.
--
-- This function should always be used during tasks like rendering,
-- where the object is frequently queried on what to write next.
--
-- If both bound values are -1, then it is assumed that no string
-- boundaries are set, allowing `text:getBoundedText` to return the
-- full contained text, similar to the function of `text:getText`.
--
-- @return[1] the left bound, or -1 if not set (defaulting to 1)
-- @return[1] the right bound, or -1 if not set (defaulting to the
--   length of the text)
--
function text:getStringBounds() end

---
-- Sets the string boundaries for this `text`.
--
-- See `text:getStringBounds` for a brief description on string
-- boundaries.
--
-- @int first the left bound, or -1 to default
-- @int second the right bound, or -1 to default
--
function text:setStringBounds(first, second) end

---
-- Returns the `textcomponent` located at the provided index in this
-- `text`.
--
-- @int index the character index to query
--
function text:getTextComponentAt(index) end

---
-- Clones this text, including only the text found within the
-- provided string boundaries.
--
-- Do not use this function repetitively in somewhere like a render
-- function, as creating text over and over is not
-- performance-friendly and makes our garbage collector unhappy. The
-- `text:setStringBounds` function is much better for such tasks.
--
-- @int first the left bound
-- @int second the right bound
--
function text:substring(first, second) end
