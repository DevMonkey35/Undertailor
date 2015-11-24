function testtext()
    game.audio.sound.getSound("ds_ovw"):play()
    -- game.audio.music.getMusic("mus_vsasgore"):play(true)
    local txt = nil
    -- for i=0,500 do
        txt = text.newText("aster", "swimmyworm", colors.presets.YELLOW)
        txt:addComponents(
            text.component.newComponent("ABCDEFGHIJKLM"))
    -- end
    
    return txt
end