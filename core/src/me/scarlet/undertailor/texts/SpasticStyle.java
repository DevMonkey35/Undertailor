package me.scarlet.undertailor.texts;

import me.scarlet.undertailor.texts.TextComponent.DisplayMeta;

import java.util.Random;

public class SpasticStyle implements Style {
    @Override
    public DisplayMeta applyCharacter() {
        DisplayMeta meta = new DisplayMeta();
        Random random = new Random();
        float sm = (0.2F * random.nextFloat()) - 0.1F;
        meta.scaleX = 1.0F + sm;
        meta.scaleY = 1.0F + sm;
        return meta;
    }
}
