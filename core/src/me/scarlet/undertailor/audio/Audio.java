package me.scarlet.undertailor.audio;

public interface Audio<T> {
    
    public float getAffectedVolume();
    public float getPosition();
    public void setPosition(float position);
    public float getVolume();
    public void setVolume(float volume);
    public float getPan();
    public void setPan(float pan);
    public float getPitch();
    public void setPitch(float pitch);
    public boolean isLooping();
    public void setLoopPoint(float loopPoint);
    public boolean isPlaying();
    public boolean isPaused();
    public T play(float volume, float pan, float pitch);
    public T play();
    public void pause(T id);
    public void stop(T id);
    
}
