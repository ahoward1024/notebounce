package com.esw.notebounce;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Alex on 9/21/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class Utility {

    /**
     * A simple linear interpolation function (https://en.wikipedia.org/wiki/Linear_interpolation).
     * @param edge0 The beginning interpolation value
     * @param edge1 The ending interpolation value
     * @param t The timestep of interpolation
     * @return The point on the interpolation line the number should be after the given timestep
     */
    public static float lerp(float edge0, float edge1, float t) {
        return (1 - t) * edge0 + t * edge1;
    }

    /**
     * An implementation of the smoothstep function (https://en.wikipedia.org/wiki/Smoothstep)
     * @param edge0 The beginning interpolation value
     * @param edge1 The ending interpolation value
     * @param t The timestep of interpolation
     * @return The point on the interpolation line the number should be after the given timestep
     */
    public static float smoothstep(float edge0, float edge1, float t)
    {
        // Scale, bias and saturate x to 0..1 range
        t = MathUtils.clamp((t - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        // Evaluate polynomial
        return t * t * (3 - (2 * t));
    }

    /**
     * An implementation of the smoothstep function (https://en.wikipedia.org/wiki/Smoothstep#Variations)
     * @param edge0 The beginning interpolation value
     * @param edge1 The ending interpolation value
     * @param t The timestep of interpolation
     * @return The point on the interpolation line the number should be after the given timestep
     */
    public static float smootherstep(float edge0, float edge1, float t)
    {
        // Scale, and clamp x to 0..1 range
        t = MathUtils.clamp((t - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        // Evaluate polynomial
        return t * t * t * (t * ((t * 6) - 15) + 10);
    }

    /**
     * Get the greatest common divisor between two numbers
     * @param a First number
     * @param b Second number
     * @return The integer GCD value
     */
    public static int GCD(int a, int b) {
        if (b==0) return a;
        return GCD(b,a%b);
    }

    /**
     * Find a scaling number for the width and the height of the screen based
     * on the greatest common divisor value. If the width and height parameters are
     * less than the base resolution's width and height (for our purposes 1920x1080 is what all
     * of the art assets are created at) then we scale down to the first matching GCD of the
     * width and height after the base GCD. Otherwise if width and height are bigger we find the
     * first matching GCD greater than the base width and height.
     * @param width The width of the screen
     * @param height The height of the screen
     * @return A float value that is the percent in which all assets need to be scaled to fit the screen
     */
    public static float findScalePercent(int width, int height) {
        if(width == NoteBounce.basew && height == NoteBounce.baseh) return 1;

        Array<Integer> numbers = new Array<Integer>();
        int m;
        if(width > height) m = height;
        else m = width;
        
        // If the width and height are less than the base width and height
        if(width < NoteBounce.basew && height < NoteBounce.baseh) {
            int div = 1; // Divider
            // If width and height are less than half of the basew and base h
            // then we need to scale further down otherwise we might get a GCD that is too big.
            if(width < NoteBounce.basew /2 & height < NoteBounce.baseh /2) {
                div = 2;
            }
            // Find the first matching GCD that is less than the base GCD scaled by the div value (if necessary)
            for(int i = m; i >= 1; i--) {
                if(width % i == 0 && height % i == 0 && i < GCD(NoteBounce.basew /div, NoteBounce.baseh /div)) {
                    return ((float)i / GCD(NoteBounce.basew /div, NoteBounce.baseh /div));
                }
            }
        } else if(width > NoteBounce.basew && height > NoteBounce.baseh){
            // If the width and height are greater than the base width and height
            // then we set the scale value to the greatest matching GCD above the base GCD
            for(int i = m; i >= 1; i--) {
                if(width % i == 0 && height % i == 0 && i > GCD(NoteBounce.baseh, NoteBounce.basew)) {
                    return ((float)i / GCD(NoteBounce.basew, NoteBounce.baseh));
                }
            }
        }
        return -1;
    }
}
