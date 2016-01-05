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

import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for any objects that could be implemented by a lua script.
 * 
 * @param <R> the type of object accepted and used by this
 *            {@link LuaImplementable}'s
 *            {@link #load(Object, Globals)} method.
 * @param <T> the type of the resulting {@link LuaImplementation} received from
 *            this LuaImplementable's
 *            {@link #load(Object, Globals)} method.
 */
public interface LuaImplementable<R, T extends LuaImplementation> {
    
    /**
     * Loads a script file into the given {@link LuaImplementation}, basing off
     * of the properties of the given {@link LuaImplementable}.
     * 
     * <p>This method serves as a default implementation of
     * {@link LuaImplementable#load(Object, Globals)}, for the common purpose of
     * loading a script file into an implementation. It may also be used in
     * conjunction with an alternate implementation of load if the object
     * contains both an implementable script and other data. The method will not
     * call the {@link #onLoad(Object, LuaImplementation)} method after it has
     * finished loading the file; it is up to the implementation of the load
     * method to do so.</p>
     * 
     * <p>This method will set the implementable of the base object as the given
     * implementable; the base object's implementable should be replaced if this
     * behaviour is unwanted.</p>
     * 
     * @param implementable the implementable to load with
     * @param baseObject the object to load
     * @param scriptFile the script file to load into <code>baseObject</code>
     * @param globals the globals to load the script with
     *            
     * @return <code>baseObject</code>, with the given script now loaded
     *         
     * @throws FileNotFoundException if the given script file was not found
     * @throws LuaScriptException if the implementation within the script was
     *             marked invalid (bad value or missing required function)
     */
    public static <T extends LuaImplementation> T loadFile(LuaImplementable<?, T> implementable, File scriptFile, Globals globals, T baseObject) throws FileNotFoundException, LuaScriptException {
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

        baseObject.setImplementable(implementable);
        baseObject.setFunctions(setFunctions);
        return baseObject;
    }
    
    /**
     * Returns the required functions for an implementation of this Lua object.
     * 
     * <p>Implementations of {@link #load(Object, Globals)}
     * should query this method to ensure the script being loaded is not missing
     * any functions the system expects the script to have. If the script fails
     * to implement a function listed in the array returned by this method
     * (either by having it not exist or having the value stored under the
     * function's name not be a function value), the script will be rejected and
     * the load method will throw a {@link LuaScriptException}.</p>
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
     * Called to execute action whenever an object has finished processing
     * through this {@link LuaImplementable}'s
     * {@link #load(Object, Globals)} method.
     * 
     * @param loaded the object containing the data used to load the object with
     * @param baseObject the object that had finished loading
     */
    public void onLoad(R loaded, T baseObject);
    
    /**
     * Loads data into a {@link LuaImplementation} generated by this
     * {@link LuaImplementable}.
     * 
     * <p>Implementations of this method should be loading data retrieved from
     * the given object and transforming it into acceptable {@link LuaValue}s to
     * be stored into the given Globals object.</p>
     * 
     * @param loaded the object containing the data to load the object with
     * @param globals the globals object to load with
     *            
     * @return a LuaImplementation with functions registered by the given script
     *         
     * @throws LuaScriptException if the data given is marked invalid for
     *             injection into the target type of implementation object
     */
    public T load(R loaded, Globals globals) throws LuaScriptException;
}
