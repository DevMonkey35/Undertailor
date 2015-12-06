local faderObj = game.ui.newObject()
local faderComp = game.ui.newComponent("fader", false, colors.presets.WHITE)
faderComp.onDestroy = function()
	fadeInObj = game.ui.newObject()
	fadeInComp = game.ui.newComponent("fader", false, colors.presets.WHITE, nil, true)
	fadeInObj:registerComponent(fadeInComp)
	game.ui.registerObject(fadeInObj)
end

faderObj:registerComponent(faderComp);
game.ui.registerObject(faderObj);
game.overworld.setCurrentRoom(game.overworld.newWorldRoom("room1"))
