/* 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor.lua.lib.game;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.audio.Audio;
import me.scarlet.undertailor.audio.MusicWrapper;
import me.scarlet.undertailor.audio.SoundWrapper;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.manager.AudioManager;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.util.NumberUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class AudioLib extends LuaLibrary {
    
    @SuppressWarnings("unchecked")
    public static LuaObjectValue<Audio<?>> check(LuaValue value) {
        if(LuaUtil.isOfType(value, Lua.TYPENAME_SOUND) || LuaUtil.isOfType(value, Lua.TYPENAME_MUSIC)) {
            return (LuaObjectValue<Audio<?>>) value;
        }
        
        throw new LuaError("expected " + Lua.TYPENAME_SOUND + " or " + Lua.TYPENAME_MUSIC + ", got " + value.typename());
    }
    
    public static LuaObjectValue<Audio<Long>> checkSound(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_SOUND);
    }
    
    public static LuaObjectValue<Audio<String>> checkMusic(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_MUSIC);
    }
    
    public static LuaObjectValue<Audio<Long>> createSound(Audio<Long> value) {
        return LuaObjectValue.of(value, Lua.TYPENAME_SOUND, LuaLibrary.asMetatable(Lua.LIB_AUDIO));
    }
    
    public static LuaObjectValue<Audio<String>> createMusic(Audio<String> value) {
        return LuaObjectValue.of(value, Lua.TYPENAME_MUSIC, LuaLibrary.asMetatable(Lua.LIB_AUDIO));
    }
    
    static AudioManager audioman() {
        return Undertailor.getAudioManager();
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new getMasterVolume(),
            new getMusic(),
            new getMusicVolume(),
            new getPan(),
            new getPitch(),
            new getPosition(),
            new getSound(),
            new getSoundVolume(),
            new getVolume(),
            new isLooping(),
            new isPaused(),
            new isPlaying(),
            new pause(),
            new play(),
            new setLooping(),
            new setLoopPoint(),
            new setMasterVolume(),
            new setMusicVolume(),
            new setPan(),
            new setPitch(),
            new setPosition(),
            new setSoundVolume(),
            new setVolume(),
            new stop(),
            new stopAllAudio(),
            new stopAllMusic(),
            new stopAllSounds(),
            new getAudioName()
    };
    
    public AudioLib() {
        super("audio", COMPONENTS);
    }
    
    // ##########################
    // #   Library functions.   #
    // ##########################
    
    // ### Getter/setter functions.
    
    static class getMusicVolume extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            return LuaValue.valueOf(audioman().getMusicManager().getVolume());
        }
    }
    
    static class setMusicVolume extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            audioman().getMusicManager().setVolume(new Float(args.checkdouble(1)));
            return LuaValue.NIL;
        }
    }
    
    static class getSoundVolume extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            return LuaValue.valueOf(audioman().getSoundManager().getVolume());
        }
    }
    
    static class setSoundVolume extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            audioman().getSoundManager().setVolume(new Float(args.checkdouble(1)));
            return LuaValue.NIL;
        }
    }
    
    static class getMasterVolume extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            return LuaValue.valueOf(audioman().getVolume());
        }
    }
    
    static class setMasterVolume extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            audioman().setVolume(new Float(args.checkdouble(1)));
            return LuaValue.NIL;
        }
    }
    static class getMusic extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            String id = args.checkjstring(1);
            return createMusic(audioman().getMusicManager().getResource(id));
        }
    }
    
    static class getSound extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            String id = args.checkjstring(1);
            return createSound(audioman().getSoundManager().getResource(id));
        }
    }
    
    // Interactive functions.
    
    static class stopAllAudio extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            
            audioman().getMusicManager().getAllPlaying().forEach(wrapper -> {
                wrapper.stop("");
            });
            
            audioman().getSoundManager().getAllPlaying().forEach(wrapper -> {
                wrapper.stop((long) -1);
            });
            
            return LuaValue.NIL;
        }
    }
    
    static class stopAllMusic extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            
            audioman().getMusicManager().getAllPlaying().forEach(wrapper -> {
                wrapper.stop("");
            });
            
            return LuaValue.NIL;
        }
    }
    
    static class stopAllSounds extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            
            audioman().getSoundManager().getAllPlaying().forEach(wrapper -> {
                wrapper.stop((long) -1);
            });
            
            return LuaValue.NIL;
        }
    }
    
    // ###########################
    // #   Object metamethods.   #
    // ###########################
    
    // Getter/setter methods.
    
    static class getAudioName extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            return LuaValue.valueOf(audio.getAudioName());
        }
    }
    
    static class getPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            return LuaValue.valueOf(audio.getPosition());
        }
    }
    
    static class setPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            float position = new Float(args.checkdouble(2));
            
            audio.setPosition(position);
            return LuaValue.NIL;
        }
    }
    
    static class isLooping extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            return LuaValue.valueOf(audio.isLooping());
        }
    }
    
    static class setLooping extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            boolean loop = args.checkboolean(2);
            
            if(loop) {
                audio.setLoopPoint(0);
            } else {
                audio.setLoopPoint(-1);
            }
            
            return LuaValue.NIL;
        }
    }
    
    static class setLoopPoint extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            float point = new Float(args.checkdouble(2));
            
            audio.setLoopPoint(point);
            return LuaValue.NIL;
        }
    }
    
    static class getPitch extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            return LuaValue.valueOf(audio.getPitch());
        }
    }
    
    static class setPitch extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            float pitch = new Float(args.checkdouble(2));
            
            audio.setPitch(pitch);
            return LuaValue.NIL;
        }
    }
    
    static class getVolume extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            return LuaValue.valueOf(audio.getVolume());
        }
    }
    
    static class setVolume extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            float volume = NumberUtil.boundFloat(new Float(args.checkdouble(2)), 0.0F, 1.0F);
            
            audio.setVolume(volume);
            return LuaValue.NIL;
        }
    }
    
    static class getPan extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            return LuaValue.valueOf(audio.getPan());
        }
    }
    
    static class setPan extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            float pan = NumberUtil.boundFloat(new Float(args.checkdouble(2)), -1.0F, 1.0F);
            
            audio.setPan(pan);
            return LuaValue.NIL;
        }
    }
    
    static class isPaused extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            return LuaValue.valueOf(audio.isPaused(null));
        }
    }
    
    static class isPlaying extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            return LuaValue.valueOf(audio.isPlaying(null));
        }
    }
    
    static class play extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 4);
            
            Audio<?> audio = check(args.arg1()).getObject();
            float volume = NumberUtil.boundFloat(new Float(args.optdouble(2, audio.getVolume())), 0.0F, 1.0F);
            float pitch = NumberUtil.boundFloat(new Float(args.optdouble(3, audio.getPitch())), 0.5F, 2.0F);
            float pan = NumberUtil.boundFloat(new Float(args.optdouble(4, audio.getPan())), -1.0F, 1.0F);
            
            if(audio instanceof MusicWrapper) {
                return LuaValue.valueOf(((MusicWrapper) audio).play(volume, pan, pitch));
            } else {
                return LuaValue.valueOf(((SoundWrapper) audio).play(volume, pan, pitch));
            }
        }
    }
    
    static class stop extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            long id = args.checklong(2);
            
            if(audio instanceof MusicWrapper) {
                ((MusicWrapper) audio).stop("");
            } else {
                ((SoundWrapper) audio).stop(id);
            }
            
            return LuaValue.NIL;
        }
    }
    
    static class pause extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Audio<?> audio = check(args.arg(1)).getObject();
            long id = args.checklong(2);
            
            if(audio instanceof MusicWrapper) {
                ((MusicWrapper) audio).pause("");
            } else {
                ((SoundWrapper) audio).pause(id);
            }
            
            return LuaValue.NIL;
        }
    }
}
