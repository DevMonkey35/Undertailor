-- changeable variables
local charBuffer = 0.2  -- the distance in degrees between each character
local distance = 2.5    -- the radius of the circle each character spins in
local speed = 4.5       -- how many degrees to pass by, per second

-- actual code
local current = 0.0
function onNextTextRender(fDelta)
    current = current + (speed * fDelta)
end

function applyCharacter(iCharIndex, iTextLength)
    if(current >= 360) then
        current = current - 360.0
    end
    
    used = current + (iCharIndex * charBuffer)
    return text.newDisplayMeta(distance * math.cos(used), distance * math.sin(used))
end