package me.scarlet.undertailor.wrappers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import java.io.File;

public class MusicWrapper extends DisposableWrapper<Music> {

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
    public boolean allowDispose() {
        if(!this.isDisposed()) {
            return !this.getReference().isPlaying();
        }
        
        return true;
    }
}
