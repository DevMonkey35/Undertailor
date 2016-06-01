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

package me.scarlet.undertailor.resource;

import com.badlogic.gdx.utils.Disposable;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * Reference class for weakly referencing a {@link Resource}
 * , as well as associating it with the factory that made it
 * and the {@link Disposable} that was assigned to it.
 *
 * @param <V> the type of the Disposable object held by the
 *        Resource
 * @param <T> the type of the Resource object itself
 */
public class ResourceReference<V extends Disposable, T extends Resource<V>>
    extends WeakReference<T> {

    V disposable;
    private ResourceFactory<V, T> owner;
    private Class<?> resourceClass;

    ResourceReference(T referent, ReferenceQueue<? super T> q, ResourceFactory<V, T> owner) {
        super(referent, q);
        this.owner = owner;
        this.disposable = referent.disposable;
        this.resourceClass = referent.getClass();
    }

    ResourceFactory<V, T> getOwningFactory() {
        return this.owner;
    }

    Class<?> getResourceClass() {
        return this.resourceClass;
    }

    V getDisposable() {
        return this.disposable;
    }
}
