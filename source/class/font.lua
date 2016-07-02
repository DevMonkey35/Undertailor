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
-- `font` objects store a set of sprites used to display language
-- in-game.
--
-- @classmod font
---

local font = {}

---
-- Returns the distance between two lines of text rendered with this
-- `font`, in pixels.
--
-- @return the size of lines with this font, in pixels
--
function font:getLineSize() end

--
-- Returns the length of spaces rendered with this `font`, in pixels.
--
-- @return the size of spaces with this font, in pixels
--
function font:getSpaceLength() end

---
-- Returns the letter spacing for the provided character, when
-- rendered with this `font`.
--
-- Characters past the very first one in the string provided will be
-- ignored.
--
-- @string character the character to query with
--
-- @return[1] the amount of empty pixels guaranteed to be behind the
--   provided character
-- @return[1] the amount of empty pixels guaranteed to be in front of
--   the provided character
--
function font:getLetterSpacing(character) end

---
-- Returns a string containing all characters supported by this
-- `font`.
--
-- @return a string of supported characters
--
function font:getCharacterList() end

---
-- Returns the `sprite` for the provided character when using this
-- `font`.
--
-- @return the provided character's sprite, or nil if none
--
function font:getCharacterSprite(character)
