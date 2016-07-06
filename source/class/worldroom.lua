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
-- `worldroom`s are container objects for `worldobject`s.
--
-- This class also holds the functions of the following classes:
--
-- * `destructible`
-- * `eventlistener`
-- * `renderable`
-- * `processable`
--
-- @classmod worldroom
-- @alias wroom
---

local wroom = {}

---
-- Returns the `overworldcontroller` currently set with this
-- `worldroom`.
--
-- @return the parent overworldcontroller
--
function wroom:getOverworld() end

---
-- Registers the provided `worldobject` into this `worldroom`.
--
-- @tparam worldobject object the worldobject to register
--
function wroom:registerObject(object) end

---
-- Removes the provided `worldobject` from this `worldroom`, if the
-- former resides inside within worldroom.
--
-- @tparam worldobject object the worldobject to remove
--
function wroom:removeObject(object) end

---
-- Removes the `worldobject` owning the provided `ID` from this
-- `worldroom`, if the former resides within this worldroom.
--
-- @int id the ID of the worldobject to remove
--
function wroom:removeObject(id) end

---
-- Map preparation functions.
--
-- These functions should only be called inside a `worldroom`'s
-- create function.
-- @section
--

---
-- Registers a room entrypoint using the provided parameters.
--
function wroom:registerEntrypoint(name, defpoint[, defshape, target room file, target entrypoint]) end

---
-- Registers a room entrypoint using the provided parameters.
--
function wroom:registerEntrypoint(name, spawnX, spawnY[, defshape, target room file, target entrypoint]) end

---
-- Sets the current tilemap of this `worldroom`.
--
function wroom:setTilemap(tilemapName) end

---
-- Map functions.
-- @section
--

---
-- Returns the current state of the collision layer matching the
-- given name.
--
-- State of collision indicates whether or not the collision shapes
-- set in the layer are active, that is, actively blocking any
-- objects from passing through them.
--
-- @string layerName the name of the collison layer to query
--
-- @return if the collision layer matching the given name is active,
--   or false if it doesn't exist
--
function wroom:getCollisionLayerState(layerName) end

---
-- Sets the current state of the collision layer matching the given
-- name.
--
-- See `wroom:getCollisionLayerState` for a brief description on
-- collision layer states.
--
-- @string layerName the name of the target collision layer
-- @bool state the new state of collision for the layer
--
function wroom:setCollisionLayerState(layerName, state)

---
-- Returns the current opacity set for the given layer index.
--
-- Layer opacity is **per layer index, not per defined layer.** This
-- means you cannot specify the opacity of a specific tile or image
-- layer, but you can instead set the opacity of any renderable
-- objects displayed on a specific layer.
--
-- @int layer the layer index to query
--
-- @return the opacity of the provided layer index
--
function wroom:getLayerOpacity(layer) end

---
-- Sets the current opacity of the given layer index.
--
-- See `wroom:getLayerOpacity` for a brief description on layer
-- opacity.
--
-- @int layer the layer index to query
-- @number opacity the opacity to set
--
function wroom:setLayerOpacity(layer, opacity) end

---
-- Returns the shape data for the provided shape in this
-- `worldroom`'s current tilemap. The shape name must be in
-- "layerName:shapeName" form.
--
-- @string defShapeName the name of the defined shape, in
--   `layerName:shapeName` form
--
-- @return[1] the name of the shape type, can be "rectangle",
--   "circle", "polygon", or "polyline"
-- @return[1] the x position of the shape's origin point
-- @return[1] the y position of the shape's origin point
-- @return[1] if a perfect circle, the radius of the circle
--
-- @return[2] the name of the shape type, can be "rectangle",
--   "circle", "polygon", or "polyline"
-- @return[2] the x position of the shape's origin point
-- @return[2] the y position of the shape's origin point
-- @return[2] if not a perfect circle, a table of the shape's
--   vertices
--
-- @return[3] nil, denoting the shape doesn't exist
--
function wroom:getMapDefinedShape(defShapeName) end

---
-- Returns the coordinates for the provided point in this
-- `worldroom`'s current tilemap. The point name must be in
-- "layerName:pointName" form.
--
-- @string defPointName the name of the defined point, in
--   `layerName:pointName` form
--
-- @return[1] the x coordinate of the point
-- @return[1] the y coordinate of the point
--
-- @return[2] nil, denoting the point doesn't exist
--
function wroom:getMapDefinedPoint(defPointName) end
