-- Fader UI component
-- Just used to cover the screen with a giant box that gradually increases or decreases in alpha to achieve a fading effect.

local varlib = load("scripts/varlib.lua")

local _fillColor;
local _component;
local _startTime; -- time we compare against
local _fadeTime;  -- how long the fade should last, in milliseconds (converted from seconds)
local _cAlpha;
local _reverse;   -- false by default to fade out, true to fade in
local _blockInput

function create(component, blockInput, fillColor, fadeTime, reverse)
	_component = component;
	_blockInput = varlib.checkarg(2, "boolean", blockInput, false)
	_fillColor = varlib.checkarg(3, "gdx-color", fillColor, colors.fromHex("000000"))
	_fadeTime = varlib.checkarg(4, "number", fadeTime, 3000)
	_reverse = varlib.checkarg(5, "boolean", reverse, false)
end

function process(fDelta, input)
	if(_blockInput) then
		input:consume()
	end
	
	if(_startTime == nil or _startTime <= 0) then
		_startTime = scheduler.millis()
	end
	
	since = scheduler.sinceMillis(_startTime)
	_cAlpha = since / _fadeTime
	if(_reverse) then
		if(_cAlpha ~= 0) then
			_cAlpha = (_cAlpha * -1) + 1.0;
		end
		
		if(_cAlpha < 0.0) then
			_component:destroyParent()
		end
	else
		if(_cAlpha >= 1.0) then
			_component:destroyParent()
		end
	end
end

function render()
	local oldColor = game.graphics.getShapeColor();
	game.graphics.setShapeColor(_fillColor, _cAlpha);
	game.graphics.drawFilledRectangle(0, 0, 640, 480);
	game.graphics.setShapeColor(oldColor);
end

function onDestroy(b)
	_startTime = nil
end
