package me.scarlet.undertailor.scheduler;

import me.scarlet.undertailor.util.InputRetriever.InputData;

public interface Task {
    
    public String getName();
    
    public boolean process(float delta, InputData input);
    // forced means if it was canceled preemptively either by an error or a call to the scheduler
    public void onFinish(boolean forced);
}
