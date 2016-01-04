package me.scarlet.undertailor.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import me.scarlet.undertailor.util.NumberUtil;
import me.scarlet.undertailor.wrappers.DisposableWrapper;

import java.io.File;

public class SoundWrapper extends DisposableWrapper<Sound> implements Audio<Long> {

    public static final long MAX_LIFETIME = 30000; // 30s
    
    private File fileReference;
    private AudioResourceManager<SoundWrapper> manager;
    public SoundWrapper(AudioResourceManager<SoundWrapper> manager, File fileReference) {
        super(null);
        
        this.manager = manager;
        this.fileReference = fileReference;
        this.audio();
    }

    @Override
    public Sound newReference() {
        return Gdx.audio.newSound(Gdx.files.absolute(fileReference.getAbsolutePath()));
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
    public void setPitch(float pitch) { this.pitch = NumberUtil.boundFloat(pitch, 0.5F, 2.0F); }
    public boolean isLooping() { return loopPoint >= 0; }
    public void setLoopPoint(float loopPoint) { this.loopPoint = loopPoint; }
    
    // # Sounds don't need to care about these.
    
    @Override
    public float getPosition() { return 0; }
    
    @Override
    public void setPosition(float position) {}

    @Override
    public boolean isPlaying() { return false; }

    @Override
    public boolean isPaused() { return false; }
    
    // #

    @Override
    public Long play(float volume, float pan, float pitch) {
        if(this.isLooping()) {
            return this.getReference().loop(manager.getAffectedVolume() * volume, pitch, pan);
        }
        
        return this.getReference().play(manager.getAffectedVolume() * volume, pitch, pan);
    }

    @Override
    public Long play() {
        return this.play(volume, pan, pitch);
    }

    @Override
    public void pause(Long id) {
        this.getReference().pause(id);
    }

    @Override
    public void stop(Long id) {
        if(id < 0) {
            this.getReference().stop();
        } else {
            this.getReference().stop(id);
        }
    }
}
