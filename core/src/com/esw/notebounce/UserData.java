package com.esw.notebounce;

/**
 * Created by Alex on 9/21/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class UserData {

    public Type type;
    public TriangleType triangle = TriangleType.none;
    public ModifierType modifier = ModifierType.none;
    public Color color = Color.none;
    public Edge edge = Edge.none;
    public int id = -1;

    public enum Type {
        boundary,
        box,
        goal,
        triangle,
        ball,
        sim,
        gun,
        door,
        doorswitch,
        mine,
        modifier,
    }

    public enum TriangleType {
        botleft,
        topleft,
        botright,
        topright,

        none
    }

    public enum ModifierType {
        accelerator,
        dampener,
        gravity,

        none
    }

    public enum Color {
        blue,
        green,
        yellow,
        grey,

        none
    }

    public enum Edge {
        top,
        bot,
        left,
        right,
        hyp,

        none
    }

    UserData(Type type) {
        this.type = type;
    }

    public UserData(UserData userData) {
        this.type = userData.type;
        this.triangle = userData.triangle;
        this.modifier = userData.modifier;
        this.color = userData.color;
        this.edge = userData.edge;
        this.id = userData.id;
    }

    @Override
    public String toString() {
        String s = "Type:[" + type + "]";
        if(triangle != TriangleType.none) s += " | TriangleType:[" + triangle + "]";
        if(modifier != ModifierType.none) s += " | ModifierType:[" + modifier + "]";
        if(color != Color.none)           s += " | Color:[" + color + "]";
        if(edge != Edge.none)             s += " | Edge:[" + edge + "]";
        if(id != -1)                      s += " | id:[" + id + "]";
        return s;
    }
}
