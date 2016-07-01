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

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.lib.MetaLib;
import me.scarlet.undertailor.lua.meta.LuaAudioDataMeta;
import me.scarlet.undertailor.lua.meta.LuaAudioMeta;
import me.scarlet.undertailor.lua.meta.LuaAudioPlayableMeta;
import me.scarlet.undertailor.lua.meta.LuaColliderMeta;
import me.scarlet.undertailor.lua.meta.LuaColorMeta;
import me.scarlet.undertailor.lua.meta.LuaDestructibleMeta;
import me.scarlet.undertailor.lua.meta.LuaEnvironmentMeta;
import me.scarlet.undertailor.lua.meta.LuaEventListenerMeta;
import me.scarlet.undertailor.lua.meta.LuaFontMeta;
import me.scarlet.undertailor.lua.meta.LuaIdentifiableMeta;
import me.scarlet.undertailor.lua.meta.LuaInputDataMeta;
import me.scarlet.undertailor.lua.meta.LuaLayerableMeta;
import me.scarlet.undertailor.lua.meta.LuaMusicMeta;
import me.scarlet.undertailor.lua.meta.LuaOverworldControllerMeta;
import me.scarlet.undertailor.lua.meta.LuaPositionableMeta;
import me.scarlet.undertailor.lua.meta.LuaPressDataMeta;
import me.scarlet.undertailor.lua.meta.LuaProcessableMeta;
import me.scarlet.undertailor.lua.meta.LuaRenderableMeta;
import me.scarlet.undertailor.lua.meta.LuaSoundDataMeta;
import me.scarlet.undertailor.lua.meta.LuaSoundMeta;
import me.scarlet.undertailor.lua.meta.LuaSpriteMeta;
import me.scarlet.undertailor.lua.meta.LuaSubsystemMeta;
import me.scarlet.undertailor.lua.meta.LuaTextComponentMeta;
import me.scarlet.undertailor.lua.meta.LuaTextMeta;
import me.scarlet.undertailor.lua.meta.LuaTextStyleMeta;
import me.scarlet.undertailor.lua.meta.LuaTransformMeta;
import me.scarlet.undertailor.lua.meta.LuaUIComponentMeta;
import me.scarlet.undertailor.lua.meta.LuaUIControllerMeta;
import me.scarlet.undertailor.lua.meta.LuaUIObjectMeta;
import me.scarlet.undertailor.lua.meta.LuaWorldObjectMeta;
import me.scarlet.undertailor.lua.meta.LuaWorldRoomMeta;
import me.scarlet.undertailor.util.LuaUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Static implementation of a class that holds meta data for
 * {@link LuaObjectValue}s.
 */
public class Lua {

    static final Logger log = LoggerFactory.getLogger(Lua.class);
    static final Set<LuaObjectMeta> metas;

    private static final Map<Class<?>, LuaObjectMeta> META;
    private static final Map<Class<?>, LuaObjectMeta> PMETA;
    private static final Map<Class<?>, LuaTable> METATABLES;
    private static final String INVALID_TYPE_MSG = "bad argument: %s expected, got %s";

    static {
        metas = new HashSet<>();
        META = new HashMap<>();
        PMETA = new HashMap<>();
        METATABLES = new HashMap<>();

        loadMeta(LuaAudioDataMeta.class);
        loadMeta(LuaAudioMeta.class);
        loadMeta(LuaAudioPlayableMeta.class);
        loadMeta(LuaColliderMeta.class);
        loadMeta(LuaColorMeta.class);
        loadMeta(LuaDestructibleMeta.class);
        loadMeta(LuaEnvironmentMeta.class);
        loadMeta(LuaEventListenerMeta.class);
        loadMeta(LuaFontMeta.class);
        loadMeta(LuaIdentifiableMeta.class);
        loadMeta(LuaInputDataMeta.class);
        loadMeta(LuaLayerableMeta.class);
        loadMeta(LuaMusicMeta.class);
        loadMeta(LuaOverworldControllerMeta.class);
        loadMeta(LuaPositionableMeta.class);
        loadMeta(LuaPressDataMeta.class);
        loadMeta(LuaProcessableMeta.class);
        loadMeta(LuaRenderableMeta.class);
        loadMeta(LuaSoundDataMeta.class);
        loadMeta(LuaSoundMeta.class);
        loadMeta(LuaSpriteMeta.class);
        loadMeta(LuaSubsystemMeta.class);
        loadMeta(LuaTextComponentMeta.class);
        loadMeta(LuaTextMeta.class);
        loadMeta(LuaTextStyleMeta.class);
        loadMeta(LuaTransformMeta.class);
        loadMeta(LuaUIComponentMeta.class);
        loadMeta(LuaUIControllerMeta.class);
        loadMeta(LuaUIObjectMeta.class);
        loadMeta(LuaWorldObjectMeta.class);
        loadMeta(new LuaWorldRoomMeta(Undertailor.getInstance()));

        log.info("Finished loading lua object metadata");
        log.info("Primary types list: ");
        Lua.PMETA.values().forEach(meta -> {
            log.info(meta.getClass().getSimpleName());
        });
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
        if (obj == null) {
            return null;
        }

        Class<?> target = null;

        if (obj instanceof LuaImplementable
            && ((LuaImplementable<?>) obj).getPrimaryIdentifyingClass() != null) {
            target = ((LuaImplementable<?>) obj).getPrimaryIdentifyingClass();
        } else {
            target = obj.getClass();
        }

        do {
            if (Lua.PMETA.containsKey(target)) {
                return Lua.PMETA.get(target);
            }

            if (Lua.META.containsKey(target)) {
                return Lua.META.get(target);
            }

            for (LuaObjectMeta meta : Lua.PMETA.values()) {
                if (meta.getTargetObjectClass().isInstance(obj)) {
                    return meta;
                }
            }

            for (LuaObjectMeta meta : Lua.META.values()) {
                if (meta.getTargetObjectClass().isInstance(obj)) {
                    return meta;
                }
            }

            if (target != obj.getClass()) {
                target = obj.getClass();
            } else {
                target = null;
            }
        } while (target != null);

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
        Lua.metas.clear();
        Lua.metas.addAll(Lua.PMETA.values());
        Lua.metas.addAll(Lua.META.values());

        Lua.metas.forEach(meta -> {
            if (meta.getTargetObjectClass().isInstance(obj) && meta.getMetatable() != null) {
                LuaUtil.iterateTable(meta.getMetatable(), vargs -> {
                    functable.set(vargs.arg(1), vargs.arg(2));
                });
            }
        });

        Lua.metas.forEach(meta -> {
            meta.postMetaInit(functable);
        });

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
     * Injects all registered metatables into the provided
     * table, with the owning meta's typename as their keys.
     * 
     * <p>Should only be used by {@link MetaLib}.</p>
     */
    public static void injectMetatables(LuaTable table) {
        Lua.PMETA.values().forEach(meta -> {
            if (meta.getMetatable() != null) {
                table.set(meta.getTypeName(), meta.getMetatable());
            }
        });

        Lua.META.values().forEach(meta -> {
            if (meta.getMetatable() != null) {
                table.set(meta.getTypeName(), meta.getMetatable());
            }
        });
    }

    /**
     * Logs a message as the Lua class.
     * 
     * @param message a log message
     */
    public static void log(String message) {
        log.info(message);
    }

    /**
     * Logs an error as the Lua class.
     * 
     * @param message the message associated with the error
     * @param err the thrown exception
     */
    public static void error(String message, Throwable err) {
        log.error(message, err);
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

            loadMeta(meta);
        } catch (Exception e) {
            log.error("Failed to load meta for meta class " + metaClass.getSimpleName(), e);
        }
    }

    /**
     * Internal method.
     * 
     * <p>Used to quickly load {@link LuaObjectMeta}
     * instances by passing the instance.</p>
     */
    static void loadMeta(LuaObjectMeta metaInstance) {
        if (metaInstance.isPrimaryType()) {
            Lua.PMETA.put(metaInstance.getTargetObjectClass(), metaInstance);
        } else {
            Lua.META.put(metaInstance.getTargetObjectClass(), metaInstance);
        }
    }

    /**
     * Internal method.
     * 
     * <p>Returns a stored {@link LuaObjectMeta}
     * instance.</p>
     */
    static LuaObjectMeta getMeta(Class<? extends LuaObjectMeta> metaClass) {
        for (LuaObjectMeta meta : Lua.PMETA.values()) {
            if (meta.getClass() == metaClass) {
                return meta;
            }
        }

        for (LuaObjectMeta meta : Lua.META.values()) {
            if (meta.getClass() == metaClass) {
                return meta;
            }
        }

        return null;
    }
}
