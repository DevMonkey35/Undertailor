package me.scarlet.undertailor.util;

import java.util.Objects;
import java.util.Optional;

public class Pair<T1, T2> {
    
    private T1 t1;
    private T2 t2;
    
    public Pair() {
        this(null, null);
    }
    
    public Pair(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }
    
    public Optional<T1> getFirstElement() {
        return Optional.ofNullable(t1);
    }
    
    public void setFirstElement(T1 t1) {
        this.t1 = t1;
    }
    
    public Optional<T2> getSecondElement() {
        return Optional.ofNullable(t2);
    }
    
    public void setSecondElement(T2 t2) {
        this.t2 = t2;
    }
    
    public boolean isEmpty() {
        return t1 == null && t2 == null;
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object o) {
        if(o instanceof Pair) {
            Pair other = (Pair) o;
            return t1.equals(other.t1) && t2.equals(other.t2);
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(t1, t2);
    }
}
