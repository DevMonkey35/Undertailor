-- room functions


-- room code

local _room;

function create(room)        -- called when the room needs to be loaded; do everything the room needs here
	_room = room;
	
	papyrus = game.overworld.newWorldObject("npc.papyrus");
	_room:registerObject(game.overworld.newWorldObject("character.charfrisk")); -- temporary
	_room:registerObject(papyrus)
	
	papyrus:setPosition(50, 50)
end

function process(delta, input)       -- called every frame while the player is in the room
end

function onRender()           -- called when the room tries to render
end

function onEnter(tEntrypoint) -- enter using a certain entrypoint
end

function onExit(tEntrypoint)  -- exit using a certain entrypoint, or nil if they tp'd
end
