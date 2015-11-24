function create(posX, posYtext)
    local textdisplay = components.newComponentFrame()
    
    textdisplay.text = text
    textdisplay.displayed = {}
    textdisplay.process = function(fDelta)
        -- processing implementation in lua
    end
    
    textdisplay.onEvent = function(event)
        -- event handling implementation in lua
    end
    
    textdisplay.render = function()
        -- rendering implementation in lua
    end
    
    return textdisplay
end