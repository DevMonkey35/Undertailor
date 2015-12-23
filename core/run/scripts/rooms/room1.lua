-- room functions


-- room code

local _room;

function create(room)        -- called when the room needs to be loaded; do everything the room needs here
	_room = room;
	
	--prepareEntrypoints()
	papyrus = game.overworld.newWorldObject("npc.papyrus");
	_room:registerObject(papyrus)
	
	papyrus:setPosition(50, 50)
	print("create called")
end

function prepareEntrypoints()
	local point1 = _room:newEntrypoint()
	local point2 = _room:newEntrypoint()
	
	point1:setPosition(0, 50)
	point1:getBoundingBox():setDimensions(20, 20)
	point1:getBoundingBox():setOrigin(0, 10)
	point1:setRoomTarget("room1:pt2")
	point1:setSpawnPosition(0, 0)
	
	point2:setPosition(100, 50)
	point2:getBoundingBox():setDimensions(20, 20)
	point2:getBoundingBox():setOrigin(20, 10)
	point2:setRoomTarget("room1:pt1")
	point1:setSpawnPosition(100, 100)
	
	_room:registerEntrypoint("pt1", point1)
	_room:registerEntrypoint("pt2", point2)
end

function process(delta, input)       -- called every frame while the player is in the room
end

function onRender()           -- called when the room tries to render
end

function onEnter(tEntrypoint) -- enter using a certain entrypoint, or nil if tp'd in
end

function onExit(tEntrypoint)  -- exit using a certain entrypoint, or nil if tp'd out
end
