package me.scarlet.undertailor.util;

public class NumberUtil {
    
    public static interface Interpolator {
        Number interpolate(Number lowerBound, Number higherBound, float percent);
    }
    
    public static final Interpolator INTERPOLATOR_LINEAR = (lowerBound, higherBound, percent) -> {
        if(lowerBound.floatValue() == higherBound.floatValue()) return lowerBound.floatValue();
        return ((higherBound.floatValue() - lowerBound.floatValue()) * percent) + lowerBound.floatValue();
    };
}
