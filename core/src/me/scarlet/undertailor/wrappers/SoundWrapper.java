package me.scarlet.undertailor.wrappers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.io.File;

public class SoundWrapper extends DisposableWrapper<Sound> {

    private File fileReference;
    public SoundWrapper(File fileReference) {
        super(null);
        
        this.fileReference = fileReference;
    }

    @Override
    public Sound newReference() {
        return Gdx.audio.newSound(Gdx.files.absolute(fileReference.getAbsolutePath()));
    }
    
    @Override
    public boolean allowDispose() {
        return true;
    }
}
