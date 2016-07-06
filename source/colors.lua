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
-- Colors library for creating instances of `color`s.
--
-- @module colors
---

local colors = {}

---
-- Library constants.
--
-- Contains preset colors.
-- @section

--- <span style="background-color:#ffffff;color:#ffffff">aaaa</span> Color hex `#FFFFFF`.
white = nil
--- <span style="background-color:#000000;color:#000000">aaaa</span> Color hex `#000000`.
black = nil
--- <span style="background-color:#ff0000;color:#ff0000">aaaa</span> Color hex `#FF0000`.
red = nil
--- <span style="background-color:#0000ff;color:#0000ff">aaaa</span> Color hex `#0000FF`.
blue = nil
--- <span style="background-color:#00ff00;color:#00ff00">aaaa</span> Color hex `#00FF00`.
green = nil
--- <span style="background-color:#8b4513;color:#8b4513">aaaa</span> Color hex `#8B4513`.
brown = nil
--- <span style="background-color:#7fff00;color:#7fff00">aaaa</span> Color hex `#7FFF00`.
chartreuse = nil
--- <span style="background-color:#00ffff;color:#00ffff">aaaa</span> Color hex `#00FFFF`.
cyan = nil
--- <span style="background-color:#ff7f50;color:#ff7f50">aaaa</span> Color hex `#FF7F50`.
coral = nil
--- <span style="background-color:#3f3f3f;color:#3f3f3f">aaaa</span> Color hex `#3F3F3F`.
dark_gray = nil
--- <span style="background-color:#b22222;color:#b22222">aaaa</span> Color hex `#B22222`.
firebrick = nil
--- <span style="background-color:#228b22;color:#228b22">aaaa</span> Color hex `#228B22`.
forest = nil
--- <span style="background-color:#ffd700;color:#ffd700">aaaa</span> Color hex `#FFD700`.
gold = nil
--- <span style="background-color:#daa520;color:#daa520">aaaa</span> Color hex `#DAA520`.
goldenrod = nil
--- <span style="background-color:#7f7f7f;color:#7f7f7f">aaaa</span> Color hex `#7F7F7F`.
gray = nil
--- <span style="background-color:#bfbfbf;color:#bfbfbf">aaaa</span> Color hex `#BFBFBF`.
light_gray = nil
--- <span style="background-color:#32cd32;color:#32cd32">aaaa</span> Color hex `#32CD32`.
lime = nil
--- <span style="background-color:#ff00ff;color:#ff00ff">aaaa</span> Color hex `#FF00FF`.
magenta = nil
--- <span style="background-color:#b03060;color:#b03060">aaaa</span> Color hex `#B03060`.
maroon = nil
--- <span style="background-color:#00007f;color:#00007f">aaaa</span> Color hex `#00007F`.
navy = nil
--- <span style="background-color:#6b8e23;color:#6b8e23">aaaa</span> Color hex `#6B8E23`.
olive = nil
--- <span style="background-color:#ffa500;color:#ffa500">aaaa</span> Color hex `#FFA500`.
orange = nil
--- <span style="background-color:#ff69b4;color:#ff69b4">aaaa</span> Color hex `#FF69B4`.
pink = nil
--- <span style="background-color:#a020f0;color:#a020f0">aaaa</span> Color hex `#A020F0`.
purple = nil
--- <span style="background-color:#4169e1;color:#4169e1">aaaa</span> Color hex `#4169E1`.
royal = nil
--- <span style="background-color:#fa8072;color:#fa8072">aaaa</span> Color hex `#FA8072`.
salmon = nil
--- <span style="background-color:#87ceeb;color:#87ceeb">aaaa</span> Color hex `#87CEEB`.
sky = nil
--- <span style="background-color:#708090;color:#708090">aaaa</span> Color hex `#708090`.
slate = nil
--- <span style="background-color:#d2b48c;color:#d2b48c">aaaa</span> Color hex `#D2B48C`.
tan = nil
--- <span style="background-color:#007f7f;color:#007f7f">aaaa</span> Color hex `#007F7F`.
teal = nil
--- <span style="background-color:#ee82ee;color:#ee82ee">aaaa</span> Color hex `#EE82EE`.
violet = nil
--- <span style="background-color:#ffff00;color:#ffff00">aaaa</span> Color hex `#FFFF00`.
yellow = nil

---
-- Library functions.
-- @section
--

---
-- Creates a color matching the provided hex value.
--
-- Hex values are in #RRGGBB format.
--
-- @string hex the hex value to create a color with
--
function colors.fromHex(hex) end

---
-- Creates a color matching the provided RGB values.
--
-- Values are integers and go up to 255.
--
-- @int r the red value of the new color
-- @int g the green value of the new color
-- @int b the blue value of the new color
--
function colors.fromRGB(r, g, b) end
