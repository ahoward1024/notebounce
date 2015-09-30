package com.esw.notebounce;

/**
 * Created by Alex on 9/28/2015.
 */
public class Edit {

    public static State state = State.none;

    public enum State {
        box,
        triangle,
        gun,
        door,

        none
    }
}
