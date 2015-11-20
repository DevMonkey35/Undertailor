-- changeable variables
local shakeDistance = 0.5   -- how far a character is allowed to move from its origin point
local maxScaleOffset = 0.1  -- how much larger/smaller a character can get

-- actual code
function applyCharacter()
    offX = (text.randomDouble() * shakeDistance * 2) - shakeDistance
    offY = (text.randomDouble() * shakeDistance * 2) - shakeDistance
    offScaleX = ((maxScaleOffset * 2) * text.randomDouble()) - maxScaleOffset
    offScaleY = ((maxScaleOffset * 2) * text.randomDouble()) - maxScaleOffset
    
    return text.newDisplayMeta(offX, offY, 1.0 + offScaleX, 1.0 + offScaleY)
end