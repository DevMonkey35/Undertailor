title = 'Undertailor Luadocs'
project = 'Undertailor Lua API'
description = 'Lua documentation for Lua libraries provided by Undertailor.'
file = "./source"

format = "markdown"
boilerplate = true
no_space_before_args = true
convert_opt = true
all = true
style = true -- the love ldoc style by aloisdeniel: https://github.com/aloisdeniel/ldoc-styles

--[[custom_display_name_handler = function(item, def)
	if(item.type == "module" or not item.module) then
		return def(item)
	else
		return item.module.mod_name.."."..def(item)
	end
end]]--
