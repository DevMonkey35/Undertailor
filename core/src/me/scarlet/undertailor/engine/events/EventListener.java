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

/**
 * Skeleton implementation for classes that are able to
 * receive and process events.
 */
public interface EventListener {

    /**
     * Returns the {@link EventHelper} managing and
     * processing events for this {@link EventListener}.
     * 
     * @return this EventListener's helper
     */
    EventHelper getEventHelper();

    /**
     * Processes the provided {@link Event}.
     * 
     * <p>This is the primary method for anything that
     * supports event handling. It is in this method where
     * an {@link EventListener} processes the given event
     * and then decides whether or not it should be passed
     * down its event chain. The return value presents
     * whether or not the provided Event was ever processed
     * by any event handler, including the ones farther down
     * this listener's chain.</p>
     * 
     * @param event the Event to process
     * 
     * @return if the event was ever processed
     */
    public boolean callEvent(Event event);
}
