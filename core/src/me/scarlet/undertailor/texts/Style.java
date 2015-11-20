package me.scarlet.undertailor.texts;

import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;

public interface Style {
    public default DisplayMeta applyCharacter(int charIndex, int textLength) { return null; } // called for every letter, not including spaces
    public default void onNextTextRender(float delta) {} // called the next time a bit of text wants to be rendered
}
