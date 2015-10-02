package com.esw.notebounce;

/**
 * Created by Alex on 9/28/2015.
 */
public class Edit {

    public static UserData.Type typeState = UserData.Type.box;
    public static UserData.Color colorState = UserData.Color.blue;
    public static UserData.Shade shadeState = UserData.Shade.zero;
    public static UserData.Triangle triangleState = UserData.Triangle.BotLeft;
    public static Grid grid = Grid.off;

    public enum Grid {
        off,
        on
    }
}
