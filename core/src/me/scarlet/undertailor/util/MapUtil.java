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

package me.scarlet.undertailor.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MapUtil {
    
    public static <T1, T2> Entry<T1, T2> getLastEntry(Map<T1, T2> map) {
        Iterator<Entry<T1, T2>> iterator = map.entrySet().iterator(); 
        Entry<T1, T2> last = null;
        while(iterator.hasNext()) {
            last = iterator.next();
        }
        
        //System.out.println("returned a last entry " + last);
        return last;
    }
    
    public static <T1, T2> Entry<T1, T2> getByIndex(Map<T1, T2> map, int index) {
        Iterator<Entry<T1, T2>> iterator = map.entrySet().iterator();
        int currentIndex = 0;
        while(iterator.hasNext()) {
            Entry<T1, T2> entry = iterator.next();
            if(currentIndex == index) {
                return entry;
            }
            
            currentIndex++;
        }
        
        return null;
    }
}
