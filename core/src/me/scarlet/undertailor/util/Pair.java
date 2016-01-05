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

import java.util.Objects;
import java.util.Optional;

public class Pair<T1, T2> {
    
    private T1 t1;
    private T2 t2;
    
    public Pair() {
        this(null, null);
    }
    
    public Pair(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }
    
    public Optional<T1> getFirstElement() {
        return Optional.ofNullable(t1);
    }
    
    public void setFirstElement(T1 t1) {
        this.t1 = t1;
    }
    
    public Optional<T2> getSecondElement() {
        return Optional.ofNullable(t2);
    }
    
    public void setSecondElement(T2 t2) {
        this.t2 = t2;
    }
    
    public boolean isEmpty() {
        return t1 == null && t2 == null;
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object o) {
        if(o instanceof Pair) {
            Pair other = (Pair) o;
            return t1.equals(other.t1) && t2.equals(other.t2);
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(t1, t2);
    }
}
