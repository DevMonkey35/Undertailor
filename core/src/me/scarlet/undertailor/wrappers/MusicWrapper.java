package me.scarlet.undertailor.wrappers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import java.io.File;

public class MusicWrapper extends DisposableWrapper<Music> {

    public static final long MAX_LIFETIME = 60000; // 1min
    
    private File fileReference;
    public MusicWrapper(File fileReference) {
        super(null);
        
        this.fileReference = fileReference;
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
}
