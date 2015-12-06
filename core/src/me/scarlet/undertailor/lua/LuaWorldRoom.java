package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.lib.meta.LuaWorldRoomMeta;
import me.scarlet.undertailor.overworld.WorldRoom;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.wrappers.RoomDataWrapper;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Map;

public class LuaWorldRoom extends LuaTable {
    
    public static final String TYPENAME = "tailor-room";
    public static LuaValue METATABLE;
    
    public static final String IMPLMETHOD_CREATE = "create";
    public static final String IMPLMETHOD_PROCESS = "process";
    public static final String IMPLMETHOD_ONRENDER = "onRender";
    public static final String IMPLMETHOD_ONENTER = "onEnter";
    public static final String IMPLMETHOD_ONEXIT = "onExit";
    
    public static String[] REQUIRED_METHODS = new String[] {IMPLMETHOD_CREATE};
    public static String[] METHODS = new String[] {IMPLMETHOD_CREATE, IMPLMETHOD_PROCESS, IMPLMETHOD_ONRENDER, IMPLMETHOD_ONENTER, IMPLMETHOD_ONEXIT};
    
    static {
        LuaWorldRoomMeta.prepareMetatable();
    }
    
    public static LuaWorldRoom checkWorldRoom(LuaValue value) {
        if(!value.typename().equals(TYPENAME)) {
            throw new LuaError("bad argument: expected " + TYPENAME + ", got " + value.typename());
        }
        
        return (LuaWorldRoom) value;
    }
    
    public static class LuaRoomImpl extends WorldRoom {
        private Map<String, LuaFunction> functions;
        public LuaRoomImpl(LuaWorldRoom parent, RoomDataWrapper map) throws LuaScriptException {
            super(map.getRoomScript().getName().split("\\.")[0], map);
            Globals globals = Undertailor.newGlobals();
            globals.loadfile(map.getRoomScript().getAbsolutePath()).invoke();
            functions = LuaUtil.checkImplementation(globals, map.getRoomScript(), REQUIRED_METHODS);
        }
        
        public Map<String, LuaFunction> getFunctions() {
            return functions;
        }
        
        @Override
        public void render() {
            super.render();
            if(functions.containsKey(IMPLMETHOD_ONRENDER)) {
                functions.get(IMPLMETHOD_ONRENDER).call();
            }
        }
        
        @Override
        public void process(float delta, InputData input) {
            if(functions.containsKey(IMPLMETHOD_PROCESS)) {
                functions.get(IMPLMETHOD_PROCESS).call(LuaValue.valueOf(delta), new LuaInputData(input));
            }
            
            super.process(delta, input);
        }
    }
    
    private WorldRoom room;
    public LuaWorldRoom(WorldRoom room) {
        this.setmetatable(METATABLE);
        this.room = room;
        prepareLuaRoom();
    }
    
    public LuaWorldRoom(RoomDataWrapper roomMap) throws LuaScriptException {
        this.setmetatable(METATABLE);
        this.room = new LuaRoomImpl(this, roomMap);
        ((LuaRoomImpl) this.room).getFunctions().get(IMPLMETHOD_CREATE).call(this);
        prepareLuaRoom();
    }
    
    private void prepareLuaRoom() {
        if(this.room instanceof LuaRoomImpl) {
            Map<String, LuaFunction> functions = ((LuaRoomImpl) this.room).getFunctions();
            for(Map.Entry<String, LuaFunction> entry : functions.entrySet()) {
                this.set(entry.getKey(), entry.getValue());
            }
        }
    }
    
    @Override
    public void rawset(LuaValue key, LuaValue value) {
        super.rawset(key, value);
        if(value != LuaValue.NIL && this.room instanceof LuaRoomImpl) {
            LuaRoomImpl luacom = (LuaRoomImpl) this.room;
            if(key.isstring()) {
                for(String method : METHODS) {
                    if(key.tojstring().equals(method)) {
                        luacom.getFunctions().put(method, value.checkfunction());
                        return;
                    }
                }
            }
        }
    }
    
    public WorldRoom getRoom() {
        return room;
    }
    
    @Override
    public int type() {
        return LuaValue.TTABLE;
    }
    
    @Override
    public String typename() {
        return TYPENAME;
    }
}
