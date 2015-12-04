local faderObj = ui.newObject()
local faderComp = ui.newComponent("fader")
faderComp.onDestroy = function()
	fadeInObj = ui.newObject()
	fadeInComp = ui.newComponent("fader", nil, nil, true)
	fadeInObj:registerComponent(fadeInComp)
	
	fadeBuffer = ui.newObject(10, true)
	fadeBufferComp = ui.newComponent("blank")
	fadeBufferComp.render = function()
		local oldColor = game.graphics.getShapeColor();
		game.graphics.setShapeColor(colors.fromRGB(0, 0, 0, 1));
		game.graphics.drawFilledRectangle(0, 0, 640, 480);
		game.graphics.setShapeColor(oldColor);
	end
	
	fadeBuffer:registerComponent(fadeBufferComp)
	
	ui.registerObject(fadeBuffer)
	ui.registerObject(fadeInObj)
end

faderObj:registerComponent(faderComp);
ui.registerObject(faderObj);
