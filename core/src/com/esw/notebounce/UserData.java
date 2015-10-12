package com.esw.notebounce;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Alex on 9/21/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class UserData {

    Type type;
    Triangle triangle = Triangle.none;
    Color color = Color.none;
    Shade shade = Shade.zero;
    public Edge edge = Edge.none; // Has to be public so BodyEditorLoader can set it.
    ModifierType[] modifierTypes = new ModifierType[4];
    int id = -1; // ID number for guns

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

        none
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
    }

    public enum ModifierType {
        acceleratorUp,
        acceleratorDown,
        acceleratorLeft,
        acceleratorRight,

        dampenerUp,
        dampenerDown,
        dampenerLeft,
        dampenerRight,

        gravityUp,
        gravityDown,
        gravityLeft,
        gravityRight,

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
        this.edge = userData.edge;
        this.modifierTypes = userData.modifierTypes;
    }

    @Override
    public String toString() {
        String output = "" + type;
        if(triangle != Triangle.none) output += " : " + triangle;
        if(edge != Edge.none) output += " : " + edge;
        output += " : " + color + " : " + shade.ordinal();
        if(modifierTypes.length > 0) {
            output += "[";
            for(ModifierType mod : modifierTypes) {
                output += ", " + mod.name();
            }
            output += "]";
        }
        return output;
    }
}
