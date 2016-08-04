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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Utility class for {@link EventListener}s to easily catch
 * and handle events.
 */
public class EventHelper {

    private Map<String, Set<Function<Event, Boolean>>> handlers;

    public EventHelper() {
        this.handlers = new HashMap<>();
    }

    /**
     * Registers a new {@link Function} as an event handler.
     * 
     * <p>The Object array passed to the function are the
     * parameters set upon the event, with the first
     * parameter always being whether or not the event had
     * been processed by a previous handler. The function
     * must return whether or not it has done anything in
     * response to the event.</p>
     * 
     * <p>All handlers for an event will be processed
     * regardless of what occurs. If any handler returns
     * true for processing, the system will not pass the
     * event down to the next object in the current
     * chain.</p>
     * 
     * @param eventId the id to listen for
     * @param handler the handler that responds to the event
     */
    public void registerHandler(String eventId, Function<Event, Boolean> handler) {
        Set<Function<Event, Boolean>> handlerSet = this.handlers.get(eventId);

        if (handlerSet == null) {
            handlerSet = new HashSet<>();
            this.handlers.put(eventId, handlerSet);
        }

        handlerSet.add(handler);
    }

    /**
     * Processes the given {@link Event} with the provided
     * parameters, and returns whether or not the event had
     * been processed by any handlers.
     * 
     * <p>If this method returns true, the event should not
     * progress further down the current chain.</p>
     * 
     * @param event the Event to process
     * 
     * @return if the event should not be passed down the
     *         current chain
     */
    public boolean processEvent(Event event) {
        if (this.handlers.containsKey(event.getId())) {
            Set<Function<Event, Boolean>> handlers = this.handlers.get(event.getId());
            for (Function<Event, Boolean> handler : handlers) {
                if (handler.apply(event)) {
                    event.processed = true;
                }
            }
        }

        return event.isProcessed();
    }
}
