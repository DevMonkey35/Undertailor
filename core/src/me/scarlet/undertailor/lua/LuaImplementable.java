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

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.manager.ScriptManager;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for any objects that could be implemented by a lua script.
 * 
 * @param <R> the type of object accepted and used by this
 *            {@link LuaImplementable}'s
 *            {@link #loadFunctions(String, Object, Globals, boolean)} method.
 * @param <T> the type of the resulting {@link LuaImplementation} received from
 *            this LuaImplementable's {@link #load(String, LuaValue...)} method.
 */
public interface LuaImplementable<R, T extends LuaImplementation> {
    
    /**
     * Loads a script file into the given {@link LuaImplementation}, basing off
     * of the properties of the given {@link LuaImplementable}.
     * 
     * <p>This method serves as a default implementation of
     * {@link LuaImplementable#loadFunctions(String, Object, Globals, boolean)},
     * for the common purpose of loading a script file into an implementation.
     * It may also be used in conjunction with an alternate implementation of
     * load if the object contains both an implementable script and other
     * data.</p>
     * 
     * <p>This method will set the implementable of the base object as the given
     * implementable; the base object's implementable should be replaced if this
     * behaviour is unwanted.</p>
     * 
     * @param implementable the implementable to load with
     * @param scriptFile the script file to load into <code>baseObject</code>
     * @param globals the globals to load the script with
     *            
     * @return <code>baseObject</code>, with the given script now loaded
     *         
     * @throws FileNotFoundException if the given script file was not found
     * @throws LuaScriptException if the implementation within the script was
     *             marked invalid (bad value or missing required function)
     */
    public static Map<String, LuaFunction> loadFile(LuaImplementable<?, ?> implementable, File scriptFile, Globals globals) throws FileNotFoundException, LuaScriptException {
        Map<String, LuaFunction> functions = LuaUtil.getScriptFunctions(globals, scriptFile);
        Map<String, LuaFunction> setFunctions = new HashMap<>();
        
        for(String func : implementable.getFunctions()) { // verify all variables registered are actually functions
            if(functions.containsKey(func)) {
                LuaValue value = functions.get(func);
                if(!value.isnil() && !value.isfunction()) {
                    throw new LuaScriptException("bad value: expected function in variable \"" + func + "\", got " + value.typename());
                } else {
                    setFunctions.put(func, (LuaFunction) value);
                }
            }
        }
        
        for(String func : implementable.getRequiredFunctions()) {
            if(!setFunctions.containsKey(func)) {
                throw new LuaScriptException("script implementation is missing required function \"" + func + "\"");
            }
        }
        
        return setFunctions;
    }
    
    /**
     * Returns the required functions for an implementation of this Lua object.
     * 
     * <p>Implementations of
     * {@link #loadFunctions(String, Object, Globals, boolean)} should query
     * this method to ensure the script being loaded is not missing any
     * functions the system expects the script to have. If the script fails to
     * implement a function listed in the array returned by this method (either
     * by having it not exist or having the value stored under the function's
     * name not be a function value), the script will be rejected and the load
     * method will throw a {@link LuaScriptException}.</p>
     */
    public String[] getRequiredFunctions();
    
    /**
     * Returns the list of functions recognized and used by anything utilizing
     * an implementation of this Lua object.
     * 
     * <p>This should include the functions returned by
     * {@link #getRequiredFunctions()}.</p>
     */
    public String[] getFunctions();
    
    /**
     * Called to execute action whenever the implementation of a listed function
     * within a Lua object of this type changes. Functions will be prevented
     * from changing if this method returns false for the given case. It is
     * recommended to throw a {@link LuaError} should such occur to inform the
     * scripter that the function had failed to change.
     * 
     * <p>This method being called does not mean the actual original function is
     * being changed, it purely means that the object holding the implementation
     * is undergoing changes involving the variable holding the original
     * function.</p>
     * 
     * <p>Note "listed function," meaning that this is only called for method
     * names that are listed by {@link #getFunctions()}.</p>
     * 
     * <p>If the parent implementable of the given implementation was changed
     * through {@link LuaImplementation#setImplementable(LuaImplementable)}, the
     * implementation may be of a different type than the implementing class's
     * type parameter.</p>
     * 
     * <p>The default implementation of this method returns true for all
     * cases.</p>
     * 
     * @param impl the implementation object whose function was changed
     * @param funcName the name of the changed function
     * @param newValue the new function to be set; can be nil
     */
    public default boolean onFunctionChange(LuaImplementation impl, String funcName, LuaValue newValue) {
        return true;
    }
    
    /**
     * Loads the given <code>scriptFile</code> into the implementable for later
     * assignment inside a {@link LuaImplementation}.
     * 
     * <p>Shortcut method for calling the overloaded variant of this method
     * using default globals generated from the current runtime's
     * {@link ScriptManager}, passing <code>false</code> for the replacement
     * argument preventing replacement of already loaded functions.</p>
     * 
     * @param scriptId the ID to assign the loaded functions
     * @param loaded the object to load data and functions from
     *            
     * @throws LuaScriptException if an error occurs loading the functions from
     *             the given load data
     */
    public default void loadFunctions(String scriptId, R loaded) throws LuaScriptException {
        this.loadFunctions(scriptId, loaded, Undertailor.getScriptManager().generateGlobals(), false);
    }
    
    /**
     * Loads the given <code>scriptFile</code> into the implementable for later
     * assignment inside a {@link LuaImplementation}.
     * 
     * <p>Script files are to be fed to this method in order to use them for
     * generating the target implementation type of this
     * {@link LuaImplementable}. Calling {@link #load(String, LuaValue...)} with
     * an ID fed to this method will generate a new {@link LuaImplementation}
     * containing the functions assigned to said ID, loaded from the given
     * <code>scriptFile</code>.</p>
     * 
     * @param scriptId the ID to assign the loaded functions
     * @param loaded the object to load data and functions from
     * @param globals the Globals object to load functions with
     * @param replace whether or not to replace the current loaded set
     *            
     * @throws LuaScriptException if an error occurs loading the functions from
     *             the given load data
     */
    public void loadFunctions(String scriptId, R loaded, Globals globals, boolean replace) throws LuaScriptException;
    
    /**
     * Loads function data tagged with the given ID into a
     * {@link LuaImplementation} generated by this {@link LuaImplementable}.
     * 
     * @param scriptId the ID of the functions to assign the generated
     *            implementation
     * 
     * @return a LuaImplementation with functions registered by the given load
     *         data
     *         
     * @throws LuaScriptException if the data given is marked invalid for
     *             injection into the target type of implementation object
     */
    public default T load(String scriptId, LuaValue... args) throws LuaScriptException {
        return load(scriptId, LuaUtil.asVarargs(args));
    };
    
    public T load(String scriptId, Varargs args) throws LuaScriptException;
    
    /**
     * Loads function data tagged with the given ID into a
     * {@link LuaImplementation} generated by this {@link LuaImplementable} if
     * it already exists. If not, the given object is loaded into the generated
     * LuaImplementation, and the loaded function data is stored with the given
     * ID.
     * 
     * <p>This method functions as if:</p>
     * 
     * <pre>
     * loadFunctions(scriptId, loaded)
     * return load(scriptId);
     * </pre>
     * 
     * @param scriptId the ID of the script to load, or to assign data to if it
     *            has no stored data
     * @param loaded the object containing data to load if no data exists under
     *            the given id
     *            
     * @return a LuaImplementation with functions registered by the given load
     *         data
     * 
     * @throws LuaScriptException if the data given is marked invalid for
     *             injection into the target type of implementation object
     */
    public default T load(String scriptId, R loaded, LuaValue... args) throws LuaScriptException {
        return load(scriptId, loaded, LuaUtil.asVarargs(args));
    }
    
    public default T load(String scriptId, R loaded, Varargs args) throws LuaScriptException {
        this.loadFunctions(scriptId, loaded);
        return load(scriptId, args);
    }
}
