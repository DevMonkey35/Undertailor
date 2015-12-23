local papyrus = store.get("scene-tgp-papyrus")

local fadein = game.ui.newObject()
local fadeinc = game.ui.newComponent("fader", true, nil, 1000, true)
local fadeout = game.ui.newObject()
local faderoutc = game.ui.newComponent("fader", true, nil, 1000, false)
local papx, papy = papyrus:getPosition()

game.overworld.setProcessing(false) -- don't let objects process; they could interrupt the cutscene
fadein:registerComponent(fadeinc)
fadeout:registerComponent(faderoutc)
game.overworld.setCameraFixing(false) -- pls don't fix
game.overworld.setCameraZoom(3)
game.overworld.setCameraPosition(papx - 10, papy + 60)

local sweeptask = {
	startTime = nil,
	process = function(self, delta, input)
		if(self.startTime == nil) then
			self.startTime = scheduler.millis()
		end
		
		local x, y = game.overworld.getCameraPosition()
		game.overworld.setCameraPosition(x + (10 * delta))
		return false
	end
}

local sweepid = scheduler.registerTask(sweeptask, false)
