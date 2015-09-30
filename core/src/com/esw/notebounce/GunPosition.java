package com.esw.notebounce;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Alex on 9/30/2015.
 */
class GunPosition {
    private static float padding = 30 * NoteBounce.scalePercent;
    public static final Vector2 botLeft = new Vector2(padding, padding);
    public static final Vector2 left = new Vector2(padding, NoteBounce.ScreenHeight / 2);
    public static final Vector2 topLeft = new Vector2(padding, NoteBounce.ScreenHeight - padding);
    public static final Vector2 top = new Vector2(NoteBounce.ScreenWidth / 2, NoteBounce.ScreenHeight - padding);
    public static final Vector2 topRight = new Vector2(NoteBounce.ScreenWidth - padding, NoteBounce.ScreenHeight - padding);
    public static final Vector2 right = new Vector2(NoteBounce.ScreenWidth - padding, NoteBounce.ScreenHeight / 2);
    public static final Vector2 botRight = new Vector2(NoteBounce.ScreenWidth - padding, padding);
    public static final Vector2 bot = new Vector2(NoteBounce.ScreenWidth / 2, padding);
    public static final Vector2 center = new Vector2(NoteBounce.ScreenWidth / 2, NoteBounce.ScreenHeight / 2);
}
