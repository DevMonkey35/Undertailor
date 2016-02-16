/* 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor.environment.overworld.map;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.environment.overworld.WorldRoom;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.impl.WorldRoomImplementable;
import me.scarlet.undertailor.manager.Manager;
import me.scarlet.undertailor.manager.ScriptManager;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.wrappers.RoomDataWrapper;
import ninja.leaping.configurate.json.JSONConfigurationLoader;
import org.luaj.vm2.Varargs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RoomLoader extends Manager<RoomDataWrapper> {
    
    public static final String MANAGER_TAG = "roomman";
    
    private Map<String, File> scriptFiles;
    private Map<String, RoomDataWrapper> rooms;
    
    public RoomLoader() {
        this.rooms = new HashMap<>();
        this.scriptFiles = new HashMap<>();
    }
    
    public void loadObjects(File directory) {
        loadObjects(directory, null);
        Undertailor.instance.log(MANAGER_TAG, rooms.keySet().size() + " room(s) currently loaded");
    }
    
    public void loadObjects(File dir, String heading) {
        String dirPath = dir.getAbsolutePath();
        if(!dir.exists()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load rooms directory " + dirPath + " (did not exist)");
            return;
        }
        
        if(!dir.isDirectory()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load rooms directory " + dirPath + " (not a directory)");
            return;
        }
        
        Undertailor.instance.log(MANAGER_TAG, "searching for rooms in " + dirPath);
        if(heading == null) {
            heading = "";
        }
        
        for(File file : dir.listFiles(file -> {
            return file.isDirectory() || file.getName().endsWith(".roommap");
        })) {
            if(file.isDirectory()) {
                loadObjects(file, heading + (heading.isEmpty() ? "" : ".") + file.getName() + ".");
                continue;
            }
            
            String roomName = file.getName().substring(0, file.getName().length() - 8);
            String entryName = heading + (heading.isEmpty() ? "" : ".") + roomName;
            
            if(!file.isFile()) {
                Undertailor.instance.warn(MANAGER_TAG, "ignoring room " + entryName + " (bad map file)");
                continue;
            }
            
            JSONConfigurationLoader loader = JSONConfigurationLoader.builder().setFile(file).build();
            try {
                Undertailor.instance.debug(MANAGER_TAG, "loading room " + entryName);
                rooms.put(entryName, new RoomDataWrapper(loader.load()));
            } catch(Exception e) {
                Undertailor.instance.error(MANAGER_TAG, "could not load room " + entryName + ": " + LuaUtil.formatJavaException(e), e);
            }
        }
    }
    
    public void loadScripts(File directory) {
        loadScripts(directory, null);
        Undertailor.instance.log(MANAGER_TAG, rooms.keySet().size() + " room script(s) currently loaded");
    }
    
    public void loadScripts(File dir, String heading) {
        String dirPath = dir.getAbsolutePath();
        if(!dir.exists()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load room scripts directory " + dirPath + " (did not exist)");
            return;
        }
        
        if(!dir.isDirectory()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load room scripts directory " + dirPath + " (not a directory)");
            return;
        }
        
        Undertailor.instance.log(MANAGER_TAG, "searching for room scripts in " + dirPath);
        if(heading == null) {
            heading = "";
        }
        
        for(File file : dir.listFiles(file -> {
            return file.isDirectory() || file.getName().endsWith(".lua");
        })) {
            if(file.isDirectory()) {
                loadObjects(file, heading + (heading.isEmpty() ? "" : ".") + file.getName() + ".");
                continue;
            }
            
            String roomName = file.getName().substring(0, file.getName().length() - 4);
            String entryName = heading + (heading.isEmpty() ? "" : ".") + roomName;
            
            if(!file.isFile()) {
                Undertailor.instance.warn(MANAGER_TAG, "ignoring room script" + entryName + " (bad file)");
                continue;
            }
            
            try {
                Undertailor.instance.debug(MANAGER_TAG, "loading room script " + entryName);
                scriptFiles.put(entryName, file);
            } catch(Exception e) {
                Undertailor.instance.error(MANAGER_TAG, "could not load room script " + entryName + ": " + LuaUtil.formatJavaException(e), e);
            }
        }
    }
    
    public RoomDataWrapper getRoom(String name) {
        if(rooms.containsKey(name)) {
            return rooms.get(name);
        }
        
        Undertailor.instance.warn(MANAGER_TAG, "system requested non-existing room (" + name + ")");
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public LuaObjectValue<WorldRoom> newRoomMap(String name, Varargs args) {
        if(scriptFiles.containsKey(name)) {
            try {
                ScriptManager scriptMan = Undertailor.getScriptManager();
                WorldRoomImplementable impl = scriptMan.getImplementable(WorldRoomImplementable.class);
                return (LuaObjectValue<WorldRoom>) impl.load(name, scriptFiles.get(name), args).getObjectValue();
            } catch(LuaScriptException e) {
                RuntimeException thrown = new RuntimeException("could not load world room script " + name + ": " + LuaUtil.formatJavaException(e));
                thrown.initCause(e);
                throw thrown;
            }
        }
        
        Undertailor.instance.warn(MANAGER_TAG, "system requested non-existing room script (" + name + ")");
        return null;
    }
    
    public File getRoomScript(String name) {
        if(scriptFiles.containsKey(name)) {
            return scriptFiles.get(name);
        }
        
        Undertailor.instance.warn(MANAGER_TAG, "system requested non-existing room script (" + name + ")");
        return null;
    }
}
