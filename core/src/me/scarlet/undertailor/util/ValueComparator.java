package me.scarlet.undertailor.util;

import java.util.Comparator;
import java.util.Map;

public abstract class ValueComparator<T1, T2> implements Comparator<T1> {
    
    private Map<T1, T2> map;
    public ValueComparator(Map<T1, T2> map) {
        this.map = map;
    }
    
    public Map<T1, T2> getMap() {
        return map;
    }
    
    public abstract int compare(T1 o1, T1 o2);
}
