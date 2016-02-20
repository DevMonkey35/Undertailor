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

public class NumberUtil {
    
    /**
     * Confirms the given float to the specified bounds.
     * 
     * @param f the float to parse
     * @param lower the lower boundary
     * 
     * @return the float, after bounds check
     */
    public static float boundFloat(float f, float lower) {
        if(f < lower) {
            return lower;
        }
        
        return f;
    }
    
    /**
     * Confirms the given float to the specified bounds.
     * 
     * @param f the float to parse
     * @param lower the lower boundary
     * @param upper the upper boundary
     * 
     * @return the float, after bounds check
     */
    public static float boundFloat(float f, float lower, float upper) {
        f = boundFloat(f, lower);
        if(f > upper) {
            return upper;
        }
        
        return f;
    }
    
    /**
     * Confirms the given double to the specified bounds.
     * 
     * @param d the double to parse
     * @param lower the lower boundary
     * 
     * @return the double, after bounds check
     */
    public static double boundDouble(double d, double lower) {
        if(d < lower) {
            return lower;
        }
        
        return d;
    }
    
    /**
     * Confirms the given double to the specified bounds.
     * 
     * @param d the double to parse
     * @param lower the lower boundary
     * @param upper the upper boundary
     * 
     * @return the double, after bounds check
     */
    public static double boundDouble(double d, double lower, double upper) {
        d = boundDouble(d, lower);
        if(d > upper) {
            return upper;
        }
        
        return d;
    }
}
