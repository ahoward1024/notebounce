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

    public static int GCD(int a, int b) {
        if (b==0) return a;
        return GCD(b,a%b);
    }

    public static float findScalePercent(int a, int b) {
        if(a == NoteBounce.basex && b == NoteBounce.basey) return 1;

        Array<Integer> numbers = new Array<Integer>();
        int m;
        if(a > b) m = b;
        else m = a;

        if(a < NoteBounce.basex && b < NoteBounce.basey) {
            for(int i = m; i >= 1; i--) {
                if(a % i == 0 && b % i == 0 && i < GCD(NoteBounce.basex, NoteBounce.basey)) {
                    return ((float)i / GCD(NoteBounce.basex, NoteBounce.basey));
                }
            }
        } else if(a > NoteBounce.basex && b > NoteBounce.basey){
            for(int i = m; i >= 1; i--) {
                if(a % i == 0 && b % i == 0 && i > GCD(NoteBounce.basey, NoteBounce.basex)) {
                    return ((float)i / GCD(NoteBounce.basex, NoteBounce.basey));
                }
            }
        }
        return -1;
    }
}
