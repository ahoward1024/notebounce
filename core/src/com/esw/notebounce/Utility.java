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
    // TODO scale percentage for 16:10 and 4:3 windows
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
            if(width < NoteBounce.basew /2 & height < NoteBounce.baseh / 2) {
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
