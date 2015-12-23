-- changeable variables
local maxScaleOffset = 0.1  -- how much larger/smaller a character can get

-- actual code
function applyCharacter()
    offsetX = ((maxScaleOffset * 2) * util.randomDouble()) - maxScaleOffset
    offsetY = ((maxScaleOffset * 2) * util.randomDouble()) - maxScaleOffset
    
    return text.newDisplayMeta(0, 0, 1.0 + offsetX, 1.0 + offsetY)
	-- return text.newDisplayMeta()
end