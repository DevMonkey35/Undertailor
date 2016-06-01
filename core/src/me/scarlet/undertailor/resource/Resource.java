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

/**
 * Wrapping class providing the functional methods for usage
 * of managed resources created by associated
 * ResourceFactories ({@link ResourceFactory}).
 * 
 * <p>Implementation of this class should purely be
 * functional and have bare minimum to no data storage, as
 * the data associated with instances of this class will not
 * be defined as the consistent data of the underlying
 * managed resource. All data should be stored by a parent
 * ResourceFactory, and should be accessed by requesting
 * from the former should the functions of this class
 * require them.</p>
 * 
 * @param <T> the type of the underlying Disposable provided
 *        by parent ResourceFactory
 */
public abstract class Resource<T extends Disposable> {

    T disposable;

    /**
     * Returns the current instance of the
     * {@link Disposable} object provided by the factory
     * that generated this {@link Resource}.
     * 
     * <p>This method may return null or return an unusable
     * Disposable due to already having been disposed,
     * though if either occurs then it is safe to assume the
     * object is no longer in use, and the object should no
     * longer be operational.</p>
     * 
     * @return the Disposable resource
     */
    protected T getReference() {
        return this.disposable;
    }
}
