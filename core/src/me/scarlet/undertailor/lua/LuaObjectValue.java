package me.scarlet.undertailor.lua;

import org.luaj.vm2.LuaTable;

import me.scarlet.undertailor.exception.LuaScriptException;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class LuaObjectValue<T> extends LuaTable {

    static final Map<Object, LuaObjectValue<?>> STORED;

    static {
        STORED = new WeakHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public static <T> LuaObjectValue<T> of(T object) {
        if (LuaObjectValue.STORED.containsKey(object)) {
            return (LuaObjectValue<T>) LuaObjectValue.STORED.get(object);
        }

        return new LuaObjectValue<>(object);
    }

    private String typename;
    private LuaObjectMeta meta;
    private WeakReference<T> ref;

    @SuppressWarnings("unchecked")
    public LuaObjectValue(T object) {
        this.ref = new WeakReference<>(object);

        LuaObjectValue.STORED.put(object, this);
        LuaObjectMeta meta = Lua.getMeta(object);
        if (meta != null) {
            this.meta = meta;
            this.typename = meta.getTypeName();
            if(meta.getMetatable() != null) this.setmetatable(meta.getMetatable());
        }

        if (object instanceof LuaImplementable) {
            ((LuaImplementable<T>) object).setObjectValue(this);
        }
    }

    @Override
    public String typename() {
        if (this.typename != null) {
            return this.typename;
        }

        return super.typename();
    }

    public T getObject() {
        return this.ref.get();
    }

    public LuaObjectMeta getMeta() {
        return this.meta;
    }

    public void load(ScriptManager manager, File luaFile)
        throws FileNotFoundException, LuaScriptException {
        manager.loadAsModule(luaFile, this);
    }
}
