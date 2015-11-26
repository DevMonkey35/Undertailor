package me.scarlet.undertailor.lua.lib.game.audio;

import com.badlogic.gdx.audio.Music;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaMusic;
import me.scarlet.undertailor.lua.LuaSound;
import me.scarlet.undertailor.wrappers.SoundWrapper;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class MusicLib extends TwoArgFunction {
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable music = new LuaTable();
        music.set("getMusicVolume", new _getMusicVolume());
        music.set("setMusicVolume", new _setMusicVolume());
        music.set("getMusic", new _getMusic());
        music.set("resume", new _resume());
        music.set("pause", new _pause());
        music.set("play", new _play());
        music.set("stop", new _stop());
        
        if(LuaMusic.METATABLE == null) {
            LuaMusic.METATABLE = LuaValue.tableOf(new LuaValue[] {INDEX, music});
        }
        
        env.set("music", music);
        return music;
    }
    
    static class _keepSoundLoaded extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            String soundName = arg1.checkjstring();
            boolean flag = arg2.isnil() ? true : arg2.checkboolean();
            
            SoundWrapper wrapper = Undertailor.getAudioManager().getSoundWrapper(soundName);
            if(wrapper != null) {
                wrapper.setAlwaysAlive(flag);
            }
            
            return LuaValue.NIL;
        }
    }
    
    static class _getMusic extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            Music music = Undertailor.getAudioManager().getMusic(arg.checkstring().tojstring());
            if(music == null) {
                return LuaValue.NIL;
            }
            
            return new LuaMusic(music);
        }
    }
    
    static class _getMusicVolume extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaValue.valueOf(Undertailor.getAudioManager().getMusicVolume());
        }
    }
    
    static class _setMusicVolume extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            Undertailor.getAudioManager().setMusicVolume(new Float(arg.checkdouble()));
            return LuaValue.NIL;
        }
    }
    
    static class _play extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            Music music = LuaMusic.checkMusic(args.arg1()).getMusic();
            boolean loop = args.arg(2).isnil() ? false : args.arg(2).checkboolean();
            float volume = (args.arg(3).isnil() ? 1.0F : new Float(args.arg(3).checkdouble())) * Undertailor.getAudioManager().getMusicVolume();
            float pitch = args.arg(4).isnil() ? 1.0F : new Float(args.arg(4).checkdouble());
            float pan = args.arg(5).isnil() ? 1.0F : new Float(args.arg(5).checkdouble());
            
            if(volume > 1.0F) {
                volume = 1.0F;
            }
            
            if(volume < 0.0F) {
                volume = 0.0F;
            }
            
            if(pitch < 0.5F) {
                pitch = 0.5F;
                Undertailor.warn("lua", LuaSound.TYPENAME + ":play() - pitch argument was set to 0.5F (was <0.5F)");
            }
            
            if(pitch > 2.0F) {
                pitch = 2.0F;
                Undertailor.warn("lua", LuaSound.TYPENAME + ":play() - pitch argument was set to 2.0F (was >2.0F)");
            }
            
            if(pan < -1.0F) {
                pan = -1.0F;
            }
            
            if(pan > 1.0F) {
                pan = 1.0F;
            }
            
            music.setPan(pan, volume * Undertailor.getAudioManager().getMusicVolume());
            if(loop) {
                music.setLooping(true);
            }
            
            music.play();
            return LuaValue.NIL;
        }
    }
    
    static class _stop extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            Music music = LuaMusic.checkMusic(arg1).getMusic();
            music.stop();
            
            return LuaValue.NIL;
        }
    }
    
    static class _pause extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            Music music = LuaMusic.checkMusic(arg1).getMusic();
            music.pause();
            
            return LuaValue.NIL;
        }
    }
    
    static class _resume extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            Music music = LuaMusic.checkMusic(arg1).getMusic();
            music.play();
            
            return LuaValue.NIL;
        }
    }
}
