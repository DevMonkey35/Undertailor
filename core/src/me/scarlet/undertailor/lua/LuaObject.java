package me.scarlet.undertailor.lua;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class LuaObject<T> extends LuaTable {
    
    private String typename;
    protected LuaValue metatable;
    public LuaObject(LuaValue metatable, String typename) {
        this.typename = typename;
        this.metatable = metatable;
        this.setmetatable(metatable);
    }
    
    @Override
    public String typename() {
        return typename;
    }
    
    @Override
    public LuaValue getmetatable() {
        return metatable;
    }
    
    @Override
    public LuaValue setmetatable(LuaValue ignore) {
        return super.setmetatable(this.metatable);
    }
    
    @Override
    public int type() {
        return LuaValue.TTABLE;
    }
    
    /**
     * Returns whether or not the given {@link LuaValue}'s object type matches
     * the type of this {@link LuaImplementation}.
     * 
     * @param value the LuaValue to check
     * 
     * @throws LuaError if <code>err</code> is true and the types do not match
     */
    public boolean matchesType(LuaValue value) {
        return this.matchesType(value, false);
    }
    
    /**
     * Returns whether or not the given {@link LuaValue}'s object type matches
     * the type of this {@link LuaImplementation}.
     * 
     * @param value the LuaValue to check
     * @param err whether or not to throw an error if a type mismatch is found
     * 
     * @throws LuaError if <code>err</code> is true and the types do not match
     */
    public boolean matchesType(LuaValue value, boolean err) {
        return this.matchesType(value.typename(), err);
    }
    
    /**
     * Returns whether or not the given typename matches the typename of this
     * {@link LuaImplementation}.
     * 
     * <p>This method works as if:</p>
     * <pre>
     * typename.equals(impl.typename())</pre>
     * <p>where <code>impl</code> is this LuaImplementation.</p>
     * 
     * @param typename the typename to compare against
     * @param err whether or not to throw an error if a type mismatch is found
     * 
     * @throws LuaError if <code>err</code> is true and the types do not match
     */
    public boolean matchesType(String typename, boolean err) {
        if(!typename.equalsIgnoreCase(this.typename())) {
            if(err) {
                throw new LuaError("bad argument: expected " + this.typename() + ", got " + typename);
            }
            
            return false;
        }
        
        return true;
    }
}
