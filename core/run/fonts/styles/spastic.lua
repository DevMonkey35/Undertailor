function applyCharacter()
    offset = (0.2 * text.randomDouble()) - 0.1
    scale = 1.0 + offset
    return text.newDisplayMeta(0, 0, scale, scale)
end