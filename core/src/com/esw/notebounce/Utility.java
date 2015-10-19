package com.esw.notebounce;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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
     * Test whether a vector point is inside of a particular radius based on the centerpoint c1.
     * @param testpoint The center of the circle.
     * @param center The point to be tested.
     * @param radius the radius of the circle.
     * @return True if the test point is inside of the circle.
     */
    public static boolean isInsideCircle(Vector2 testpoint, Vector2 center, float radius) {
        return Math.pow((center.x - testpoint.x), 2) + Math.pow((center.y - testpoint.y), 2) <= Math.pow(radius, 2);
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

    public static float getAspectRatio(int a, int b) {
        return  (float)a / (float)b;
    }


    static float dimscaling = 0.5625f;
    public static Vector2 getScaleDimension(int width, int height) {
        Vector2 dim = new Vector2(0,0);
        float w = 0;
        for(int i = width; i > 9; i--) {
            float s = i * dimscaling;
            if(i % 8 == 0) {
                if((s % 8 == 0) && (s <= height)) {
                    w = i; break;
                }
            }
        }
        dim.x = w; dim.y = w * 0.5625f;
        return dim;
    }
}
