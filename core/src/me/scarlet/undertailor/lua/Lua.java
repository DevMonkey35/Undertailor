package me.scarlet.undertailor.lua;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.lua.meta.LuaTextStyleMeta;

import java.util.HashMap;
import java.util.Map;

public class Lua {

    static final Logger log = LoggerFactory.getLogger(Lua.class);
    private static final Map<Class<?>, LuaObjectMeta> META;

    static {
        META = new HashMap<>();

        loadMeta(LuaTextStyleMeta.class);
    }

    public static LuaObjectMeta getMeta(Object obj) {
        if (obj instanceof LuaImplementable
            && ((LuaImplementable<?>) obj).getPrimaryIdentifyingClass() != null) {
            Class<?> target = ((LuaImplementable<?>) obj).getPrimaryIdentifyingClass();
            if (Lua.META.containsKey(target)) {
                return Lua.META.get(target);
            }
        }

        if (Lua.META.containsKey(obj.getClass())) {
            return Lua.META.get(obj.getClass());
        }

        for (LuaObjectMeta meta : Lua.META.values()) {
            if (meta.getTargetObjectClass().isInstance(obj)) {
                return meta;
            }
        }

        return null;
    }

    static void loadMeta(Class<? extends LuaObjectMeta> metaClass) {
        try {
            LuaObjectMeta meta = metaClass.newInstance();
            if (meta.getTargetObjectClass() == null) {
                log.error("Failed to load meta for meta class " + metaClass.getSimpleName()
                    + " (invalid target class)");
                return;
            }

            Lua.META.put(meta.getTargetObjectClass(), metaClass.newInstance());
        } catch (Exception e) {
            log.error("Failed to load meta for meta class " + metaClass.getSimpleName(), e);
        }
    }
}
