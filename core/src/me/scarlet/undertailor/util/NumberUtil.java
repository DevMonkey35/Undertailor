package me.scarlet.undertailor.util;

public class NumberUtil {
    
    public static Number interpolateLinearly(Number lowerBound, Number higherBound, float percent) {
        if(lowerBound.floatValue() == higherBound.floatValue()) return lowerBound.floatValue();
        return ((higherBound.floatValue() - lowerBound.floatValue()) * percent) + lowerBound.floatValue();
    }
    
}
