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

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Generic container for Java objects to be passable as Lua objects.
 * 
 * <p>If the given type is an instance of {@link LuaImplementation}, this object
 * will automagically register the implementation's functions into the script as
 * well as call its
 * {@link LuaImplementable#onFunctionChange(LuaImplementation, String, LuaValue)}
 * should any recognized functions be changed.</p>
 * 
 * @param <T> the type of the object to contain
 */
public class LuaObjectValue<T> extends LuaTable {
    
    private static Map<Object, LuaObjectValue<?>> objects = new WeakHashMap<>();
    
    /**
     * Instantiates a new {@link LuaObjectValue} containing the given object,
     * the specified Lua object typename, and a given metatable.
     * 
     * @param object the object to contain
     * @param typename the typename for Lua to recognize this object as
     */
    public static <T> LuaObjectValue<T> of(T object, String typename) {
        return of(object, typename, null);
    }

    /**
     * Instantiates a new {@link LuaObjectValue} containing the given object,
     * the specified Lua object typename, and a given metatable.
     * 
     * <p>The metatable should already be in {INDEX, TABLE} form.</p>
     * 
     * <p>Note that once this method is called, it will store the generated
     * LuaObjectValue to ensure new instances containing the same object will
     * not be made. This means the typename and metatable will not be changeable
     * through this method alone, since this method will return a stored object
     * value for the given object if found when this method was first called for
     * said object. The object may be destroyed completely from memory through
     * the use of {@link #destroyObjectValue(Object)}, allowing a new instance
     * to be made and stored through this method.</p>
     * 
     * @param object the object to contain
     * @param typename the typename for Lua to recognize this object as
     * @param metatable the metatable to assign to this object, or null to not
     *            assign one
     */
    @SuppressWarnings("unchecked")
    public static <T> LuaObjectValue<T> of(T object, String typename, LuaValue metatable) {
        if(!objects.containsKey(object)) {
            objects.put(object, new LuaObjectValue<T>(object, typename, metatable));
        }
        
        return (LuaObjectValue<T>) objects.get(object);
    }
    
    /**
     * Destroys a cached object value for the given object.
     * 
     * <p>Destroying the object value will remove all data stored within the
     * object value that may have been set by a Lua script that had accessed it.
     * Be wary of script errors that may arise from scripters who expect values
     * within an object value to be present when they were erased by this
     * method.</p>
     * 
     * @param object the target stored object of the target
     *            {@link LuaObjectValue}
     */
    public static void destroyObjectValue(Object object) {
        objects.remove(object);
    }
    
    private T object;
    private String typename;
    private LuaValue metatable;
    
    LuaObjectValue(T object, String typename, LuaValue metatable) {
        if(object instanceof LuaImplementation) {
            LuaImplementation impl = (LuaImplementation) object;
            impl.setObjectValue(this);
            if(impl.getFunctions() != null) {
                for(String key : impl.getFunctions().keySet()) {
                    this.set(key, impl.getFunctions().get(key));
                }
            }
        }
        
        this.object = object;
        this.typename = typename;
        this.metatable = metatable;
        this.setmetatable(metatable);
    }
    
    /**
     * Returns the object contained by this {@link LuaObjectValue}.
     */
    public T getObject() {
        return object;
    }
    
    /**
     * Overrides the default rawset method in order to detect changes to visible
     * properties of the contained script.
     */
    @Override
    public void rawset(LuaValue key, LuaValue value) {
        if(key.isstring() && !value.isnil() && this.object instanceof LuaImplementation) {
            LuaImplementation impl = (LuaImplementation) object;
            for(String func : impl.getImplementable().getFunctions()) {
                if(key.tojstring().equals(func)) {
                    if(value.isfunction()) {
                        impl.getImplementable().onFunctionChange(impl, key.tojstring(), (LuaFunction) value);
                        break;
                    } else { // can't be nil here so it has to be anything but a func
                        throw new LuaError("cannot change variable " + func + " to contain a non-functional value (implemented script function)");
                    }
                }
            }
        }
        
        super.rawset(key, value);
    }

    @Override
    public int type() {
        return LuaValue.TTABLE;
    }

    @Override
    public String typename() {
        return typename;
    }
    
    @Override
    public LuaValue setmetatable(LuaValue ignored) {
        return super.setmetatable(this.metatable);
    }
}
