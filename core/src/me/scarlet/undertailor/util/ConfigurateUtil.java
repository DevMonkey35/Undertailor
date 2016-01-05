/* 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor.util;

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
            return toPrimitiveArray(processIntegerArray(node, defaultt));
        }
    }
    
    public static Integer[] processIntegerArray(ConfigurationNode node, Integer[] defaultt) {
        if(node.isVirtual()) {
            if(defaultt == null) {
                throw new NoRecordedValueException("value for node " + pathFromArray(node.getPath()) + " not present");
            } else {
                return defaultt;
            }
        } else {
            String str = node.getString();
            try {
                String[] stringList = node.getList(obj -> {
                    return obj.toString();
                }).toArray(new String[0]);
                
                Integer[] returned = new Integer[stringList.length];
                for(int i = 0; i < stringList.length; i++) {
                    returned[i] = stringList[i].equals("-") ? null : Integer.parseInt(stringList[i]);
                }
                
                return returned;
            } catch(NumberFormatException e) {
                throw new ConfigurationException("bad value (\"" + str == null ? "null" : str + "\") for node " + pathFromArray(node.getPath()));
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
        
        return str;
    }
    
    public static String[] processStringArray(ConfigurationNode node, String[] defaultt) {
        if(node.isVirtual()) {
            if(defaultt == null) {
                throw new NoRecordedValueException("value for node " + pathFromArray(node.getPath()) + " not present");
            } else {
                return defaultt;
            }
        } else {
            String[] stringList = node.getList(obj -> {
                return obj.toString();
            }).toArray(new String[0]);
            return stringList;
        }
    }
    
    public static boolean processBoolean(ConfigurationNode node, Boolean defaultt) {
        if(node.isVirtual()) {
            if(defaultt == null) {
                throw new NoRecordedValueException("value for node " + pathFromArray(node.getPath()) + " not present");
            } else {
                return defaultt;
            }
        } else {
            String bool = node.getString(null);
            if(bool == null) {
                throw new NoRecordedValueException("value for node " + pathFromArray(node.getPath()) + " not present");
            } else {
                return Boolean.valueOf(bool);
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
            returned[i] = array[i] == null ? 0 : array[i];
        }
        
        return returned;
    }
}
