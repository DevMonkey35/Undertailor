package me.scarlet.undertailor.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MapUtil {
    
    public static <T1, T2> Entry<T1, T2> getLastEntry(Map<T1, T2> map) {
        Iterator<Entry<T1, T2>> iterator = map.entrySet().iterator(); 
        Entry<T1, T2> last = null;
        while(iterator.hasNext()) {
            last = iterator.next();
        }
        
        return last;
    }
}
