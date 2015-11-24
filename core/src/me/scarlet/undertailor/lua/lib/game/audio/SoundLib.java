package me.scarlet.undertailor.lua.lib.game.audio;

import com.badlogic.gdx.audio.Sound;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaSound;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class SoundLib extends TwoArgFunction {
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable sound = new LuaTable();
        sound.set("getSoundVolume", new _getSoundVolume());
        sound.set("setSoundVolume", new _setSoundVolume());
        sound.set("getSound", new _getSound());
        sound.set("resume", new _resume());
        sound.set("pause", new _pause());
        sound.set("play", new _play());
        sound.set("stop", new _stop());
        
        if(LuaSound.METATABLE == null) {
            LuaSound.METATABLE = LuaValue.tableOf(new LuaValue[] {INDEX, sound});
        }
        
        env.set("sound", sound);
        return sound;
    }
    
    static class _getSound extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            Sound sound = Undertailor.getAudioManager().getSound(arg.checkstring().tojstring());
            if(sound == null) {
                return LuaValue.NIL;
            }
            
            return new LuaSound(sound);
        }
    }
    
    static class _getSoundVolume extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaValue.valueOf(Undertailor.getAudioManager().getSoundVolume());
        }
    }
    
    static class _setSoundVolume extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            Undertailor.getAudioManager().setSoundVolume(new Float(arg.checkdouble()));
            return LuaValue.NIL;
        }
    }
    
    static class _play extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            Sound sound = LuaSound.checkSound(args.arg1()).getSound();
            boolean loop = args.arg(2).isnil() ? false : args.arg(2).checkboolean();
            float volume = (args.arg(3).isnil() ? 1.0F : new Float(args.arg(3).checkdouble())) * Undertailor.getAudioManager().getSoundVolume();
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
            
            if(loop) {
                return LuaValue.valueOf(sound.loop(volume, pitch, pan));
            } else {
                return LuaValue.valueOf(sound.play(volume, pitch, pan));
            }
        }
    }
    
    static class _stop extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            Sound sound = LuaSound.checkSound(arg1).getSound();
            if(arg2.isnil()) {
                sound.stop();
            } else {
                sound.stop(arg2.checklong());
            }
            
            return LuaValue.NIL;
        }
    }
    
    static class _pause extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            Sound sound = LuaSound.checkSound(arg1).getSound();
            if(arg2.isnil()) {
                sound.pause();
            } else {
                sound.pause(arg2.checklong());
            }
            
            return LuaValue.NIL;
        }
    }
    
    static class _resume extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            Sound sound = LuaSound.checkSound(arg1).getSound();
            if(arg2.isnil()) {
                sound.resume();
            } else {
                sound.resume(arg2.checklong());
            }
            
            return LuaValue.NIL;
        }
    }
}
