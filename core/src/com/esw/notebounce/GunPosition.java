package com.esw.notebounce;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Alex on 9/30/2015.
 * Copyright echosoftworks 2015
 */
class GunPosition {
    private static float padding = 50;
    public static final Vector2 one = new Vector2(NoteBounce.bufferWidth + padding, NoteBounce.bufferHeight + padding);
    public static final Vector2 two = new Vector2(NoteBounce.ScreenWidth / 2, NoteBounce.bufferHeight + padding);
    public static final Vector2 three = new Vector2((NoteBounce.ScreenWidth - NoteBounce.bufferWidth) - padding, NoteBounce.bufferHeight + padding);
    public static final Vector2 four = new Vector2(NoteBounce.bufferWidth + padding, NoteBounce.ScreenHeight / 2);
    public static final Vector2 five = new Vector2(NoteBounce.ScreenWidth / 2, NoteBounce.ScreenHeight / 2);
    public static final Vector2 six = new Vector2((NoteBounce.ScreenWidth - NoteBounce.bufferWidth) - padding, NoteBounce.ScreenHeight / 2);
    public static final Vector2 seven = new Vector2(NoteBounce.bufferWidth + padding, (NoteBounce.ScreenHeight - NoteBounce.bufferHeight) - padding);
    public static final Vector2 eight = new Vector2(NoteBounce.ScreenWidth / 2, (NoteBounce.ScreenHeight - NoteBounce.bufferHeight) - padding);
    public static final Vector2 nine = new Vector2((NoteBounce.ScreenWidth - NoteBounce.bufferWidth) - padding, (NoteBounce.ScreenHeight - NoteBounce.bufferHeight) - padding);
}
