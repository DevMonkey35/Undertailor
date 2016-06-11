package me.scarlet.undertailor.lua.lib;

import com.badlogic.gdx.Gdx;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.lib.game.AudioLib;

public class GameLib extends LuaLibrary {

    private Undertailor undertailor;

    private AudioLib audio;

    public GameLib(Undertailor undertailor) {
        super("game");

        this.undertailor = undertailor;
        this.registerFunction("setTitle", vargs -> {
            Gdx.graphics.setTitle(vargs.arg1().checkjstring());
            return LuaValue.NIL;
        });
    }

    @Override
    public void postinit(LuaTable table, LuaValue environment) {
        this.audio = new AudioLib(undertailor.getAssetManager().getAudioManager());
        this.audio.call(null, table);
    }
}
