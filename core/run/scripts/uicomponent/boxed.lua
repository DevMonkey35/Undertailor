-- Boxed UI component
-- Simply just renders an outline box with a black background to surround the component.
-- This component should be registered first in order to ensure it stands as a background for the rest of the components applied to the parent object.

local _fillColor;
local _borderColor;
local _width;
local _height;
local _borderThickness;
local _component;

function create(component, borderColor, fillColor, width, height, borderThickness)
    _component = component
	
	if(fillColor == nil) then
		_fillColor = colors.presets.BLACK
	else
	    if(type(fillColor) ~= "gdx-color") then
			error("bad argument #3: expected gdx-color, got "..type(fillColor))
			return
		end
		
		_fillColor = fillColor
	end
	
	if(borderColor == nil) then
		_borderColor = colors.presets.WHITE
	else 
	    if(type(borderColor) ~= "gdx-color") then
			error("bad argument #2: expected gdx-color, got "..type(borderColor))
			return
		end
		
		_borderColor = borderColor
	end
	
	if(type(width) ~= "number") then
		error("bad argument #4: expected number, got "..type(width))
		return
	end
	_width = width;
	
	if(type(height) ~= "number") then
		error("bad argument #5: expected number, got "..type(height))
		return
	end
	_height = height;
	
	if(borderThickness == nil) then
		_borderThickness = 3.0
	else
		if(type(borderThickness) ~= "number") then
			error("bad argument #6: expected number, got "..type(borderThickness))
			return
		end
		
		_borderThickness = borderThickness
	end
end

function onDestroy(bObject) end      -- we're a box, the fuck are we gonna proc
function onEvent(uievent) end        -- ^
function process(delta, input) end   -- ^^
function render()
    local posX, posY = _component:getRealPosition()
	
	game.graphics.setShapeColor(_borderColor)
    game.graphics.drawFilledRectangle(posX - _borderThickness, posY - _borderThickness, _width + (_borderThickness * 2), _height + (_borderThickness * 2))
	
	game.graphics.setShapeColor(_fillColor)
	game.graphics.drawFilledRectangle(posX, posY, _width, _height)
end