package me.scarlet.undertailor.util;

import ninja.leaping.configurate.ConfigurationNode;

public class ConfigurateUtil {
    
    // TODO replace runtime ex
    public static int processInt(ConfigurationNode node, String logTag) throws RuntimeException {
        if(node.isVirtual()) {
            throw new RuntimeException("value for node " + pathFromArray(node.getPath()) + " not present");
        } else {
            String str = node.getString();
            try {
                return Integer.parseInt(str);
            } catch(NumberFormatException e) {
                throw new RuntimeException("bad value (\"" + str + "\") for node " + pathFromArray(node.getPath()));
            }
        }
    }
    
    public static int[] processIntArray(ConfigurationNode node, String logTag) throws RuntimeException {
        if(node.isVirtual()) {
            throw new RuntimeException("value for node " + pathFromArray(node.getPath()) + " not present");
        } else {
            String str = node.getString();
            try {
                return toPrimitiveArray(node.getList(obj -> {
                    return (int) Integer.parseInt(obj.toString());
                }).toArray(new Integer[0]));
            } catch(NumberFormatException e) {
                throw new RuntimeException("bad value (\"" + str + "\") for node " + pathFromArray(node.getPath()));
            }
        }
    }
    
    public static String pathFromArray(Object[] path) {
        StringBuilder sb = new StringBuilder();
        for(Object obj : path) {
            sb.append("." + obj.toString());
        }
        
        return sb.toString().substring(1);
    }
    
    public static int[] toPrimitiveArray(Integer[] array) {
        int[] returned = new int[array.length];
        for(int i = 0; i < returned.length; i++) {
            returned[i] = array[i];
        }
        
        return returned;
    }
}
