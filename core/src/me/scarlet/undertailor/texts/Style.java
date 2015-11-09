package me.scarlet.undertailor.texts;

import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;

public interface Style {
    public DisplayMeta apply(char character); // called for every letter, not including spaces
}
