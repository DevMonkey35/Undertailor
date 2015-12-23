package me.scarlet.undertailor.nlua;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

/**
 * Base class for any objects that could be implemented by a lua script.
 */
public abstract class LuaImplementable {
    
    /**
     * Returns the typename of this implemented Lua object.
     */
    public abstract String getTypeName();
    
    /**
     * Returns the required functions for an implementation of this Lua object.
     * 
     * <p>A script loader will query this method to ensure the script being
     * loaded is not missing any functions the system expects the script to
     * have. If the script fails to implement a function listed in the array
     * returned by this method (either by having it not exist or having the
     * value stored under the function's name not be a function value), the
     * script will be rejected.</p>
     */
    public abstract String[] getRequiredFunctions();
    
    /**
     * Returns the list of functions recognized and used by anything utilizing
     * an implementation of this Lua object.
     * 
     * <p>This should include the methods returned by
     * {@link #getRequiredFunctions()}.</p>
     */
    public abstract String[] getFunctions();
    
    /**
     * Called to execute action whenever the implementation of a listed function
     * within a Lua object of this type changes.
     * 
     * <p>Note "listed function," meaning that this is only called for method
     * names that are listed by {@link #getFunctions()}.</p>
     * 
     * @param impl the implementation object whose function was changed
     * @param funcName the name of the changed function
     * @param newValue the new function to be set; can be nil
     */
    public abstract void onFunctionChange(LuaImplementation impl, String funcName, LuaValue newValue);
    
    public boolean matches(LuaValue value) {
        return this.matches(value, false);
    }
    
    public boolean matches(LuaValue value, boolean err) {
        return this.matches(value.typename(), err);
    }
    
    public boolean matches(String typename, boolean err) {
        if(!typename.equalsIgnoreCase(this.getTypeName())) {
            if(err) {
                throw new LuaError("bad argument: expected " + this.getTypeName() + ", got " + typename);
            }
            
            return false;
        }
        
        return true;
    }
}
