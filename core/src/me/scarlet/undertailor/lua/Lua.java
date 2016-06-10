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
