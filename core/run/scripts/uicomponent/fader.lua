-- Fader UI component
-- Just used to cover the screen with a giant box that gradually increases or decreases in alpha to achieve a fading effect.

local _fillColor;
local _component;
local _startTime; -- time we compare against
local _fadeTime;  -- how long the fade should last, in milliseconds (converted from seconds)
local _cAlpha;
local _reverse;   -- false by default to fade out, true to fade in

function create(component, fillColor, fadeTime, reverse)
	_component = component;
	
	if(fillColor == nil) then
		_fillColor = colors.presets.BLACK
	else 
		if(type(fillColor) ~= "gdx-color") then
			error("bad argument #2: expected gdx-color, got "..type(fillColor))
		end
		
		_fillColor = fillColor
	end
	
	if(fadeTime == nil) then
		_fadeTime = 3000.0; -- 3 seconds in ms
	else
		if(type(fadeTime) ~= "number") then
			error("bad argument #3: expected number, got "..type(fadeTime))
		end
		
		_fadeTime = fadeTime * 1000.0 -- convert seconds to ms
	end
	
	if(reverse == nil) then
		_reverse = false
	else
		if(type(reverse) ~= "boolean") then
			error("bad argument #4: expected boolean, got"..type(reverse))
		end
		
		_reverse = reverse
	end
	
	if(_reverse) then
		_cAlpha = 1.0 -- preset it to 1.0 since nil'll flash
	end
	
	print(_reverse)
end

function process(fDelta)
	if(_startTime == nil or _startTime <= 0) then
		_startTime = time.millis()
	end
	
	since = time.sinceMillis(_startTime)
	_cAlpha = since / _fadeTime
	if(_reverse) then
		_cAlpha = (_cAlpha * -1) + 1.0;
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
	local r, g, b, a = oldColor:getRGB()
	_fillColor:setRGB(nil, nil, nil, _cAlpha);
	game.graphics.setShapeColor(_fillColor);
	game.graphics.drawFilledRectangle(0, 0, 640, 480);
	game.graphics.setShapeColor(oldColor);
end
