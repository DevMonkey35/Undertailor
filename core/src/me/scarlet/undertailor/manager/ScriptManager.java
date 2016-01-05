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

package me.scarlet.undertailor.manager;

import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.LuaImplementable;
import me.scarlet.undertailor.lua.LuaImplementation;
import me.scarlet.undertailor.lua.LuaLibrary;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.DebugLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseOsLib;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ScriptManager {
    
    private static LuaValue[] SHARED_BASE_LIBS;
    
    static {
        SHARED_BASE_LIBS = new LuaValue[] {
                new Bit32Lib(),
                new TableLib(),
                new StringLib(),
                new JseMathLib(),
                new JseOsLib()
        };
    }
    
    private Map<Class<? extends LuaLibrary>, LuaValue> metatables;
    private Map<Class<? extends LuaLibrary>, LuaLibrary> libraries;
    private Map<Class<? extends LuaImplementable<?, ?>>, LuaImplementable<?, ?>> implementables;
    
    public ScriptManager() {
        this.libraries = new HashMap<>();
        this.metatables = new HashMap<>();
        this.implementables = new HashMap<>();
    }
    
    /**
     * Registers the given LuaLibraries ({@link LuaLibrary}) into this
     * {@link ScriptManager}.
     * 
     * <p>A ScriptManager will not hold duplicates of the same type of library,
     * therefore giving this method types of libraries of which has already been
     * registered previously will have the previous library replaced.</p>
     * 
     * @param libraries the LuaLibraries to register
     */
    @SafeVarargs
    public final void registerLibraries(LuaLibrary... libraries) {
        for(LuaLibrary library : libraries) {
            this.libraries.put(library.getClass(), library);
        }
    }
    
    /**
     * Registers the given {@link LuaImplementable}s into this
     * {@link ScriptManager}.
     * 
     * <p>A ScriptManager will not hold duplicates of the same type of library,
     * therefore giving this method types of libraries of which has already been
     * registered previously will have the previous library replaced.</p>
     * 
     * @param implementables the LuaImplementables to register
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final void registerImplementables(LuaImplementable<?, ?>... implementables) {
        for(LuaImplementable<?, ?> impl : implementables) {
            this.implementables.put((Class<? extends LuaImplementable<?, ?>>) impl.getClass(), impl);
        }
    }
    
    /**
     * Returns a metatable version of the given {@link LuaLibrary} type.
     * 
     * <p>This method will only generate metatables of LuaLibraries that have
     * been registered within this {@link ScriptManager}. Typical nature of
     * metatables prevents non-shareable libraries
     * {@link LuaLibrary#isShareable()} from being registered as metatables
     * through the way this method performs.</p>
     * 
     * @param clazz the type of the registered LuaLibrary to generate a
     *            metatable of
     */
    public LuaValue getLibraryMetatable(Class<? extends LuaLibrary> clazz) {
        LuaValue returned = metatables.get(clazz);
        if(returned == null && libraries.containsKey(clazz)) {
            LuaLibrary lib = libraries.get(clazz);
            if(lib.isShareable()) {
                metatables.put(clazz, returned = LuaLibrary.asMetatable(lib));
            } else {
                throw new IllegalArgumentException("cannot retrieve metatable of non-shareable library");
            }
        }
        
        return returned;
    }
    
    /**
     * Generates a new {@link Globals} instance, with libraries determined by
     * the properties of this {@link ScriptManager}.
     * 
     * @see #generateGlobals(boolean)
     */
    public Globals generateGlobals() {
        return generateGlobals(true);
    }
    
    /**
     * Generates a new {@link Globals} instance, with libraries determined by
     * the properties of this {@link ScriptManager}.
     * 
     * <p>Base Lua libraries are always loaded. These include:</p> <ul>
     * <li>JseBaseLib</li> <li>PackageLib</li> <li>DebugLib</li>
     * <li>Bit32Lib</li> <li>TableLib</li> <li>StringLib</li>
     * <li>JseMathLib</li> <li>JseOsLib</li></ul>
     * 
     * <p>After base libraries are loaded, extra libraries registered into this
     * {@link ScriptManager} are loaded, if <code>libs</code> is true.</p>
     * 
     * @param libs whether or not to include registered libraries
     */
    public Globals generateGlobals(boolean libs) {
        Globals returned = new Globals();
        returned.load(new JseBaseLib());
        returned.load(new PackageLib());
        returned.load(new DebugLib());
        
        for(LuaValue lib : SHARED_BASE_LIBS) {
            returned.load(lib);
        }
        
        returned.set("debug", LuaValue.NIL);
        LoadState.install(returned);
        LuaC.install(returned);
        
        if(libs) {
            for(LuaLibrary lib : libraries.values()) {
                if(lib.isShareable()) {
                    returned.load(lib);
                } else {
                    returned.load(lib.cleanClone());
                }
            }
        }
        
        return returned;
    }
    
    /**
     * Returns the {@link LuaImplementable} found by the given type, or null if
     * not found.
     * 
     * <p>LuaImplementables may be registered into this {@link ScriptManager} by
     * the use of the {@link #registerImplementables(LuaImplementable...)}
     * method.</p>
     * 
     * @param clazz the type to search for
     */
    @SuppressWarnings("unchecked")
    public <T extends LuaImplementable<?, ?>> T getImplementable(Class<T> clazz) {
        for(Entry<Class<? extends LuaImplementable<?, ?>>, LuaImplementable<?, ?>> entry : implementables.entrySet()) {
            if(entry.getKey().equals(clazz)) {
                return (T) entry.getValue(); // should be fine; registration assures class keys match their value's class
            }
        }
        
        return null;
    }
    
    /**
     * A quick shortcut method for accessing a registered
     * {@link LuaImplementable} to generate an implementation.
     * 
     * <p>This method executes as if:</p>
     * 
     * <pre>
     * ScriptManager.getImplementable(type).load(loaded, ScriptManager.{@link #generateGlobals()});
     * </pre>
     * 
     * @param type the type of the implementable to generate with
     * @param loaded the object to load from
     *        
     * @return a {@link LuaImplementation} generated from the requested
     *         implementable type
     *         
     * @throws LuaScriptException
     */
    @SuppressWarnings("unchecked")
    public <R, T extends LuaImplementation> T generateImplementation(Class<? extends LuaImplementable<R, T>> type, R loaded) throws LuaScriptException {
        return ((LuaImplementable<R, T>) this.implementables.get(type)).load(loaded, generateGlobals(true));
    }
}
