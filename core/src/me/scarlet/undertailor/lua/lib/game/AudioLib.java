package me.scarlet.undertailor.lua.lib.game;

import me.scarlet.undertailor.lua.lib.game.audio.MusicLib;
import me.scarlet.undertailor.lua.lib.game.audio.SoundLib;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class AudioLib extends TwoArgFunction {
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable audio = new LuaTable();
        new SoundLib().call(LuaValue.valueOf(""), audio);
        new MusicLib().call(LuaValue.valueOf(""), audio);
        
        env.set("audio", audio);
        return audio;
    }
    
    
}
