package com.esw.notebounce;

/**
 * Created by Alex on 9/28/2015.
 * Copyright echosoftworks 2015
 */
public class Edit {

    public static Tool toolState = Tool.paint;
    public static UserData.Type typeState = UserData.Type.box;
    public static UserData.Color colorState = UserData.Color.blue;
    public static UserData.Shade shadeState = UserData.Shade.zero;
    public static UserData.Triangle triangleState = UserData.Triangle.BotLeft;
    public static UserData.Modifier modifierState = UserData.Modifier.accelerator;
    public static Door.State doorState = Door.State.shut;
    public static Door.Plane doorPlane = Door.Plane.vertical;
    public static Grid grid = Grid.off;

    public enum Tool {
        paint,
        erase
    }

    public enum Grid {
        off,
        on
    }
}
