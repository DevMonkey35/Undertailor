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
        
        //System.out.println("returned a last entry " + last);
        return last;
    }
    
    public static <T1, T2> Entry<T1, T2> getByIndex(Map<T1, T2> map, int index) {
        Iterator<Entry<T1, T2>> iterator = map.entrySet().iterator();
        int currentIndex = 0;
        while(iterator.hasNext()) {
            Entry<T1, T2> entry = iterator.next();
            if(currentIndex == index) {
                return entry;
            }
            
            currentIndex++;
        }
        
        return null;
    }
}
