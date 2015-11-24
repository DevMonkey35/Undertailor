package me.scarlet.undertailor.texts;

import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;

public interface Style {
    public DisplayMeta applyCharacter(int charIndex, int textLength); // called for every letter, not including spaces
    public default void onNextTextRender(float delta) {} // called the next time a bit of text wants to be rendered
    public Style duplicate(); // replicate the style completely
}
