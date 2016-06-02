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

package me.scarlet.undertailor.util;

import ninja.leaping.configurate.ConfigurationNode;

import me.scarlet.undertailor.exception.BadAssetException;

import java.util.function.Function;

public class ConfigUtil {

    public static void checkExists(ConfigurationNode node) throws BadAssetException {
        if (node.isVirtual())
            throw new BadAssetException("Missing configuration value " + toPath(node.getPath()));
    }

    public static <T> T checkValue(T value, Function<T, String> processor) throws BadAssetException {
        String message = processor.apply(value);
        if(message != null) {
            if(message.isEmpty()) {
                throw new BadAssetException();
            } else {
                throw new BadAssetException(message);
            }
        }
        
        return value;
    }

    private static String toPath(Object[] path) {
        String returned = "";
        for (Object obj : path) {
            returned += obj + ".";
        }

        return returned.substring(0, returned.length());
    }
}
