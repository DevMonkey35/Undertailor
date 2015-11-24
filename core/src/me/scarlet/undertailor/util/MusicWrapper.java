package me.scarlet.undertailor.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.File;

public class MusicWrapper extends DisposableWrapper<Music> {

    private File fileReference;
    public MusicWrapper(File fileReference) {
        super(null);
        
        this.fileReference = fileReference;
    }

    @Override
    public void newReference() {
        this.disposable = Gdx.audio.newMusic(Gdx.files.absolute(fileReference.getAbsolutePath()));
        this.lastAccess = TimeUtils.millis();
    }
    
    @Override
    public boolean dispose() {
        if(this.disposable.isPlaying()) {
            return false;
        }
        
        return super.dispose();
    }
}
