-- changeable variables
local shakeDistance = 0.55 -- how far a character is allowed to move from its origin point

-- actual code
function applyCharacter()
    offX = (mathutil.randomDouble() * shakeDistance * 2) - shakeDistance
    offY = (mathutil.randomDouble() * shakeDistance * 2) - shakeDistance
    
    return text.newDisplayMeta(offX, offY)
end