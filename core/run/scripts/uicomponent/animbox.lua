require("scripts/varlib.lua")

local _component
local _anim
local _scale

local started

function create(component, animation, scale)
	_component = component
	_anim = varlib.checkarg(2, "tailor-animation", animation)
	_scale = varlib.checkarg(3, "number", 2.0)
	
	component:setAlwaysActive(true)
	started = false
end

function process(delta, input)
	if(not started) then
		started = true
		_anim:play(scheduler.millis())
	end
end

function stop()
	_anim:stop()
end

function render()
	local x, y = _component:getRealPosition()
	_anim:drawCurrentFrame(x, y, _scale)
end