function testtext()
    game.audio.sound.getSound("ds_ovw"):play()
    game.audio.music.getMusic("mus_vsasgore"):play(true)
    local txt = nil
    --for i=0,500 do
        txt = text.newText("8bitop", "swimmyworm", colors.presets.YELLOW)
        txt:addComponents(
            text.component.newComponent("You know, it doesn't even "),
            text.component.newComponent("look", nil, "swimmyworm"),
            text.component.newComponent(" like I need to do much here."))
    --end
    
    return txt
end