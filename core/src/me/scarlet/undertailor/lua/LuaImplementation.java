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

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;

import java.util.Map;

/**
 * An implementation of a lua script file.
 * 
 * <p>This class is meant to be sent through an implementation of
 * {@link LuaImplementable} before being used, so as to set its creator variable
 * ({@link #getImplementable()}). Attempting to use instances of this class and
 * its subclasses will likely result in errors due to the absence of internal
 * variables set by a LuaImplementable.</p>
 */
public interface LuaImplementation {
    
    /**
     * Returns the {@link LuaImplementable} instance that this
     * {@link LuaImplementation} was processed through.
     * 
     * <p>This method returning null indicates that this LuaImplementation has
     * yet to be sent through a LuaImplementable instance to be given a script
     * implementation. Errors will occur during usage of this LuaImplementation
     * if attempts to use it while this method returns null.</p>
     */
    LuaImplementable<?, ? extends LuaImplementation> getImplementable();
    
    /**
     * Sets the parent {@link LuaImplementable} for this implementation.
     * 
     * <p>This method solely exists to let a LuaImplementable set itself as the
     * parent implementable upon this implementation. It is not advised to
     * change the parent implementation due to the likely expectations of the
     * implementing class's functions.</p>
     * 
     * @param impl the parent implementable to set
     */
    void setImplementable(LuaImplementable<?, ? extends LuaImplementation> impl);
    
    /**
     * Returns a read-only mapping of functions currently assigned to this
     * {@link LuaImplementation}.
     * 
     * <p>This will only return functions of the names included within the
     * {@link LuaImplementable} ({@link LuaImplementable#getFunctions()}) that
     * loaded this implementation, assuming they existed within the script
     * loaded by the former. It is expected that a default
     * {@link LuaImplementable} will always make sure that functions specified
     * by {@link LuaImplementable#getRequiredFunctions()} are present upon
     * instantiation. It is up to whatever holds the implementation to ensure
     * those functions stay present, should they choose to let them be.</p>
     */
    Map<String, LuaFunction> getFunctions();
    
    /**
     * Sets the function table for this implementation.
     * 
     * <p>This replaces all functions within this implementation with the given
     * map of functions. While it can be used for other purposes, this method is
     * intended to be used by the
     * {@link LuaImplementable#loadFunctions(String, Object, Globals, boolean)}
     * method to load a script's functions into an implementation.</p>
     * 
     * @param functions
     */
    void setFunctions(Map<String, LuaFunction> functions);
    
    /**
     * Returns the object value that contains this {@link LuaImplementation}.
     */
    LuaObjectValue<?> getObjectValue();
    
    /**
     * Sets the object value that contains this {@link LuaImplementation}.
     * 
     * <p>Upon being set upon this LuaImplementation, the implementation is
     * expected to set its default functions (denoted by the
     * {@link LuaImplementable#getFunctions()} method of its parent
     * implementable) upon the object value. This means any values within the
     * object value of the same name will be overwritten. Changing these values
     * will not be prohibited by the implementation.</p>
     * 
     * @param obj the object value to set
     */
    void setObjectValue(LuaObjectValue<?> obj);
    
    /**
     * Changes a function within this implementation.
     * 
     * <p>This method simply queries the method
     * {@link LuaImplementable#onFunctionChange(LuaImplementation, String, org.luaj.vm2.LuaValue)}
     * , then changes the method with {@link #setFunction(String, LuaFunction)}
     * (if the queried method returns true). A NullPointerException will be
     * thrown if {@link #getImplementable()} returns null.</p>
     * 
     * <p>It is possible to remove functions denoted required by this
     * implementation's parent {@link LuaImplementable}, though one should not
     * do so unless the system utilizing the implementation expects the
     * possibility of a missing required function.</p>
     * 
     * @param name the name of the function to change
     * @param function the new function, or null to remove
     
    public default void changeFunction(String name, LuaFunction function) {
        if(this.getImplementable().onFunctionChange(this, name, function)) {
            this.setFunction(name, function);
        }
    }
    
    /**
     * Replaces a function within this implementation.
     * 
     * <p>This method will not trigger the change method. As with the
     * {@link #changeFunction(String, LuaFunction)} method, it is not advised to
     * replace a required function unless the system expects it.</p>
     * 
     * @param name the name of the function to replace
     * @param function the new function, or null to remove
     
    public void setFunction(String name, LuaFunction function);*/
}
