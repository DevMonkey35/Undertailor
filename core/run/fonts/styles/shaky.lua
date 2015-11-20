-- changeable variables
local shakeDistance = 0.5 -- how far a character is allowed to move from its origin point

-- actual code
function applyCharacter()
    offX = (text.randomDouble() * shakeDistance * 2) - shakeDistance
    offY = (text.randomDouble() * shakeDistance * 2) - shakeDistance
    
    return text.newDisplayMeta(offX, offY)
end