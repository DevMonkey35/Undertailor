package me.scarlet.undertailor.manager;

import me.scarlet.undertailor.gfx.Animation;

import java.util.HashMap;
import java.util.Map;

public class AnimationManager {
    // owning object name, animation name, animation
    private Map<String, Map<String, Animation<?>>> animationMap;
    public AnimationManager() {
        this.animationMap = new HashMap<>();
    }
}
