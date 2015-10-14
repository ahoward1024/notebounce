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
        mine,

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
        modifierTypes[0] = ModifierType.none;
        modifierTypes[1] = ModifierType.none;
        modifierTypes[2] = ModifierType.none;
        modifierTypes[3] = ModifierType.none;
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
        String output = "Type: [" + type + "]";
        if(triangle != Triangle.none) output += " Triangle : [" + triangle + "]";
        if(edge != Edge.none) output += " Edge : [" + edge + "]";
        if(color != Color.none) output += " Color : [" + color + "] Shade : [" + shade.ordinal() + "]";
        ModifierType m = null;
        for(ModifierType mod : modifierTypes) {
            m = mod;
        }
        if(m != null) {
            output += " Modifiers : [";
            for(ModifierType mod : modifierTypes) {
                if(mod != null) output += ", " + mod.name();
            }
            output += "]";
        }
        return output;
    }
}
