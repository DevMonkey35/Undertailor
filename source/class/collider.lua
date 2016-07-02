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
-- `collider` objects are affected by physics and can interact with
-- each other in the Overworld subsystem.
--
-- @classmod collider
---

local collider = {}

---
-- Returns the body type of this `collider`.
--
-- This function can return 3 integers.
--
-- * 1 - The body is a static body.
-- * 2 - The body is a kinematic body.
-- * 3 - The body is a dynamic body.
--
-- @return the body type id of this collider
--
function collider:getColliderType() end

---
-- Sets the body type of this `collider`.
--
-- See `collider:getColliderType` for the possible type IDs.
--
-- @see collider:getColliderType
--
-- @int type the ID of the body type to set
--
function collider:setColliderType(type) end

---
-- Returns the velocity of this `collider`.
--
-- @return[1] the velocity of this collider on the x axis
-- @return[1] the velocity of this collider on the y axis
--
function collider:getVelocity() end

---
-- Sets the velocity of this `collider`.
--
-- @number velX the new velocity of this collider on the x axis
-- @number velY the new velocity of this collider on the y axis
--
function collider:setVelocity(velX, velY) end

---
-- Applies a force to this `collider`.
--
-- Applying a force at a point other than the object's point origin
-- (at local 0, 0) will generate torque, if the object's rotation is
-- not fixed (see `collider:isRotationFixed`).
--
-- @number forceX the strength of the force to apply on the x axis
-- @number forceY the strength of the force to apply on the y axis
-- @number[opt=0.0] localX the location relative to this collider's
--   origin to apply the force to on the x axis
-- @number[opt=0.0] localY the location relative to this collider's
--   origin to apply the force to on the y axis
--
function collider:applyForce(forceX, forceY, localX, localY) end

---
-- Applies an impulse to this `collider`.
--
-- Applying an impulse at a point other than the object's point
-- origin (at local 0, 0) will generate torque, if the object's
-- rotation is not fixed (see `collider:isRotationFixed`).
--
-- @number impX the strength of the impulse to apply on the x axis
-- @number impY the strength of the impulse to apply on the y axis
-- @number[opt=0.0] localX the location relative to the collider's
--   origin to apply the impulse to on the x axis
-- @number[opt=0.0] localY the location relative to the collider's
--   origin to apply the impulse to on the y axis
--
function collider:applyImpulse(impX, impY, localX, localY) end

---
-- Applies torque to this `collider`.
--
-- @number torque the strength of torque to apply
--
function collider:applyTorque(torque) end

---
-- Returns whether or not this `collider` can collide with
-- other colliders.
--
-- This only affects physical collision, where colliding objects stop
-- each other from passing in their current direction. The collider
-- will still be notified if an object collides with it, even if said
-- object passes through it due to not having physically collided.
--
-- @return whether or not the provided collider can collide
-- 
function collider:canCollide() end

---
-- Sets whether or not this `collider` can collide with other
-- colliders.
--
-- See `collider:canCollide` for the effects of this property.
--
-- @bool canCollide whether or not the provided collider should
--   collide with other objects
--
function collider:setCanCollide(canCollide) end

---
-- Returns whether or not this `collider` is affected by
-- rotational velocity, or torque.
--
-- @return whether or not the collider is affected by torque
--
function collider:isRotationFixed() end

---
-- Sets whether or not this `collider` is affected by
-- rotational velocity, or torque.
--
-- @bool rotationFixed whether or not the collider is affected by
--   torque
--
function collider:setRotationFixed(rotationFixed) end

---
-- Returns the group ID of this `collider`.
--
-- For objects that can collide, group IDs determine which objects
-- a single object may collide with. Like with the `canCollide` flag,
-- objects will still be notified if another passes through them
-- despite having collision disabled.
--
-- Group ID rules are as follows:
--
-- * Objects with the same group ID will not collide with each other.
-- * Objects with a negative group ID collide with everything.
--
-- The default group ID for any object is -1.
-- 
-- @return the group ID of the provided collider
--
function collider:getGroupId() end

---
-- Sets the group ID of this `collider`.
--
-- See `collider:getGroupId` for a brief explanation on group IDs.
--
-- @int id the new group ID of the collider
--
function collider:setGroupId(id) end
