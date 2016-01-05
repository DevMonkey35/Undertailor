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

package me.scarlet.undertailor.lua;

import me.scarlet.undertailor.manager.ScriptManager;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Library class that can be loaded into a {@link Globals} object.
 * 
 * <p>Subclasses of this class must contain a blank constructor in order to be
 * properly used by a {@link ScriptManager}.</p>
 * 
 * <p>If a library requires extra tasks aside from simply registering a table of
 * functions, the {@link #postinit(LuaValue, LuaValue)} method can be overriden
 * to do so.</p>
 * 
 * <pre>
 * public LuaValue call(LuaValue modname, LuaValue env) {
 *     return super.call(modname, env);
 * }
 * </pre>
 */
public class LuaLibrary extends TwoArgFunction implements LuaLibraryComponent {
    
    public static Map<Class<? extends LuaLibrary>, LuaValue> metatables = new HashMap<>();
    
    public static LuaValue asMetatable(LuaLibrary lib) {
        if(!metatables.containsKey(lib.getClass())) {
            if(lib.isShareable()) {
                LuaValue metatable = new LuaTable();
                lib.register(metatable, LuaValue.INDEX.tojstring());
                metatables.put(lib.getClass(), metatable);
                return metatable;
            }
            
            throw new IllegalArgumentException("Cannot create metatable of non-shareable library");
        }
        
        return metatables.get(lib.getClass());
    }
    
    /**
     * Library function to be assigned to an owning {@link LuaLibrary} class for
     * registration.
     * 
     * <p>The name of the function registered with the library is determined by
     * the name of the subclass implementing this class.</p>
     */
    public static abstract class LibraryFunction extends VarArgFunction implements LuaLibraryComponent {
        
        private LuaLibrary lib;
        
        /**
         * Returns the library instance of which this function is assigned to.
         * 
         * <p>{@link LibraryFunction}s will not lock themselves to a single
         * owning {@link LuaLibrary}. It is possible that this method will
         * return an instance different from one it has previously returned if a
         * managing class shares this function instance between multiple
         * libraries.</p>
         */
        public final LuaLibrary getLibraryInstance() {
            return lib;
        }
        
        @Override
        public String getKey() {
            return this.getClass().getSimpleName();
        }
        
        @Override
        public void store(LuaValue env) {
            env.set(this.getKey(), this);
        }
        
        @Override
        public final Varargs invoke(Varargs args) {
            try {
                return execute(args);
            } catch(Exception e) {
                throw e;
            }
        }
        
        public abstract Varargs execute(Varargs args);
    }
    
    private String libName;
    private Set<LuaLibraryComponent> values;
    
    public LuaLibrary(String libName) {
        this.libName = libName;
        this.values = new HashSet<LuaLibraryComponent>();
    }
    
    /**
     * Create a new {@link LuaLibrary}, assigning it preinstantiated library
     * functions or sublibraries.
     * 
     * @param libName the name of the library, used when registering the library
     *            in an environment table
     * @param values the function/sublibrary instances to register the library
     *            with
     */
    public LuaLibrary(String libName, LuaLibraryComponent... values) {
        this.libName = libName;
        this.values = new HashSet<LuaLibraryComponent>();
        for(LuaLibraryComponent value : values) {
            if(value instanceof LibraryFunction) {
                ((LibraryFunction) value).lib =  this;
            }
            
            this.values.add(value);
        }
    }
    
    /**
     * Returns the name of this {@link LuaLibrary}.
     * 
     * <p>This is the name used to register the library within another
     * environment table. If this returns null, then the functions assigned to
     * this library are instead set upon the environment table directly, as
     * opposed to creating a new table storing the functions within the
     * former.</p>
     */
    public String getLibraryName() {
        return libName;
    }
    
    /**
     * Implementation of call method for a two-arg function.
     * 
     * <p>Simply calls {@link #register(LuaValue, String)} with the library name
     * returned by {@link #getLibraryName()}.</p>
     */
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        return register(env, libName);
    }

    @Override
    public String getKey() {
        return this.getLibraryName();
    }

    @Override
    public void store(LuaValue env) {
        this.call(LuaValue.NIL, env);
    }
    
    /**
     * Registers all {@link LuaLibraryComponent}s that were assigned to this
     * {@link LuaLibrary} instance upon instantiation to a new table and returns
     * it, after placing it within the given environment table under the key
     * <code>libName</code>.
     * 
     * <p>If <code>libName</code> is null, functions are set directly on the
     * given environment table and the environment table is returned
     * instead.</p>
     */
    public LuaValue register(LuaValue env, String libName) {
        LuaValue table;
        if(libName == null) {
            table = env;
        } else {
            table = new LuaTable();
            env.set(libName, table);
        }
        
        for(LuaLibraryComponent value : values) {
            value.store(table);
        }
        
        postinit(env, table);
        return table;
    }
    
    /**
     * Called after this library gets initialized; specifically after the
     * effects of {@link #call(LuaValue, LuaValue)}.
     * 
     * <p>It is likely that a library gets initialized multiple times within a
     * single session due to the nature of their implementation alongside
     * {@link Globals} objects; it is advised that if this library is not
     * planned to be shared between multiple instances that its implementation
     * not be dependent on having one sole instance during the entire
     * session.</p>
     * 
     * @param env the environment table of which this library was inserted into
     * @param table the resulting table containing all of this library
     *            functions, can be the same as <code>env</code> if
     *            {@link #getLibraryName()} returned null
     */
    public void postinit(LuaValue env, LuaValue table) {}
    
    /**
     * Returns whether or not this {@link LuaLibrary} can be shared between
     * separate instances of {@link Globals} objects.
     * 
     * <p>If this returns false, the {@link #cleanClone()} method should be
     * implemented in order to create a clone of this {@link LuaLibrary}
     * assuming it holds more than just what's given to the default LuaLibrary
     * constructor.</p>
     */
    public boolean isShareable() { return true; }
    
    /**
     * Creates another {@link LuaLibrary} of this type with no modifications.
     */
    public LuaLibrary cleanClone() { return new LuaLibrary(libName, values.toArray(new LuaLibraryComponent[values.size()])); };
}
