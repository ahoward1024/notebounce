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
    ModifierType[] modifierTypes = new ModifierType[5];
    int id = -1; // ID number for guns


    // FIXME URGENT UserData needs to be __PER FIXTURE__ NOT per object
    // fixme Each object will have to track it's own color, shade, etc but
    // fixme each _fixture_ will have an edge/modifier


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
    }

    // FIXME modifier types should be _per fixture_ so we only need accelerator, dampener, gravity, none
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
        this.modifierTypes = userData.modifierTypes;
    }

    @Override
    public String toString() {
        String output = "Type:[" + type + "]";
        if(triangle != Triangle.none) output += " Triangle:[" + triangle + "]";
        if(color != Color.none) output += " Color:[" + color + "]";
        if(shade != Shade.none) output += " Shade:[" + shade.ordinal() + "]";
        if(edge != Edge.none) output += " Edge:[" + edge + "]";
        ModifierType m = null;
        for(ModifierType mod : modifierTypes) {
            m = mod;
        }
        if(m != null) {
            output += " Modifiers:[";
            for(ModifierType mod : modifierTypes) {
                if(mod != null) output += mod.name() +",";
            }
            output += "]";
        }
        return output;
    }
}
