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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * Factory and data bag for an underlying {@link Disposable}
 * resource.
 * 
 * <p>This class works together with a {@link Resource}
 * class in order to allow the underlying Disposable to be
 * removed from memory when found to no longer be in use.
 * Implementations of this class should only implement the
 * three abstract methods to generate the Disposable and
 * Resource associated with itself, as well as be a data bag
 * for any data meant to be consistent across instances of
 * the Disposable.</p>
 *
 * @param <V> the produced underlying Disposable
 * @param <T> the produced Resource holding and interacting
 *        with the Disposable
 */
public abstract class ResourceFactory<V extends Disposable, T extends Resource<V>> {

    static final Logger log = LoggerFactory.getLogger(ResourceFactory.class);
    
    protected ResourceFactory() {
        this.completionConsumer = (disposable, err) -> {
            if (err != null) {
                this.handleLoadError(err);
            }

            if(this.cancelGen || this.getResourceReference() == null) {
                this.cancelGen = false;
                if(disposable != null) {
                    disposable.dispose();
                }
            } else {
                this.disposableGen = null;
                this.disposable = disposable;
                this.getResourceReference().load(disposable);
                synchronized(this.getResourceReference()) {
                    this.getResourceReference().notifyAll();
                }
            }
        };
    }
    
    V disposable;
    boolean cancelGen;
    ResourceReference<V, T> reference;
    CompletableFuture<V> disposableGen;
    BiConsumer<V, Throwable> completionConsumer;

    // ---------------- abstract method definitions ----------------

    /**
     * Generates a new {@link Disposable} of the type
     * assigned to this {@link ResourceFactory}.
     * 
     * <p>Any loading should be done by the returned
     * {@link CompletableFuture} object. The factory will
     * listen for when the loading is completed, and will
     * then assign the disposable (if any) to the underlying
     * resource.</p>
     * 
     * <p>The resource is generated prior to calling this
     * method, and if required, can be accessible through
     * {@link #getResourceReference()}.</p>
     * 
     * @return a new CompletableFuture promising the loading
     *         of the underlying Disposable resource
     */
    protected abstract CompletableFuture<V> loadDisposable();

    /**
     * Generates a new {@link Resource} of the type assigned
     * to this {@link ResourceFactory}.
     * 
     * @return a new Disposable
     */
    protected abstract T newResource();

    /**
     * Checks whether or not the provided {@link Disposable}
     * instance can be removed from memory.
     * 
     * <p>While this method will be used primarily for the
     * {@link ResourceHandler} to query whether or not the
     * current Disposable reference held by the factory can
     * be safely disposed, this method will also be
     * referenced for any loose Disposables that were
     * deferred and belonged to this factory.</p>
     * 
     * @param disposable the Disposable to check
     * 
     * @return whether or not it can be disposed without
     *         error or unintended behavior
     */
    public abstract boolean isDisposable(V disposable);

    // ---------------- optional methods ----------------

    /**
     * Handles errors that occur when loading a
     * {@link Disposable}.
     * 
     * <p>By default, this method simply logs the error.</p>
     * 
     * @param err the error caught
     */
    protected void handleLoadError(Throwable err)  {
        log.error("Failed to load resource under factory type " + this.getClass().getSimpleName(), err);
    }

    /**
     * Queries whether or not the main thread should wait
     * for the {@link Disposable} generated by this
     * {@link ResourceFactory}.
     * 
     * @return if this factory should always wait for its
     *         resource to load before giving access to the
     *         resource
     */
    protected boolean waitForLoading() {
        return false;
    }

    /**
     * Queries whether or not the {@link Disposable}s
     * generated by this {@link ResourceFactory} should be
     * disposed on the LWJGL Application thread (through
     * {@link Gdx.app#postRunnable()}).
     * 
     * @return whether or not this ResourceFactory's
     *         resources should be disposed on the game's
     *         main thread
     */
    protected boolean disposeOnGameThread() {
        return false;
    }

    /**
     * Called right before the underlying {@link Resource}
     * and {@link Disposable} are dispoed through calling
     * {@link #dispose()}.
     */
    protected void onDispose() {}

    // ---------------- object ----------------

    /**
     * Returns the current instance of the
     * {@link Disposable} held by this
     * {@link ResourceFactory}, of which may be null.
     * 
     * @return the current Disposable, or null if it was
     *         previously disposed and not regenerated, or
     *         never generated prior
     */
    protected final V getDisposable() {
        return this.disposable;
    }

    /**
     * Returns the current instance of the {@link Resource}
     * without triggering generation.
     * 
     * @return the underlying Resource if found, null
     *         otherwise
     */
    protected final T getResourceReference() {
        return this.reference != null ? this.reference.get() : null;
    }

    /**
     * Returns the current instance of the {@link Resource}
     * held by this {@link ResourceFactory}.
     * 
     * <p>If the current Resource has yet to exist, a new
     * one will be generated and stored by the factory until
     * its {@link #dispose()} method has been called. The
     * resource is generated first before loading the
     * disposable on another thread. Should any errors occur
     * loading the disposable on the other thread, this
     * factory's {@link #handleLoadError(Throwable)} method
     * will be called.</p>
     * 
     * @return the current Resource instance
     */
    @SuppressWarnings("javadoc")
    public final T getResource() {
        if (this.reference == null || this.reference.get() == null) {
            T resource = this.newResource();
            this.reference = new ResourceReference<>(resource, ResourceHandler.QUEUE, this);
        }

        if (this.disposable == null) {
            this.cancelGen = false;
            this.disposableGen = this.loadDisposable();
            if(disposableGen.isDone()) {
                try {
                    this.completionConsumer.accept(this.disposableGen.get(), null);
                } catch(Exception e) {
                    this.completionConsumer.accept(null, e);
                }
            } else {
                this.disposableGen.whenComplete(this.completionConsumer);
            }

            if(this.disposableGen != null && !this.disposableGen.isDone() && this.waitForLoading()) {
                synchronized(this.getResourceReference()) {
                    while(!this.getResourceReference().isLoaded()) {
                        try {
                            this.getResourceReference().wait(0, 10);
                        } catch(Exception e) {}
                    }
                }
            }
        }

        return this.reference.get();
    }

    /**
     * Disposes of the underlying {@link Resource} and
     * {@link Disposable} pair held by this
     * {@link ResourceFactory}.
     */
    public final void dispose() {
        this.onDispose();
        if(this.disposableGen != null) {
            this.cancelGen = true;
        }

        if (this.disposable != null) {
            if (this.disposeOnGameThread()) {
                Disposable copy = this.disposable;
                Gdx.app.postRunnable(() -> copy.dispose());
            } else {
                this.disposable.dispose();
            }

            this.disposable = null;
        }

        if (this.reference != null) {
            if (this.reference.get() != null)
                this.reference.get().disposable = null;
            this.reference.enqueue();
            this.reference = null;
        }
    }
}
