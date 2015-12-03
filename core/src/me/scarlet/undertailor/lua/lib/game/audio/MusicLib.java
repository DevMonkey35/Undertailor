package me.scarlet.undertailor.lua.lib.game.audio;

import com.badlogic.gdx.backends.lwjgl.audio.OpenALMusic;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaMusic;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.wrappers.MusicWrapper;
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
        music.set("getPitch", new _getPitch());
        music.set("setPitch", new _setPitch());
        music.set("getVolume", new _getVolume());
        music.set("setVolume", new _setVolume());
        music.set("setPan", new _setPan());
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
            MusicWrapper music = Undertailor.getAudioManager().getMusic(arg.checkstring().tojstring());
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
            float volume = new Float(arg.checkdouble());
            if(volume > 1.0F) {
                volume = 1.0F;
            }
            
            if(volume < 0.0F) {
                volume = 0.0F;
            }
            
            Undertailor.getAudioManager().setMusicVolume(volume);
            return LuaValue.NIL;
        }
    }
    
    static class _getPitch extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            MusicWrapper music = LuaMusic.checkMusic(arg).getMusic();
            return LuaValue.valueOf(((OpenALMusic) music.getReference()).getPitch());
        }
    }
    
    static class _setPitch extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            MusicWrapper music = LuaMusic.checkMusic(arg1).getMusic();
            float pitch = new Float(arg2.checkdouble());
            if(pitch < 0.5F) {
                pitch = 0.5F;
            }
            
            if(pitch > 2.0F) {
                pitch = 2.0F;
            }
            
            ((OpenALMusic) music.getReference()).setPitch(pitch);
            return LuaValue.NIL;
        }
    }
    
    static class _getVolume extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            MusicWrapper music = LuaMusic.checkMusic(arg).getMusic();
            return LuaValue.valueOf(music.getReference().getVolume());
        }
    }
    
    static class _setVolume extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            MusicWrapper music = LuaMusic.checkMusic(arg1).getMusic();
            float volume = new Float(arg2.checkdouble());
            if(volume > 1.0F) {
                volume = 1.0F;
            }
            
            if(volume < 0.0F) {
                volume = 0.0F;
            }
            
            music.getReference().setVolume(volume * Undertailor.getAudioManager().getMusicVolume());
            return LuaValue.NIL;
        }
    }
    
    static class _setPan extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            MusicWrapper music = LuaMusic.checkMusic(arg1).getMusic();
            float volume = music.getReference().getVolume();
            float pan = new Float(arg2.checkdouble());
            if(pan < -1.0F) {
                pan = -1.0F;
            }
            
            if(pan > 1.0F) {
                pan = 1.0F;
            }
            
            music.getReference().setPan(pan, volume);
            return LuaValue.NIL;
        }
    }
    
    static class _play extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 5);
            MusicWrapper music = LuaMusic.checkMusic(args.arg1()).getMusic();
            boolean loop = args.arg(2).isnil() ? false : args.arg(2).checkboolean();
            float volume = (args.arg(3).isnil() ? 1.0F : new Float(args.arg(3).checkdouble())) * Undertailor.getAudioManager().getMusicVolume();
            float pan = args.arg(4).isnil() ? 1.0F : new Float(args.arg(4).checkdouble());
            float pitch = args.arg(5).isnil() ? 1.0F : new Float(args.arg(5).checkdouble());
            
            if(volume > 1.0F) {
                volume = 1.0F;
            }
            
            if(volume < 0.0F) {
                volume = 0.0F;
            }
            
            if(pitch < 0.5F) {
                pitch = 0.5F;
            }
            
            if(pitch > 2.0F) {
                pitch = 2.0F;
            }
            
            if(pan < -1.0F) {
                pan = -1.0F;
            }
            
            if(pan > 1.0F) {
                pan = 1.0F;
            }
            
            ((OpenALMusic) music.getReference()).setPitch(pitch);
            music.getReference().setPan(pan, volume * Undertailor.getAudioManager().getMusicVolume());
            if(loop) {
                music.getReference().setLooping(true);
            }
            
            music.getReference().play();
            return LuaValue.NIL;
        }
    }
    
    static class _stop extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            MusicWrapper music = LuaMusic.checkMusic(arg1).getMusic();
            music.getReference().stop();
            
            return LuaValue.NIL;
        }
    }
    
    static class _pause extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            MusicWrapper music = LuaMusic.checkMusic(arg1).getMusic();
            music.getReference().pause();
            
            return LuaValue.NIL;
        }
    }
    
    static class _resume extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            MusicWrapper music = LuaMusic.checkMusic(arg1).getMusic();
            music.getReference().play();
            
            return LuaValue.NIL;
        }
    }
}
