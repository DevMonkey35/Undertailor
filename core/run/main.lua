require("scripts/scenelib.lua")

local room = game.overworld.newWorldRoom("room1");
local character = game.overworld.newWorldObject("character.charfrisk");
local entrytrans = {}
entrytrans.fdid = -1;
entrytrans.name = "entrytrans";
entrytrans.process = function(tbl, delta, input)
	if(tbl.fdid == -1) then
		local faderObj = game.ui.newObject()
		local faderComp = game.ui.newComponent("fader", true, nil, 1000, true)
		faderObj:registerComponent(faderComp)
		tbl.fdid = game.ui.registerObject(faderObj)
	end
	
	return true
end

local exittrans = {}
exittrans.fdid = -1;
exittrans.name = "exittrans";
exittrans.process = function(tbl, delta, input)
	if(tbl.fdid == -1) then
		local faderObj = game.ui.newObject()
		local faderComp = game.ui.newComponent("fader", true, nil, 1000, false)
		faderObj:registerComponent(faderComp)
		tbl.fdid = game.ui.registerObject(faderObj)
	end
	
	return true
end

game.overworld.setEntryTransition(entrytrans)
game.overworld.setExitTransition(exittrans)

character:setPersisting(true)
game.overworld.setCurrentRoom(room, false)
room:registerObject(character);

game.overworld.setCharacterID(character:getID())
