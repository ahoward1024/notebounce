package com.esw.notebounce;

/**
 * Created by Alex on 9/21/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class UserData {

    Type type;
    Triangle triangle = Triangle.none;
    Color color = Color.none;
    Shade shade = Shade.none;
    public Edge edge = Edge.none; // Public so BodyEditorLoader can set it
    public Modifier modifier = Modifier.none;
    public int id = -1; // ID number for guns

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
        mine
    }

    // These are capitalized to follow the file naming convention for each of the .pngs
    public enum Triangle {
        BotLeft,
        TopLeft,
        BotRight,
        TopRight,

        none
    }

    public enum Color {
        blue,
        green,
        cyan,
        magenta,
        yellow,
        goal,

        none
    }

    public enum Shade {
        zero,
        one,
        two,
        three,
        four,
        five,
        six,
        seven,
        eight,

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

    public enum Modifier {
        accelerator,
        dampener,
        gravity,

        none
    }

    UserData(Type type) {
        this.type = type;
    }

    public UserData(UserData userData) {
        this.type = userData.type;
        this.triangle = userData.triangle;
        this.color = userData.color;
        this.shade = userData.shade;
        this.modifier = userData.modifier;
        this.id = userData.id;
    }

    public static Modifier[] createModifierArray() {
        Modifier[] modifiers = new Modifier[4];
        modifiers[0] = Modifier.none;
        modifiers[1] = Modifier.none;
        modifiers[2] = Modifier.none;
        modifiers[3] = Modifier.none;
        return modifiers;
    }

    @Override
    public String toString() {
        String output = "Type:[" + type + "]";
        if(triangle != Triangle.none) output += " Triangle:[" + triangle + "]";
        if(color != Color.none) output += " Color:[" + color + "]";
        if(shade != Shade.none) output += " Shade:[" + shade.ordinal() + "]";
        if(edge != Edge.none) output += " Edge:[" + edge + "]";
        output += "Modifier:[" + modifier + "]";
        return output;
    }
}
