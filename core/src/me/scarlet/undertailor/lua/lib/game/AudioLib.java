package me.scarlet.undertailor.lua.lib.game;

import static me.scarlet.undertailor.util.LuaUtil.valueOrNil;

import me.scarlet.undertailor.audio.AudioManager;
import me.scarlet.undertailor.lua.LuaLibrary;

public class AudioLib extends LuaLibrary {

    public AudioLib(AudioManager manager) {
        super("audio");

        // ---------------- g/s volumes ----------------

        // audio.getMasterVolume()
        registerFunction("getMasterVolume", vargs -> {
            return valueOf(manager.getMasterVolume());
        });

        // audio.setMasterVolume(nVol)
        registerFunction("setMasterVolume", vargs -> {
            manager.setMasterVolume(vargs.checknumber(1).tofloat());
            return NIL;
        });

        // audio.getMusicVolume()
        registerFunction("getMusicVolume", vargs -> {
            return valueOf(manager.getMusicVolume());
        });

        // audio.setMusicVolume(nVol)
        registerFunction("setMusicVolume", vargs -> {
            manager.setMusicVolume(vargs.checknumber(1).tofloat());
            return NIL;
        });

        // audio.getSoundVolume()
        registerFunction("getSoundVolume", vargs -> {
            return valueOf(manager.getSoundVolume());
        });

        // audio.setSoundVolume(nVol)
        registerFunction("setSoundVolume", vargs -> {
            manager.setSoundVolume(vargs.checknumber(1).tofloat());
            return NIL;
        });

        // ---------------- loading methods ----------------

        // audio.getSound(sKey)
        registerFunction("getSound", vargs -> {
            return valueOrNil(manager.getSound(vargs.checkjstring(1)));
        });

        // audio.getMusic(sKey)
        registerFunction("getMusic", vargs -> {
            return valueOrNil(manager.getMusic(vargs.checkjstring(1)));
        });

        // ---------------- functional methods ----------------

        // audio.stopAllAudio()
        registerFunction("stopAllAudio", vargs -> {
            manager.stopAllAudio();
            return NIL;
        });
    }
}
