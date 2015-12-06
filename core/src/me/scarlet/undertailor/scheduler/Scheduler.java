package me.scarlet.undertailor.scheduler;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.util.InputRetriever.InputData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Scheduler {
    
    public static int nextId;
    public static final String MANAGER_TAG = "scheduler";
    
    static {
        nextId = 0;
    }
    
    private Map<Integer, Task> tasks;
    private Map<Integer, Task> activeTasks;
    public Scheduler() {
        this.tasks = new HashMap<>();
        this.activeTasks = new LinkedHashMap<>();
    }
    
    public void process(float delta, InputData data) {
        Iterator<Entry<Integer, Task>> iterator = tasks.entrySet().iterator();
        while(iterator.hasNext()) {
            Entry<Integer, Task> entry = iterator.next();
            int id = entry.getKey();
            Task task = entry.getValue();
            String taskName = (task.getName() == null ? "#" + id : task.getName() + " (#" + id + ")");
            
            try {
                if(task.process(delta, data)) {
                    Undertailor.instance.debug(MANAGER_TAG, "task " + taskName + " finished and was removed");
                    iterator.remove();
                }
            } catch(Exception e) {
                Undertailor.instance.warn(MANAGER_TAG, "task " + taskName + " was removed due to caught error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                iterator.remove();
            }
        }
        
        iterator = activeTasks.entrySet().iterator();
        if(iterator.hasNext()) {
            Entry<Integer, Task> entry = iterator.next();
            int id = entry.getKey();
            Task task = entry.getValue();
            String taskName = (task.getName() == null ? "#" + id : task.getName() + " (#" + id + ")");
            
            try {
                if(task.process(delta, data)) {
                    Undertailor.instance.debug(MANAGER_TAG, "active task " + taskName + " finished and was removed");
                    iterator.remove();
                }
            } catch(Exception e) {
                Undertailor.instance.warn(MANAGER_TAG, "active task " + taskName + " was removed due to caught error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                iterator.remove();
            }
        }
    }
    
    public int registerTask(Task task, boolean active) {
        int id = nextId++;
        String taskName = (task.getName() == null ? "#" + id : task.getName() + " (#" + id + ")");
        if(active) {
            activeTasks.put(id, task);
            Undertailor.instance.debug(MANAGER_TAG, "active task " + taskName + " registered");
        } else {
            tasks.put(id, task);
            Undertailor.instance.debug(MANAGER_TAG, "task " + taskName + " registered");
        }
        
        return id;
    }
    
    public void cancelTask(int id) {
        if(tasks.containsKey(id)) {
            Task task = tasks.get(id);
            String taskName = (task.getName() == null ? "#" + id : task.getName() + " (#" + id + ")");
            
            task.onFinish(true);
            Undertailor.instance.debug(MANAGER_TAG, "task " + taskName + " was removed by scheduler call");
            tasks.remove(id);
        }
        
        if(activeTasks.containsKey(id)) {
            Task task = activeTasks.get(id);
            String taskName = (task.getName() == null ? "#" + id : task.getName() + " (#" + id + ")");
            
            task.onFinish(true);
            Undertailor.instance.debug(MANAGER_TAG, "active task " + taskName + " was removed by scheduler call");
            activeTasks.remove(id);
        }
    }
}
