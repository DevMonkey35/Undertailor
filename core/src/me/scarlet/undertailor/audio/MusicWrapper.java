package me.scarlet.undertailor.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALMusic;
import me.scarlet.undertailor.util.NumberUtil;
import me.scarlet.undertailor.wrappers.DisposableWrapper;
import org.lwjgl.openal.AL10;

import java.io.File;

public class MusicWrapper extends DisposableWrapper<Music> implements Audio<String> {

    public static final long MAX_LIFETIME = 60000; // 1min
    
    private String id;
    private File fileReference;
    private AudioResourceManager<MusicWrapper> manager;
    public MusicWrapper(String id, AudioResourceManager<MusicWrapper> manager, File fileReference) {
        super(null);
        
        this.fileReference = fileReference;
        this.manager = manager;
        this.id = id;
        audio();
    }

    @Override
    public Music newReference() {
        return Gdx.audio.newMusic(Gdx.files.absolute(fileReference.getAbsolutePath()));
    }
    
    @Override
    public long getMaximumLifetime() {
        return MAX_LIFETIME;
    }
    
    @Override
    public boolean allowDispose() {
        if(!this.getReferrers().isEmpty()) {
            return false;
        }
        
        if(!this.isDisposed()) {
            return !this.getReference().isPlaying();
        }
        
        return true;
    }
    
    // audio impl
    
    private float loopPoint;
    private float volume;
    private float pitch;
    private float pan;
    
    void audio() {
        this.volume = 1.0F;
        this.pitch = 1.0F;
        this.pan = 0.0F;
        this.loopPoint = -1F;
    }
    
    public float getAffectedVolume() { return manager.getAffectedVolume() * volume; }
    public float getVolume() { return volume; }
    public void setVolume(float volume) { this.volume = NumberUtil.boundFloat(volume, 0.0F, 1.0F); }
    public float getPan() { return pan; }
    public void setPan(float pan) { this.pan = NumberUtil.boundFloat(pan, -1.0F, 1.0F); }
    public float getPitch() { return pitch; }
    public boolean isLooping() { return loopPoint >= 0; }
    public void setLoopPoint(float loopPoint) { this.loopPoint = loopPoint; }

    @Override
    public void setPitch(float pitch) {
        this.pitch = NumberUtil.boundFloat(pitch, 0.5F, 2.0F);
        if(this.getReference().isPlaying()) {
            AL10.alSourcef(((OpenALMusic) this.getReference()).getSourceId(), AL10.AL_PITCH, this.pitch);
        }
    }
    
    @Override
    public float getPosition() {
        return this.getReference().getPosition();
    }
    
    @Override
    public void setPosition(float position) {
        this.getReference().setPosition(position);
    }
    
    @Override
    public boolean isPlaying() {
        return this.getReference().isPlaying();
    }

    @Override
    public boolean isPaused() {
        return !this.getReference().isPlaying() && this.getReference().getPosition() <= 0.0F;
    }

    @Override
    public String play(float volume, float pan, float pitch) {
        this.setVolume(volume);
        this.setPan(pan);
        this.setPitch(pitch);
        return play();
    }

    @Override
    public String play() {
        this.getReference().setPan(pan, getAffectedVolume());
        updateLoop();
        
        this.getReference().play();
        this.setPitch(this.pitch); // set after
        return id;
    }

    @Override
    public void pause(String id) {
        this.getReference().pause();
    }

    @Override
    public void stop(String id) {
        this.getReference().stop();
    }
    
    void updateLoop() {
        if(loopPoint < 0) {
            this.getReference().setOnCompletionListener(null);
        } else {
            this.getReference().setOnCompletionListener(music -> {
                music.play();
                music.setPosition(loopPoint);
                AL10.alSourcef(((OpenALMusic) music).getSourceId(), AL10.AL_PITCH, this.pitch);
            });
        }
    }
}
