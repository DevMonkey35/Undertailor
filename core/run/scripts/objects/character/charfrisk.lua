-- up 19
-- down 20
-- left 21
-- right 22

local C_ANIM_SET = "frisk"
local C_IDLE_ANIMS = {"idle_up", "idle_down", "idle_left", "idle_right"}
local C_MOVE_ANIMS = {"walk_up", "walk_down", "walk_left", "walk_right"}
local _movementSpeed = 80.0

local _object;
local _interactBox;
local _direction = 2;
local _moving = false
local _animOffset = 0

function create(object)
	_object = object
	
	_object:getBoundingBox():setDimensions(15, 15)
	_object:getBoundingBox():setOrigin(7.5, 7.5)
	_object:setFocusCollide(true)
	_object:setAnimation(C_ANIM_SET, C_IDLE_ANIMS[_direction])
end

function setMoving(bool, direction)
	if(_moving ~= bool or _direction ~= direction) then
		if(bool) then
			_object:setAnimation(C_ANIM_SET, C_MOVE_ANIMS[direction], _animOffset)
		else
			_object:setAnimation(C_ANIM_SET, C_IDLE_ANIMS[direction], _animOffset)
		end
	end
	
	_moving = bool
	_direction = direction
end

function prepareInteractionBox()
	if(_interactBox == nil or _interactBox:getRoom() == nil) then
		_interactBox = game.overworld.newWorldObject("blank")
		_interactBox:setFocusCollide(true)
		_interactBox:setCanCollide(false)
		_interactBox:setSolid(false)
		_interactBox:setCanCollide(false)
		_interactBox.onCollide = function(object)
			if(type(object.onInteract) == "function") then
				if(_object:getID() ~= -1 and object:getID() ~= -1 and _interactBox:getID() ~= _object:getID() and _object:getID() ~= object:getID()) then
					object:onInteract(_object)
					_object:onInteract(object)
					_interactBox:setCanCollide(false)
				end
			end
		end
		
		_object:getRoom():registerObject(_interactBox)
	end
end

function isInteractionBoxActive()
	return _interactBox ~= nil or _interactBox:getRoom() ~= nil
end

function process(delta, input) -- triggered whenever the object actively exists within the current room
	prepareInteractionBox()
	
	local xVel = 0;
	local yVel = 0;
	local zoomAdd = 0;
	local direction = _direction;
	if not (input:isConsumed()) then
		if(input:getPressData(19):isPressed()) then -- up
			direction = 1
			yVel = _movementSpeed
		end
		
		if(input:getPressData(20):isPressed()) then -- down
			direction = 2
			yVel = _movementSpeed * -1
		end
		
		if(input:getPressData(21):isPressed()) then -- left
			direction = 3
			xVel = _movementSpeed * -1
		end
		
		if(input:getPressData(22):isPressed()) then -- right
			direction = 4
			xVel = _movementSpeed
		end
		
		if(isInteractionBoxActive()) then
			if(input:getPressData(54):justPressed() and input:getPressData(54):isPressed() and not _interactBox.pressed) then -- z
				_interactBox.pressed = true
				_interactBox:setCanCollide(true)
			end
			
			if(input:getPressData(54):justReleased() and not input:getPressData(54):isPressed() and _interactBox.pressed) then -- z
				_interactBox.pressed = false
			end
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
	
	local xPos, yPos = _object:getPosition()
	local boundX, boundY = _object:getBoundingBox():getDimensions()
	setMoving(not (xVel == 0 and yVel == 0), direction)
	_direction = direction
	
	_object:setVelocity(xVel * delta, yVel * delta)
	
	game.overworld.setCameraPosition(_object:getPosition())
	if(isInteractionBoxActive()) then -- safecheck since prepare method'll add in next frame
		xPos, yPos = _object:getPosition() -- update since movement changes
		
		if(_direction == 1) then
			_interactBox:getBoundingBox():setDimensions(6, 4)
			_interactBox:getBoundingBox():setOrigin(3, 0)
			_interactBox:setPosition(xPos, yPos + (boundY / 2))
		end
		
		if(_direction == 2) then
			_interactBox:getBoundingBox():setDimensions(6, 4)
			_interactBox:getBoundingBox():setOrigin(3, 4)
			_interactBox:setPosition(xPos, yPos - (boundY / 2))
		end
		
		if(_direction == 3) then
			_interactBox:getBoundingBox():setDimensions(4, 6)
			_interactBox:getBoundingBox():setOrigin(4, 3);
			_interactBox:setPosition(xPos - (boundX / 2), yPos)
		end
		
		if(_direction == 4) then
			_interactBox:getBoundingBox():setDimensions(4, 6)
			_interactBox:getBoundingBox():setOrigin(0, 3);
			_interactBox:setPosition(xPos + (boundX / 2), yPos)
		end
	end
end

function onRender()                  -- called when the object is rendered; if you need to render something too then do it here
end

function onInteract(object)          -- called when the object gets interacted with by the player character, method is given player object; or if this object is a player character, whenever the player character tries to interact with anything, method is given interacted object
end

function onCollide(object)           -- called when the collision engine finds this object colliding with another
	if(object:isSolid()) then
		local xVel, yVel = _object:getVelocity();
		if(xVel == 0 and yVel == 0) then
			_animOffset = 1
			setMoving(false, _direction)
		end
	end
end

function onPersist(room, entrypoint) -- called for any objects that persist across rooms; typically player characters; vars is new room and entrypoint used
	if(entrypoint ~= nil) then
		_object:setPosition(entrypoint:getSpawnPosition())
	end
end

function onPause() -- called when the overworld gets told to pause processing
	_object:setVelocity(0, 0)
	setMoving(false, _direction)
end

function onDestroy()                 -- called for objects whenever the room they're in is destroyed; never called for persisting objects until their persistent state is disabled
end
