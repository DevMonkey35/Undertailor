package me.scarlet.undertailor.texts;

import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;

public interface Style {
    public default DisplayMeta applyCharacter() { return null; } // called for every letter, not including spaces
    public default DisplayMeta applyWord() { return null; } // called for every word (separated by spaces)
    public default DisplayMeta applyText() { return null; } // called for every full text
}
