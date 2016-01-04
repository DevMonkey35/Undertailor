package me.scarlet.undertailor.lua;

import org.luaj.vm2.LuaValue;

public interface LuaLibraryComponent {
    
    /**
     * Returns the key this {@link LuaLibraryComponent} would be stored under
     * should its {@link #store(LuaValue)} method be called.
     */
    public String getKey();
    
    /**
     * Stores the equivalent {@link LuaValue} of this
     * {@link LuaLibraryComponent} within the given environment table, under the
     * key returned by {@link #getKey()}.
     * 
     * @param env the environment table to store this value in
     */
    public void store(LuaValue env);
}
