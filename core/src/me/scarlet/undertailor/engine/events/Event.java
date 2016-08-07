/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without
 * limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package me.scarlet.undertailor.engine.events;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.engine.EnvironmentManager;
import me.scarlet.undertailor.engine.Modular;
import me.scarlet.undertailor.engine.overworld.OverworldController;
import me.scarlet.undertailor.engine.overworld.WorldObject;
import me.scarlet.undertailor.engine.overworld.WorldRoom;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;

/**
 * Databag class for events being processed.
 */
public class Event {

    /**
     * Called by an {@link OverworldController} on itself
     * after successfully changing rooms.
     */
    public static final String EVT_ROOMCHANGE = "onRoomChange";
    /**
     * Called by {@link WorldObject}s on themselves after
     * persisting through rooms.
     */
    public static final String EVT_PERSIST = "onPersist";
    /**
     * Called by {@link Modular} objects when they get
     * claimed by a parent.
     */
    public static final String EVT_CLAIM = "onClaim";
    /**
     * Called by a {@link WorldRoom} on itself after loading
     * (right after its create function).
     */
    public static final String EVT_LOAD = "onLoad";
    /**
     * Called by a {@link WorldObject} on itself when it
     * collides with another object.
     */
    public static final String EVT_STARTCOLLIDE = "onCollide";
    /**
     * Called by a {@link WorldObject} on itself when it
     * stops colliding with another object.
     */
    public static final String EVT_STOPCOLLIDE = "onStopCollide";
    /**
     * Called by the system when the window focus changes.
     */
    public static final String EVT_FOCUSCHANGED = "focusChanged";

    EventListener source;
    boolean processed;
    private String id;
    private Object[] params;

    // lua variant
    private LuaTable table;

    /**
     * Converts the provided {@link Varargs} parameters into
     * an {@link Event} instance.
     * 
     * <p>Varargs is to contain the event ID as the first
     * parameter, and any parameters relevant to the event
     * following the former.</p>
     * 
     * @param args the Varargs instance to convert
     * 
     * @return the Event resulting from the provided Varargs
     */
    public static Event asLuaEvent(Varargs args) {
        String id = args.checkjstring(1);
        Object[] params = LuaUtil.asJavaVargs(args.subargs(2));
        return new Event(id, params);
    }

    public Event(String id, Object... params) {
        this.id = id;
        this.params = params;
        this.processed = false;

        this.table = new LuaTable();
        this.table.set(1, this.id);
        this.table.set(2, LuaValue.NIL);
        this.table.set(3, LuaValue.valueOf(this.processed));
        Varargs luaParams = LuaUtil.varargsOf(this.params);
        for (int i = 0; i < this.params.length; i++) {
            this.table.set(4 + i, luaParams.arg(i + 1));
        }
    }

    /**
     * Returns the type ID of this {@link Event}.
     * 
     * @return this Event's type ID
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns whether or not this {@link Event} was
     * previously processed by another event handler.
     * 
     * @return if this Event was previously processed
     */
    public boolean isProcessed() {
        return this.processed;
    }

    /**
     * Returns the parameters instantiated with this
     * {@link Event} as an Object array.
     * 
     * @return this event's parameters
     */
    public Object[] getParameters() {
        return this.params;
    }

    /**
     * Returns the Lua version of this {@link Event}
     * instance.
     * 
     * <p>An event instance is an array table, containing
     * the following:</p> <ul> <li>the type ID of this
     * Event</li> <li>the source of the event</li>
     * <li>whether this event had been processed</li>
     * <li>the parameters of the event</li> </ul>
     * 
     * @return the Lua version of this Event
     */
    public LuaTable asLua() {
        this.table.set(2,
            source instanceof EnvironmentManager ? LuaValue.NIL : LuaObjectValue.of(source));
        this.table.set(3, LuaValue.valueOf(this.processed));
        return this.table;
    }
}
