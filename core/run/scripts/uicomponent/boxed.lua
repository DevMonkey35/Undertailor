-- Boxed UI component
-- Simply just renders an outline box with a black background to surround the component.
-- This component should be registered first in order to ensure it stands as a background for the rest of the components applied to the parent object.

require("scripts/varlib.lua")

local _fillColor;
local _borderColor;
local _width;
local _height;
local _borderThickness;
local _component;

function create(component, borderColor, fillColor, width, height, borderThickness)
    _component = component
	
	_component:setAlwaysActive(true)
	
	_borderColor = varlib.checkarg(2, "gdx-color", borderColor, colors.presets.WHITE)
	_fillColor = varlib.checkarg(3, "gdx-color", fillColor, colors.presets.BLACK)
	_width = varlib.checkarg(4, "number", width)
	_height = varlib.checkarg(5, "number", height)
	_borderThickness = varlib.checkarg(6, "number", borderThickness, 3.0)
end

function onDestroy(bObject) end      -- we're a box, the fuck are we gonna proc
function onEvent(uievent) end        -- ^
function process(delta, input) end   -- ^^
function render()
    local posX, posY = _component:getRealPosition()
	
	game.graphics.setShapeColor(_borderColor, 1)
    game.graphics.drawFilledRectangle(posX - _borderThickness, posY - _borderThickness, _width + (_borderThickness * 2), _height + (_borderThickness * 2))
	
	game.graphics.setShapeColor(_fillColor, 1)
	game.graphics.drawFilledRectangle(posX, posY, _width, _height)
end