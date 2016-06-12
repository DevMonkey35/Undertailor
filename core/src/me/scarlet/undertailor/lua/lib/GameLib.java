package me.scarlet.undertailor.lua.lib;

import com.badlogic.gdx.Gdx;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.lib.game.AudioLib;
import me.scarlet.undertailor.lua.lib.game.GraphicsLib;

import java.util.HashSet;
import java.util.Set;

public class GameLib extends LuaLibrary {

    private Undertailor undertailor;

    private Set<LuaLibrary> childLibraries;

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
        this.childLibraries = new HashSet<>();
        
        childLibraries.add(new AudioLib(undertailor.getAssetManager().getAudioManager()));
        childLibraries.add(new GraphicsLib(undertailor.getRenderer()));

        childLibraries.forEach(lib -> lib.call(null, table));
    }
}
