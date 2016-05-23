package me.scarlet.undertailor.util;

/**
 * Class holding a pair of two objects of the same type.
 *
 * @param <T> the type of object to hold two of
 */
public class Pair<T> {

    private T item1;
    private T item2;

    public Pair() {
        this(null, null);
    }

    public Pair(T item1, T item2) {
        this.item1 = item1;
        this.item2 = item2;
    }
    
    /**
     * Returns the first item stored by this {@link Pair}.
     * 
     * @return this Pair's first item
     */
    public T getFirst() {
        return this.item1;
    }
    
    public void setFirst(T first) {
        this.item1 = first;
    }

    /**
     * Returns the second item stored by this {@link Pair}.
     * 
     * @return this Pair's second item
     */
    public T getSecond() {
        return this.item2;
    }
    
    public void setSecond(T second) {
        this.item2 = second;
    }
    
    public void setItems(T item1, T item2) {
        this.item1 = item1;
        this.item2 = item2;
    }
    
    public void clearItems() {
        this.item1 = null;
        this.item2 = null;
    }
}
