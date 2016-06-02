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
