-- object methods

-- object:getZ() -> int
-- object:setZ(int) "z value"
-- object:getPosition() -> float, float
-- object:setPosition(float, float) "x", "y"
-- object:getAnimation() -> String, String
-- object:setAnimation(String, String) "animationSet", "animationName"
-- object:getBoundingBox() -> LuaRectangle
-- object:getBoundingBoxSize() -> float, float
-- object:setBoundingBoxSize(float, float) "width, height"
-- object:getBoundingBoxOrigin() -> float, float
-- object:setBoundingBoxOrigin(float, float) "x, y"
-- object:getScale() -> float
-- object:setScale(float)
-- object:canCollide() -> boolean
-- object:setCanCollide(boolean)

-- object code

-- Character Frisk

-- up 19
-- down 20
-- left 21
-- right 22

local C_ANIM_SET = "frisk"
local C_IDLE_ANIMS = {"idle_up", "idle_down", "idle_left", "idle_right"}
local C_MOVE_ANIMS = {"walk_up", "walk_down", "walk_left", "walk_right"}
local _object;
local _direction = 2;
local _movementSpeed = 80.0
function create(object)
	_object = object
	
	_object:setBoundingBoxSize(12.5, 12.5)
	_object:setBoundingBoxOrigin(6.25, 6)
	_object:setAnimation(C_ANIM_SET, C_IDLE_ANIMS[_direction])
end

function process(delta, input) -- triggered whenever the object actively exists within the current room
	local xVel = 0;
	local yVel = 0;
	local zoomAdd = 0;
	if not (input:isConsumed()) then
		if(input:getPressData(19):isPressed()) then -- up
			_direction = 1
			yVel = _movementSpeed
		end
		
		if(input:getPressData(20):isPressed()) then -- down
			_direction = 2
			yVel = _movementSpeed * -1
		end
		
		if(input:getPressData(21):isPressed()) then -- left
			_direction = 3
			xVel = _movementSpeed * -1
		end
		
		if(input:getPressData(22):isPressed()) then -- right
			_direction = 4
			xVel = _movementSpeed
		end
		
		if(input:getPressData(92):isPressed()) then -- pageup
			zoomAdd = 2
		end
		
		if(input:getPressData(93):isPressed()) then -- pagedown
			zoomAdd = -2
		end
	end
	
	if(zoomAdd ~= 0) then
		game.overworld.setCameraZoom(game.overworld.getCameraZoom() + (zoomAdd * delta))
	end
	
	if(xVel == 0 and yVel == 0) then
		_object:setAnimation(C_ANIM_SET, C_IDLE_ANIMS[_direction])
	else
		_object:setAnimation(C_ANIM_SET, C_MOVE_ANIMS[_direction])
		local xPos, yPos = _object:getPosition()
		_object:setPosition(xPos + (xVel * delta), yPos + (yVel * delta))
	end
	
	game.overworld.setCameraPosition(_object:getPosition())
end

function onRender()     -- called when the object is rendered; if you need to render something too then do it here
end

-- TBI below
function onInteract(object)   -- triggered when the object gets interacted with by the player character, method is given player object; or if this object is a player character, whenever the player character tries to interact with anything, method is given interacted object
end

function onCollide(object, collosionType)
end
