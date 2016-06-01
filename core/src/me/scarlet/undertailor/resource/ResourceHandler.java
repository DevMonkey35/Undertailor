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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Holds two threads responsible for the automatic disposal
 * of unused resources.
 * 
 * <p>Disposer: The main disposal thread that tracks the
 * reachable state of objects and disposes of them once
 * they've become weakly reachable.</p>
 * 
 * <p>Deferrer: The secondary disposal thread that tracks
 * the state of the disposable object to ensure it leaves as
 * intended. Disposable objects are passed here when they
 * are eligible for disposal as per its reachability state,
 * but has been deemed not disposable yet by its owning
 * {@link ResourceFactory}.</p>
 */
public class ResourceHandler {

    public static final String THREAD_ID_DISPOSER = "RscHandler-Disposer";
    public static final String THREAD_ID_DEFERRER = "RscHandler-Deferrer";

    static final ReferenceQueue<Resource<?>> QUEUE;

    private static final Set<Disposable> DEFERRED_CANCEL;
    private static final Set<Reference<?>> DEFERRED_RESOURCES;
    private static final Logger log = LoggerFactory.getLogger(ResourceHandler.class);

    /**
     * Removes a {@link Disposable} from the deferrence
     * disposal queue.
     * 
     * @param resource the resource to remove
     */
    static void removeFromDeferrence(Disposable resource) {
        synchronized (DEFERRED_CANCEL) {
            DEFERRED_CANCEL.add(resource);
        }
    }

    static {
        QUEUE = new ReferenceQueue<>();
        DEFERRED_CANCEL = new HashSet<>();
        DEFERRED_RESOURCES = new HashSet<>();
    }

    private Thread handlerThread;
    private Thread deferrenceThread;

    @SuppressWarnings("unchecked")
    public <D extends Disposable> ResourceHandler() {
        this.handlerThread = new Thread(() -> {
            while (true) {
                try {
                    ResourceReference<D, Resource<D>> reference =
                        (ResourceReference<D, Resource<D>>) ResourceHandler.QUEUE.remove();
                    String resourceTypeName = reference.getResourceClass().getName();

                    if (reference.getOwningFactory().isDisposable(reference.getDisposable())) {
                        log.info("Disposing resource of type " + resourceTypeName);
                        this.disposeFactoryResource(reference.getOwningFactory());
                    } else {
                        log.info("Deferring disposable for resource of type " + resourceTypeName);
                        synchronized (DEFERRED_RESOURCES) {
                            DEFERRED_RESOURCES.add(reference);
                        }
                    }
                } catch (InterruptedException ignore) {
                }
            }
        } , ResourceHandler.THREAD_ID_DISPOSER);

        this.deferrenceThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ignore) {
                }

                synchronized (ResourceHandler.DEFERRED_RESOURCES) {
                    synchronized (ResourceHandler.DEFERRED_CANCEL) {
                        if (!ResourceHandler.DEFERRED_RESOURCES.isEmpty()) {
                            Iterator<Reference<?>> iterator =
                                ResourceHandler.DEFERRED_RESOURCES.iterator();
                            while (iterator.hasNext()) {
                                ResourceReference<D, Resource<D>> reference =
                                    (ResourceReference<D, Resource<D>>) iterator.next();
                                String resourceTypeName = reference.getResourceClass().getName();

                                if (!ResourceHandler.DEFERRED_CANCEL.isEmpty()
                                    && ResourceHandler.DEFERRED_CANCEL
                                        .contains(reference.disposable)) {
                                    log.info("Canceling disposal of deferred resource of type "
                                        + resourceTypeName);
                                    ResourceHandler.DEFERRED_CANCEL.remove(reference.disposable);

                                    iterator.remove();
                                } else if (reference.getOwningFactory()
                                    .isDisposable(reference.disposable)) {
                                    log.info(
                                        "Disposing deferred resource of type " + resourceTypeName);
                                    if (reference
                                        .getOwningFactory().disposable == reference.disposable) {
                                        this.disposeFactoryResource(reference.getOwningFactory());
                                    } else {
                                        this.disposeLooseResource(reference.disposable);
                                    }

                                    iterator.remove();
                                }
                            }

                            DEFERRED_CANCEL.clear();
                        }
                    }
                }
            }
        } , ResourceHandler.THREAD_ID_DEFERRER);

        this.handlerThread.setDaemon(true);
        this.deferrenceThread.setDaemon(true);
    }

    public void start() {
        log.info("Resource handler has been started.");
        this.handlerThread.start();
        this.deferrenceThread.start();
    }

    private void disposeFactoryResource(ResourceFactory<?, ?> factory) {
        factory.dispose();
    }

    private void disposeLooseResource(Disposable disposable) {
        disposable.dispose();
    }
}
