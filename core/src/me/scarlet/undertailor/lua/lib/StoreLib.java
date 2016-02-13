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

package me.scarlet.undertailor.lua.lib;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.util.LuaUtil;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.json.JSONConfigurationLoader;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

public class StoreLib extends LuaLibrary {
    
    // key/value as per usual, but all values are strings
    // conversion occurs using the first character of the string
    // 
    public static final char BOOLEAN_PREFIX = 'b';
    public static final char INTEGER_PREFIX = 'i';
    public static final char NUMBER_PREFIX = 'n';
    public static final char STRING_PREFIX = 's';
    // 
    // values that're actually extra nodes follow the same rules
    
    public static LuaTable load(ConfigurationNode rootNode) {
        LuaTable table = new LuaTable();
        
        for(Entry<Object, ? extends ConfigurationNode> entry : rootNode.getChildrenMap().entrySet()) {
            String key = entry.getKey().toString();
            ConfigurationNode node = entry.getValue();

            LuaValue tablekey = fromStoreString(key);
            if(node.hasMapChildren()) { // is a table;
                table.set(tablekey, load(node));
            } else if(node.hasListChildren()) { // is an array
                List<String> strList = node.getList((Function<Object, String>) obj -> {
                    return obj.toString();
                });
                
                LuaTable arrayTable = new LuaTable();
                for(int i = 0; i < strList.size(); i++) {
                    arrayTable.set(i, fromStoreString(strList.get(i)));
                }
                
                table.set(tablekey, arrayTable);
            } else { // is a normal value;
                LuaValue tablevalue = fromStoreString(node.getString());
                table.set(tablekey, tablevalue);
            }
        }
        
        return table;
    }
    
    public static ConfigurationNode convert(ConfigurationNode node, LuaTable table, boolean head) {
        if(LuaUtil.isArrayTable(table)) {
            if(head) {
                Undertailor.instance.error("store", "could not convert luatable; array cannot be the first level");
            } else {
                List<String> str = new ArrayList<>();
                
                LuaUtil.iterateTable(table, new Consumer<Varargs>() {
                    private int current = 0;
                    
                    public void accept(Varargs args) {
                        current++;
                        String store = toStoreString(args.arg(2));
                        if(store == null) {
                            Undertailor.instance.warn("store", "dropped value #" + current + " in array table during table->file conversion (key or value unsupported for storage)");
                        } else {
                            str.add(toStoreString(args.arg(2)));
                        }
                    }
                });
                
                node.setValue(str);
            }
        } else {
            LuaUtil.iterateTable(table, args -> {
                String key = toStoreString(args.arg(1));
                LuaValue value = args.arg(2);
                String sValue = toStoreString(value);
                
                if(key == null || (sValue == null && !value.istable())) {
                    Undertailor.instance.warn("store", "dropped key/value pair \"" + (node.getKey() != null ? node.getKey() + "." : "") + (key == null ? args.arg(1).tojstring() : key) + "\" during table->file conversion (key or value unsupported for storage)");
                } else {
                    if(value.istable() && ((LuaTable) table).keyCount() > 0) {
                        convert(node.getNode(key), (LuaTable) value, false);
                    } else {
                        node.getNode(key).setValue(sValue);
                    }
                }
            });
        }
        
        return node;
    }
    
    public static String toStoreString(LuaValue value) {
        if(value.isboolean()) {
            return BOOLEAN_PREFIX + value.tojstring();
        } else if(value.isnumber()) {
            if(value.isint()) {
                return INTEGER_PREFIX + value.tojstring();
            } else {
                return NUMBER_PREFIX + value.tojstring();
            }
        } else if(value.isstring()) { // has to be after number check
            return STRING_PREFIX + value.tojstring();
        }
        
        return null;
    }
    
    public static LuaValue fromStoreString(String str) {
        char type = str.charAt(0);
        String value = str.substring(1);
        switch(type) {
            case BOOLEAN_PREFIX:
                return LuaValue.valueOf(Boolean.parseBoolean(value));
            case INTEGER_PREFIX:
                return LuaValue.valueOf(Integer.parseInt(value));
            case NUMBER_PREFIX:
                return LuaValue.valueOf(Double.parseDouble(value));
            case STRING_PREFIX:
                return LuaValue.valueOf(value);
            default:
                return null;
        }
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new getStore(),
            new saveStore(),
            new setStore(),
            new loadStore()
    };
    
    private static Map<String, LuaTable> tables;
    public static final File STORE_LOCATION;
    public static final String SAVE_FILE_EXT;
    
    static {
        tables = new HashMap<>();
        STORE_LOCATION = new File(Undertailor.ASSETS_DIRECTORY, "save/");
        SAVE_FILE_EXT = ".save";
    }
    
    public StoreLib() {
        super("store", COMPONENTS);
        if(!STORE_LOCATION.exists()) {
            STORE_LOCATION.mkdirs();
        }
    }
    
    static class loadStore extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            String key = args.checkjstring(1);
            LuaTable defaultt = args.opttable(2, null);
            
            File storeFile = new File(STORE_LOCATION, args.checkjstring(1) + SAVE_FILE_EXT);
            LuaTable table = null;
            
            if(storeFile.exists()) {
                JSONConfigurationLoader loader = JSONConfigurationLoader.builder().setFile(storeFile).build();
                try {
                    table = StoreLib.load(loader.load());
                } catch(Exception e) {
                    e.printStackTrace();
                    table = null;
                }
            }
            
            if(table == null && defaultt != null) {
                tables.put(key, defaultt);
            } else {
                tables.put(key, table);
            }
            
            if(tables.get(key) == null) {
                System.out.println("NIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIL");
                return LuaValue.NIL;
            }
            
            return tables.get(key);
        }
    }
    
    static class getStore extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            String key = args.checkjstring(1);
            
            if(tables.containsKey(key)) {
                return tables.get(key);
            } else {
                return LuaValue.NIL;
            }
        }
    }
    
    static class setStore extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            String key = args.checkjstring(1);
            LuaTable table = args.opttable(2, null);
            
            if(table == null) {
                tables.remove(key);
            } else {
                tables.put(key, table);
            }
            
            return tables.get(key);
        }
    }
    
    static class saveStore extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 2);
            
            String key = args.checkjstring(1);
            LuaTable table = args.opttable(2, tables.get(key));
            File file = new File(STORE_LOCATION, key + SAVE_FILE_EXT);
            
            if(file.exists() && (table == null || table.keyCount() == 0)) {
                try {
                    file.delete();
                    return LuaValue.valueOf(true);
                } catch(Exception e) {
                    Undertailor.instance.warn("store", "could not delete store file " + key + ": " + LuaUtil.formatJavaException(e));
                    return LuaValue.valueOf(false);
                }
            } else {
                try {
                    JSONConfigurationLoader loader = JSONConfigurationLoader.builder().setFile(file).build();
                    loader.save(StoreLib.convert(loader.createEmptyNode(ConfigurationOptions.defaults()), table, true));
                    return LuaValue.valueOf(true);
                } catch(Exception e) {
                    Undertailor.instance.warn("store", "could save store file " + key + ": " + LuaUtil.formatJavaException(e));
                    return LuaValue.valueOf(false);
                }
            }
        }
    }
}
