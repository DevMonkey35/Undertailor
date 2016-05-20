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
package me.scarlet.undertailor.resource;

import com.badlogic.gdx.utils.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Thread working in parallel with the main thread in order
 * to clean and dispose of {@link Resource}s no longer in
 * use.
 */
public class ResourceHandler extends Thread {
    
    static Logger log = LoggerFactory.getLogger(ResourceHandler.class);
    
    private Set<Disposable> removedKeys;
    
    public ResourceHandler() {
        this.setDaemon(true);
        this.setName("ResourceHandler");
        
        this.removedKeys = new HashSet<>();
    }
    
    @Override
    public void run() {
        log.info("Resource handler has been started.");
        
        while(true) {
            this.removedKeys.clear();
            Iterator<Entry<Disposable, WeakReference<Resource<?>>>> iterator = Resource.livingResources.entrySet().iterator();
            while(iterator.hasNext()) {
                Entry<Disposable, WeakReference<Resource<?>>> entry = iterator.next();
                Resource<?> resource = entry.getValue().get();
                if(resource == null || (resource.getTimeSinceLastAccess() > Resource.getLifetimeForResource(resource.getResourceClass())) && resource.isDisposable()) {
                    log.info("Disposing resource with disposable type " + entry.getKey().getClass().getSimpleName() + ".");
                    if(resource == null) {
                        entry.getKey().dispose();
                    } else {
                        resource.dispose();
                    }
                    
                    this.removedKeys.add(entry.getKey());
                }
            }
            
            synchronized(Resource.livingResources) {
                this.removedKeys.forEach(Resource.livingResources::remove);
            }
            
            try {
                Thread.sleep(500);
            } catch(InterruptedException ignored) {}
        }
    }
    
}
