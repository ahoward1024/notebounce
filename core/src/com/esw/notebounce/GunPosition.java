package com.esw.notebounce;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Alex on 9/30/2015.
 */
class GunPosition {
    private static float padding = 30 * NoteBounce.scalePercent;
    public static final Vector2 one = new Vector2(padding, padding);
    public static final Vector2 two = new Vector2(NoteBounce.ScreenWidth / 2, padding);
    public static final Vector2 three = new Vector2(NoteBounce.ScreenWidth - padding, padding);
    public static final Vector2 four = new Vector2(padding, NoteBounce.ScreenHeight / 2);
    public static final Vector2 five = new Vector2(NoteBounce.ScreenWidth / 2, NoteBounce.ScreenHeight / 2);
    public static final Vector2 six = new Vector2(NoteBounce.ScreenWidth - padding, NoteBounce.ScreenHeight / 2);
    public static final Vector2 seven = new Vector2(padding, NoteBounce.ScreenHeight - padding);
    public static final Vector2 eight = new Vector2(NoteBounce.ScreenWidth / 2, NoteBounce.ScreenHeight - padding);
    public static final Vector2 nine = new Vector2(NoteBounce.ScreenWidth - padding, NoteBounce.ScreenHeight - padding);


}
