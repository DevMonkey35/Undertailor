local faderObj = game.ui.newObject()
local faderComp = game.ui.newComponent("fader", colors.presets.WHITE)
faderComp.onDestroy = function()
	fadeInObj = game.ui.newObject()
	fadeInComp = game.ui.newComponent("fader", colors.presets.WHITE, nil, true)
	fadeInObj:registerComponent(fadeInComp)
	game.ui.registerObject(fadeInObj)
end

faderObj:registerComponent(faderComp);
game.ui.registerObject(faderObj);
