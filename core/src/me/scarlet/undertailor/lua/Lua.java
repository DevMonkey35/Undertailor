/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without
 * limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package me.scarlet.undertailor.lua;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.lua.meta.LuaAudioMeta;
import me.scarlet.undertailor.lua.meta.LuaColorMeta;
import me.scarlet.undertailor.lua.meta.LuaSoundDataMeta;
import me.scarlet.undertailor.lua.meta.LuaSoundMeta;
import me.scarlet.undertailor.lua.meta.LuaTextStyleMeta;
import me.scarlet.undertailor.util.LuaUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Static implementation of a class that holds meta data for
 * {@link LuaObjectValue}s.
 */
public class Lua {

    static final Logger log = LoggerFactory.getLogger(Lua.class);
    private static final Map<Class<?>, LuaObjectMeta> META;
    private static final Map<Class<?>, LuaTable> METATABLES;
    private static final String INVALID_TYPE_MSG = "bad argument: %s expected, got %s";

    static {
        META = new HashMap<>();
        METATABLES = new HashMap<>();

        loadMeta(LuaTextStyleMeta.class);
        loadMeta(LuaAudioMeta.class);
        loadMeta(LuaSoundMeta.class);
        loadMeta(LuaSoundDataMeta.class);
        loadMeta(LuaColorMeta.class);
    }

    // ---------------- functional methods ----------------

    /**
     * Returns the given {@link LuaValue} as the appropriate
     * type of {@link LuaObjectValue}, directed by the
     * provided {@link LuaObjectMeta} type.
     * 
     * <p>If the provided value is not of the appropriate
     * type, a {@link LuaError} is raised.</p>
     * 
     * @param value the LuaValue to check
     * @param clazz the target LuaObjectMeta type to check
     *        with
     * 
     * @return the appropriately typed LuaValue as a
     *         LuaObjectValue
     */
    @SuppressWarnings("unchecked")
    public static <T> LuaObjectValue<T> checkType(LuaValue value,
        Class<? extends LuaObjectMeta> clazz) {
        LuaObjectMeta meta = Lua.getMeta(clazz);
        if (meta == null) {
            throw new IllegalArgumentException(
                "Meta class " + clazz.getSimpleName() + " isn't registered");
        }

        if (value instanceof LuaObjectValue) {
            LuaObjectValue<?> objectValue = ((LuaObjectValue<?>) value);
            if (meta.getTargetObjectClass().isInstance(objectValue.getObject())) {
                return (LuaObjectValue<T>) objectValue;
            }
        }

        throw new LuaError(String.format(INVALID_TYPE_MSG, meta.getTypeName(), value.typename()));
    }

    /**
     * Returns the {@link LuaObjectMeta} associated with the
     * provided object.
     * 
     * <p>If the provided object is an instance of
     * {@link LuaImplementable} and has a primary
     * identifying class, the meta associated with that
     * class is scanned for first.</p>
     * 
     * @param obj the Object to query with
     * 
     * @return the LuaObjectMeta associated with the
     *         provided object, or null if one was not found
     */
    public static LuaObjectMeta getMeta(Object obj) {
        if(obj == null) {
            return null;
        }

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

    /**
     * Generates a metatable containing metafunctions for a
     * given object type using the currently registered
     * {@link LuaObjectMeta}s.
     * 
     * <p>This method will only generate one metatable per
     * class type; the mapping of already made metatables
     * will not be cleared at any point during runtime. If a
     * metatable had already been generated, that metatable
     * is returned.</p>
     * 
     * <p>It is possible for functions within each object
     * meta to override each other when this method
     * generates a new metatable.</p>
     * 
     * <p>If there are no suitable meta objects for the
     * provided object or none of the applicable meta
     * objects have any metafunctions to register, null is
     * returned.</p>
     * 
     * @param obj the Object to generate a metatable for
     * 
     * @return the metatable for the given Object
     */
    public static LuaTable generateMetatable(Object obj) {
        if (Lua.METATABLES.containsKey(obj.getClass())) {
            return Lua.METATABLES.get(obj.getClass());
        }

        LuaTable functable = new LuaTable();
        for (LuaObjectMeta meta : Lua.META.values()) {
            if (meta.getTargetObjectClass().isInstance(obj) && meta.getMetatable() != null) {
                LuaUtil.iterateTable(meta.getMetatable(), vargs -> {
                    functable.set(vargs.arg(1), vargs.arg(2));
                });
            }
        }

        if (LuaUtil.getTableSize(functable) > 0) {
            LuaTable metatable = new LuaTable();
            metatable.set("__index", functable);
            Lua.METATABLES.put(obj.getClass(), metatable);
            return metatable;
        } else {
            Lua.METATABLES.put(obj.getClass(), null);
            return null;
        }
    }

    /**
     * Logs a message as the Lua class.
     */
    public static void log(String message) {
        log.info(message);
    }

    // ---------------- internal methods ----------------

    /**
     * Internal method.
     * 
     * <p>Used to quickly load {@link LuaObjectMeta}
     * instances by passing the class.</p>
     */
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

    /**
     * Internal method.
     * 
     * <p>Returns a stored {@link LuaObjectMeta}
     * instance.</p>
     */
    static LuaObjectMeta getMeta(Class<? extends LuaObjectMeta> metaClass) {
        for (LuaObjectMeta meta : Lua.META.values()) {
            if (meta.getClass() == metaClass) {
                return meta;
            }
        }

        return null;
    }
}
