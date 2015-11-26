package me.scarlet.undertailor.wrappers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.File;

public class SoundWrapper extends DisposableWrapper<Sound> {

    private File fileReference;
    public SoundWrapper(File fileReference) {
        super(null);
        
        this.fileReference = fileReference;
    }

    @Override
    public void newReference() {
        this.disposable = Gdx.audio.newSound(Gdx.files.absolute(fileReference.getAbsolutePath()));
        this.lastAccess = TimeUtils.millis();
    }
}
