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
