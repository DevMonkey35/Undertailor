-- NPC Papyrus

require("scripts/colliderlib.lua")
require("scripts/scenelib.lua")

local count = 0
local C_ANIM_SET = "papyrus";
local C_IDLE_ANIMS = {"idle_up", "idle_down", "idle_left", "idle_right"}
local C_MOVE_ANIMS = {"walk_up", "walk_down", "walk_left", "walk_right"}
local C_SPECIAL_ANIMS = {"flashycape", "mad"}

local _papyrus;
local interactionCount;

function create(papyrus)
	_papyrus = papyrus
	
	papyrus:getBoundingBox():setDimensions(15, 15)
	papyrus:getBoundingBox():setOrigin(7.5, 7.5)
	papyrus:setAnimation(C_ANIM_SET, C_SPECIAL_ANIMS[2])
	papyrus:setSolid(true)
	interactionCount = 0
end

function onCollide(object)
	colliderlib.snapOnCollide(_papyrus, object)
end

function onInteract(object)
	interactionCount = interactionCount + 1
	dialog(interactionCount)
end

function dialog2(count)
	--store.set("scene-tgp-papyrus", _papyrus)
	--execute("scripts/scenes/thegreatpapyrus.lua")
end

function dialog(count)
	texts = {}
	if(count == 1) then
		texts[1] = {
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent("*WHAT IF SANS IS GASTER"))
		}
		
		texts[2] = {
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent("*You could say he's having a")),
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent(" bad time", nil, nil, colors.presets.BLUE),
				text.component.newComponent("."))
		}
	elseif(count == 2) then
		texts[1] = {
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent("*What, ", nil, nil, nil, nil, nil, 1000),
				text.component.newComponent("you're still here?"))
		}
		
		texts[2] = {
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent("*Don't you have anything better")),
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent(" to do?"))
		}
	elseif(count == 3) then
		texts[1] = {
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent("*I mean, ", nil, nil, nil, nil, nil, 1000),
				text.component.newComponent("its not like you can")),
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent(" even do anything about it.", nil, nil, nil, nil, nil, 1000))
		}
		
		texts[2] = {
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent("*The engine still has a few bugs")),
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent(" that're much bigger than")),
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent(" whatever fiasco Papyrus is in."))
		}
		
		texts[3] = {
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent("*Unless you're willing to write")),
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent(" that for me, you should probably")),
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent(" go do something else."))
		}
	elseif(count == 4) then
		texts[1] = {
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent("*Don't you have anything better")),
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent(" to do?"))
		}
	else
		texts[1] = {
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent("*Looks like the developer ran")),
			text.newText("8bitop", nil, nil, "ds_ovw"):addComponents(
				text.component.newComponent(" out of text."))
		}
	end
	
	for k,v in ipairs(texts) do
		scenelib.textbox(v, 0)
	end
end
