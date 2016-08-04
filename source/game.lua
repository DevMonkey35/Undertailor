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
-- `game` library for interfacing with core functionality.
--
-- @module game
---

---
-- Sublibraries.
-- @section

---
-- `game.graphics` library used for controlling how objects are
-- rendered and for shape rendering.
--
graphics = nil

---
-- `game.audio` library used for retrieving and controlling instances
-- of `audio`.
--
audio = nil

---
-- `game.control` library used to query user input.
--
control = nil

local game = {}

---
-- Environment functions.
-- @section
--

---
-- Returns the `environment` under the given name, or creates a new
-- one if it doesn't exist.
--
-- @string name the name of the environment
--
-- @return the environment under the given name, never nil
--
function game.getEnvironment(name) end

---
-- Destroys the given environment.
--
-- Alternatively, one can use `environment:destroy`.
--
-- @tparam environment env the environment to destroy
--
function game.destroyEnvironment(env) end

---
-- Destroys the environment of the given name, if it exists.
--
-- @string name the name of the environment to destroy
--
function game.destroyEnvironment(name) end

---
-- Returns the current active environment, if available.
--
-- @return the current active environment, or nil if not set
--
function game.getActiveEnvironment() end

---
-- Sets the active environment.
--
-- @tparam environment env the environment to set as active, or nil
--   to disable
--
function game.setActiveEnvironment(env) end

---
-- Sets the active environment.
--
-- @string name the name of the environment to set as active
--
function game.setActiveEnvironment(name) end

---
-- Object creation functions.
--
-- The three primary implementable types, `worldobject`,
-- `uicomponent`, and `renderable` can be called without providing
-- a script path for external implementation.
-- @section
--

---
-- Creates a new `worldobject`, implemented by the script at the
-- given path.
--
-- @string[opt=nil] scriptPath the path to the implementing script
-- @tparam[opt=nil] vargs ... creation parameters, passed to the
--   implementing script's `create` function
--
-- @return a worldobject
--
function game.newWorldObject(scriptPath, ...) end

---
-- Creates a new `worldroom`, implemented by the script at the
-- given path.
--
-- @string scriptPath the path to the implementing script
-- @tparam[opt=nil] vargs ... creation parameters, passed to the
--   implementing script's `create` function
--
-- @return a worldroom
--
function game.newWorldRoom(scriptPath, ...) end

---
-- Creates a new `uicomponent`, implemented by the script at the
-- given path.
--
-- @string[opt=nil] scriptPath the path to the implementing script
-- @tparam[opt=nil] vargs ... creation parameters, passed to the
--   implementing script's `create` function
--
-- @return a uicomponent
--
function game.newUIComponent(scriptPath, ...) end

---
-- Creates a new `uiobject`.
--
-- @boolean[opt=false] if the uiobject is active
--
-- @return a uiobject
--
function game.newUIObject(active) end

---
-- Creates a new `renderable`, implemented by the script at the
-- given path.
--
-- @string[opt=nil] scriptPath the path to the implementing script
-- @tparam[opt=nil] vargs ... creation parameters, passed to the
--   implementing script's `create` function
--
-- @return a renderable
--
function game.newRenderable(scriptPath, ...) end

---
-- Misc functions.
-- @section
--

---
-- Returns the global `scheduler`.
--
-- @return the global scheduler
--
function game.getGlobalScheduler() end

---
-- Sets the title of the game window.
--
-- Providing nil sets the title to the default.
--
-- @string title the new title of the game window, or nil to default
--
function game.setTitle(title) end
