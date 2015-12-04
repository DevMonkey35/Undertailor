local faderObj = ui.newObject()
local faderComp = ui.newComponent("fader", colors.presets.WHITE)
faderComp.onDestroy = function()
	fadeInObj = ui.newObject()
	fadeInComp = ui.newComponent("fader", colors.presets.WHITE, nil, true)
	fadeInObj:registerComponent(fadeInComp)
	ui.registerObject(fadeInObj)
end

faderObj:registerComponent(faderComp);
ui.registerObject(faderObj);
