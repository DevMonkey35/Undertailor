package me.scarlet.undertailor.engine;

/**
 * Skeleton implementation for objects that refuse to
 * perform their tasks until their resources, of which may
 * take an extended period of time, completely load.
 */
public interface PotentialDelay {

    /**
     * Pokes this {@link PotentialDelay} and returns whether
     * or not it is ready to proceed.
     * 
     * @return if this PotentialDelay is ready
     */ // poke it to death
    boolean poke();

}
