-- Text player UI component
-- Plays the text given to it.
-- You probably want it to render on top, so register it last.

local varlib = load("scripts/varlib.lua")
local stringhelper = load("scripts/stringhelper.lua")

local strEndsWith = stringhelper.endsWith

local C_KEYID_Z = 54
local C_KEYID_X = 52

local _component
local _skippable     -- can "x" through the text
local _controllable  -- can press z to advance text
local _blocking      -- whether or not this consumes input
local _texts
local _scale
local _spX
local _spY
local _lineDistance
local _asteriskDistance

local waiting = false
local drawnText = {}
local ctid = 0
local currentText;
local charDelay = 0;
local waitBetween = 0
local nextCharDelay = 0;
local lastTextTime = -1;
local currentCharacter = 1;
local lastCharacterTime = -1;

local function canWriteCharacter(toDraw)
	if(currentText == nil) then
		return false;
	end
	
	if(toDraw == nil) then
		return false;
	end
	
	currentComp = currentText:getComponentAtCharacter(currentCharacter)
	currentDrawn = toDraw:getComponentAtCharacter(currentCharacter)
	if(util.trim(currentDrawn:getText()):len() == util.trim(currentComp:getText()):len() and charDelay <= 0) then
		nextCharDelay = 1000.0 * currentDrawn:getDelay()
	end
	
	if(currentText:getSpeed() <= 0) then
		textSpeed = 1000
	else
		textSpeed = 1000.0 / currentComp:getSpeed()
	end
	
	compare = scheduler.sinceMillis(lastCharacterTime + textSpeed + charDelay)
	if(lastCharacterTime ~= -1 and compare < 0) then
		return false
	end
	
	returned = lastCharacterTime <= -1 or compare >= 0
	if(returned) then
		if(charDelay == 0) then
			charDelay = nextCharDelay
			nextCharDelay = 0
		else
			charDelay = 0
		end
	end
	
	return returned;
end

local function skip()
	for i=1,#_texts do
		drawnText[i] = _texts[i]
	end
	
	ctid = #_texts
	currentText = _texts[#_texts]
	currentCharacter = currentText:getText():len()
end

function create(component, texts, blocking, skippable, controllable, scale, lineDistance, asteriskDistance)
	_component = component;
	_texts = varlib.checkarg(2, "table", texts)
	_blocking = varlib.checkarg(3, "boolean", blocking, true)
	_skippable = varlib.checkarg(4, "boolean", skippable, true)
	_controllable = varlib.checkarg(5, "boolean", controllable, true)
	_scale = varlib.checkarg(6, "number", scale, 2)
	_lineDistance = varlib.checkarg(7, "number", lineDistance, 18)
	_asteriskDistance = varlib.checkarg(8, "number", asteriskDistance, 32)
	
	for k,v in ipairs(texts) do
		local ok = pcall(varlib.checkarg, nil, "tailor-text", v)
		if not (ok) then
			error("bad argument #2: table contained entries not of the type tailor-text")
		end
	end
end

function process(delta, input)
	local waitTimeDone = lastTextTime == -1 or scheduler.sinceMillis(lastTextTime + waitBetween) > 0
	local lastTextDone = currentText == nil or currentCharacter == currentText:getText():len()
	if(waitTimeDone and lastTextDone) then
		ctid = ctid + 1
		if(ctid <= #_texts and _texts[ctid] ~= nil) then
			currentText = _texts[ctid]
			txt = currentText:getText()
			currentCharacter = 0
			if(stringhelper.startsWith(txt, "*") or stringhelper.startsWith(txt, " ")) then
				currentCharacter = 1
			end
			
			lastTextTime = scheduler.millis()
			waitBetween = 1000.0 * currentText:getDelay()
		else
			_skippable = false -- force disable skippable since we're done anyway
			if(_controllable) then
				waiting = true
				if(input:getPressData(C_KEYID_Z):justPressed()) then
					_component:destroyParent()
				end
			else
				_component:destroyParent()
			end
		end
	end
	
	if(not lastTextDone) then
		local toDraw = currentText:substring(0, currentCharacter + 1)
		if(canWriteCharacter(toDraw) and currentCharacter + 1 <= currentText:getText():len()) then
			tx = currentText:substring(0, currentCharacter):getText()
			if(strEndsWith(tx, " ")) then
				currentCharacter = currentCharacter + 2
			else
				currentCharacter = currentCharacter + 1
			end
			
			drawnText[ctid] = currentText:substring(0, currentCharacter)
			sound = currentText:getComponentAtCharacter(currentCharacter):getSound()
			if(sound ~= nil and currentChar ~= currentText:getText():len()) then
				sound:play()
			end
			
			lastCharacterTime = scheduler.millis()
		end
	end
	
	if(_skippable) then
		if(input:getPressData(C_KEYID_X):justPressed()) then
			skip()
		end
	end
	
	if(_blocking) then
		input:consume()
	end
end

function render()
	for i=1,#drawnText do
		local x, y = _component:getRealPosition()
		drawn = drawnText[i];
		txt = drawn:getText()
		if(stringhelper.startsWith(txt, "*") or stringhelper.startsWith(txt, " ")) then
			text.drawText(drawnText[i]:substring(0, 1), x, y - ((i * _lineDistance) * _scale), _scale, _scale)
			x = x + _asteriskDistance
			drawn = drawnText[i]:substring(1)
		end
		
		text.drawText(drawn, x, y - ((i * _lineDistance) * _scale), _scale, _scale)
	end
end

function isWaiting()
	return waiting
end