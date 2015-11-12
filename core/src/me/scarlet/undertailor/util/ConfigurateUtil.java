package me.scarlet.undertailor.util;

import static me.scarlet.undertailor.Undertailor.debug;

import me.scarlet.undertailor.exception.ConfigurationException;
import me.scarlet.undertailor.exception.NoRecordedValueException;
import ninja.leaping.configurate.ConfigurationNode;

public class ConfigurateUtil {
    
    public static int processInt(ConfigurationNode node, Integer defaultt) {
        if(node.isVirtual()) {
            if(defaultt == null) {
                throw new NoRecordedValueException("value for node " + pathFromArray(node.getPath()) + " not present");
            } else {
                return defaultt;
            }
        } else {
            String str = node.getString();
            try {
                Integer returned = Integer.parseInt(str);
                debug("configutil", "int retrieve at " + pathFromArray(node.getPath()) + " returned " + returned);
                return returned;
            } catch(NumberFormatException e) {
                throw new ConfigurationException("bad value (\"" + str + "\") for node " + pathFromArray(node.getPath()));
            }
        }
    }
    
    public static float processFloat(ConfigurationNode node, Float defaultt) {
        if(node.isVirtual()) {
            if(defaultt == null) {
                throw new NoRecordedValueException("value for node " + pathFromArray(node.getPath()) + " not present");
            } else {
                return defaultt;
            }
        } else {
            String str = node.getString();
            try {
                Float returned = Float.parseFloat(str);
                debug("configutil", "float retrieve at " + pathFromArray(node.getPath()) + " returned " + returned);
                return returned;
            } catch(NumberFormatException e) {
                throw new ConfigurationException("bad value (\"" + str + "\") for node " + pathFromArray(node.getPath()));
            }
        }
    }
    
    public static int[] processIntArray(ConfigurationNode node, Integer[] defaultt) {
        if(node.isVirtual()) {
            if(defaultt == null) {
                throw new NoRecordedValueException("value for node " + pathFromArray(node.getPath()) + " not present");
            } else {
                return toPrimitiveArray(defaultt);
            }
        } else {
            String str = node.getString();
            try {
                Integer[] returned = node.getList(obj -> {
                    return (int) Integer.parseInt(obj.toString());
                }).toArray(new Integer[0]);
                debug("configutil", "intarray retrieve at " + pathFromArray(node.getPath()) + " returned " + returned.toString());
                return toPrimitiveArray(returned);
            } catch(NumberFormatException e) {
                throw new ConfigurationException("bad value (\"" + str + "\") for node " + pathFromArray(node.getPath()));
            }
        }
    }
    
    public static String processString(ConfigurationNode node, String defaultt) {
        String str = node.getString(null);
        if(node.isVirtual() || str == null) {
            if(defaultt == null) {
                throw new NoRecordedValueException("value for node " + pathFromArray(node.getPath()) + " not present");
            } else {
                return defaultt;
            }
        }
        
        debug("configutil", "intarray retrieve at " + pathFromArray(node.getPath()) + " returned " + str);
        return str;
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
