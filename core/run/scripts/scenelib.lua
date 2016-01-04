-- scene lib
local varlib = load("scripts/varlib.lua")

local function textbox_genanimtask(player, anim)
	local task = {}
	task.anim = anim
	task.player = player
	task.process = function(self, delta, input)
		if(self.player:isWaiting()) then
			self.anim:stop()
			return true
		end
		
		return false
	end
end

function textbox(texts, postype, avatar, px, py, sx, sy, tx)
	texts = varlib.checkarg(1, "table", texts)
	varlib.checktablesize(1, texts)
	varlib.checkitable(1, "tailor-text", texts)
	-- 0 - default size and bottom, 1 - default size and top, 2 - custom position and dynamic size, 3 - custom pos and specific size
	_postype = varlib.checkarg(2, "number", postype, 0)
	avatar = varlib.checkarg(3, "tailor-animation", avatar, -1)
	if(_postype == 0 or _postype == 1) then
		sx, sy = 566, 140
		if(_postype == 0) then
			px, py = 38, 14
		else
			px, py = 38, 326 -- ?
		end
	elseif(_postype == 2 or _postype == 3) then
		varlib.checkarg(4, "number", px)
		varlib.checkarg(5, "number", py)
		if(_postype == 3) then
			varlib.checkarg(6, "number", sx)
			varlib.checkarg(7, "number", sy)
		end
	end
	
	local obj = game.ui.newObject()
	local boxcomp = game.ui.newComponent("boxed", nil, nil, sx, sy, 6)
	local playercomp = game.ui.newComponent("textplayer", texts)
	
	playercomp:setPosition(22, sy - 40)
	
	obj:setPosition(px, py)
	obj:registerComponent(boxcomp)
	obj:registerComponent(playercomp)
	
	if(avatar ~= -1) then
		local animcomp = game.ui.newComponent("animbox", avatar)
		playercomp:setPosition(138, sy - 40)
		animcomp:setPosition(70, 70)
		obj:registerComponent(animcomp)
	end
	
	game.ui.registerObject(obj)
end