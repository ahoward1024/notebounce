package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Alex on 9/28/2015.
 * Copyright echosoftworks 2015
 */
public class Edit {

    public static Tool toolState = Tool.paint;
    public static UserData.Type typeState = UserData.Type.box;
    public static UserData.Color colorState = UserData.Color.blue;
    public static UserData.TriangleType triangleState = UserData.TriangleType.botleft;
    public static UserData.ModifierType modifierState = UserData.ModifierType.accelerator;
    public static UserData.Edge edgeState = UserData.Edge.top;
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

    static Box tmpbox = null;
    static Triangle tmptriangle = null;
    static Modifier tmpmodifier = null;
    static Goal tmpgoal = null;
    static Door tmpdoor = null;
    static DoorSwitch tmpswitch = null;
    static Mine tmpmine = null;

    static int startgun = 0;

    static boolean drawGrid = false;

    static boolean saved = true;

    public static void destroyAll() {
        if(tmpbox != null) {
            NoteBounce.world.destroyBody(tmpbox.body);
            tmpbox = null;
        }
        if(tmptriangle != null) {
            NoteBounce.world.destroyBody(tmptriangle.body);
            tmptriangle = null;
        }
        if(tmpmodifier != null) {
            NoteBounce.world.destroyBody(tmpmodifier.body);
            tmpmodifier = null;
        }
        if(tmpgoal != null) {
            NoteBounce.world.destroyBody(tmpgoal.body);
            tmpgoal = null;
        }
        if(tmpdoor != null) {
            NoteBounce.world.destroyBody(tmpdoor.body);
            tmpdoor = null;
        }
        if(tmpswitch != null) {
            NoteBounce.world.destroyBody(tmpswitch.body);
            tmpswitch = null;
        }
        if(tmpmine != null) {
            NoteBounce.world.destroyBody(tmpmine.body);
            tmpmine = null;
        }
    }

    public static void editLevel() {
        Inputs.getEditInputs();

        // Toggle grid
        if(!drawGrid && grid == Grid.off) {
            drawGrid = true;
            grid = Grid.on;
        }

        // Save level
        if(Inputs.lctrl && Inputs.s) {
            LevelLoader.saveLevel(LevelLoader.levels.get(LevelLoader.levelPtr).name);
            saved = true;
        } else if(Inputs.lctrl && Inputs.n) {
            LevelLoader.saveLevel(LevelLoader.levels.get(LevelLoader.levelPtr).name);
            LevelLoader.newLevel();
            saved = true;
        }

        // Edit states (also we must reset all tmp objects to null so they don't continue to appear)
        else if(Inputs.b && !Inputs.lctrl) { // Box
            typeState = UserData.Type.box;
            destroyAll();
        } else if(Inputs.t && !Inputs.lctrl) { // TriangleType
            typeState = UserData.Type.triangle;
            destroyAll();
        } else if(Inputs.g && !Inputs.lctrl) { // Gun
            typeState = UserData.Type.gun;
            destroyAll();
        } else if(Inputs.v && !Inputs.lctrl) { // Goal
            typeState = UserData.Type.goal;
            destroyAll();
        } else if(Inputs.l && !Inputs.lctrl) { // Door
            typeState = UserData.Type.door;
            destroyAll();
        } else if(Inputs.m && !Inputs.lctrl) { // Mine
            typeState = UserData.Type.mine;
            destroyAll();
        } else if(Inputs.n && !Inputs.ctrl) {
            typeState = UserData.Type.modifier;
            destroyAll();
        } else if (Inputs.c && !Inputs.lctrl) { // Paint/Erase
            if(toolState == Tool.paint) {
                toolState = Tool.erase;

            } else if(toolState == Tool.erase) {
                toolState = Tool.paint;
            }
            destroyAll();
        }

        // Painting
        if(toolState == Tool.paint) {
            switch(typeState) {
                case box: {

                    // CREATE THE BOX
                    if(tmpbox == null) {
                        tmpbox = new Box(Inputs.mouse, NoteBounce.scalePercent, colorState);
                    }

                    if(Inputs.one) {
                        colorState = UserData.Color.blue;
                        tmpbox.setColor(colorState);
                    } else if(Inputs.two) {
                        colorState = UserData.Color.green;
                        tmpbox.setColor(colorState);
                    } else if(Inputs.three) {
                        colorState = UserData.Color.red;
                        tmpbox.setColor(colorState);
                    } else if(Inputs.four) {
                        colorState = UserData.Color.grey;
                        tmpbox.setColor(colorState);
                    }

                    if(Gdx.input.justTouched()) {
                        NoteBounce.boxes.add(tmpbox);
                        tmpbox = null;
                        saved = false;
                    } else {
                        Vector2 v = new Vector2(0, 0);
                        if(Inputs.lshift) {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.midlines) * NoteBounce.midlines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.midlines) * NoteBounce.midlines;
                        } else {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.lines) * NoteBounce.lines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.lines) * NoteBounce.lines;
                        }
                        tmpbox.setPos(v);
                    }

                } break;
                case triangle: {

                    // CREATE TRIANGLE
                    if(tmptriangle == null) {
                        tmptriangle = new Triangle(Inputs.mouse, NoteBounce.scalePercent, triangleState, colorState);
                    }

                    if(Inputs.one) {
                        colorState = UserData.Color.blue;
                        tmptriangle.setColor(colorState);
                    } else if(Inputs.two) {
                        colorState = UserData.Color.green;
                        tmptriangle.setColor(colorState);
                    } else if(Inputs.three) {
                        colorState = UserData.Color.red;
                        tmptriangle.setColor(colorState);
                    } else if(Inputs.four) {
                        colorState = UserData.Color.grey;
                        tmptriangle.setColor(colorState);
                    }

                    if(Inputs.q) {
                        triangleState = UserData.TriangleType.topleft;
                        tmptriangle.setTriangle(triangleState);
                    } else if(Inputs.w) {
                        triangleState = UserData.TriangleType.botleft;
                        tmptriangle.setTriangle(triangleState);
                    } else if(Inputs.e) {
                        triangleState = UserData.TriangleType.botright;
                        tmptriangle.setTriangle(triangleState);
                    } else if(Inputs.r) {
                        triangleState = UserData.TriangleType.topright;
                        tmptriangle.setTriangle(triangleState);
                    }

                    if(Gdx.input.justTouched()) {
                        NoteBounce.triangles.add(tmptriangle);
                        tmptriangle = null;
                        saved = false;
                    } else {
                        Vector2 v = new Vector2(0, 0);
                        if(Inputs.lshift) {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.midlines) * NoteBounce.midlines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.midlines) * NoteBounce.midlines;
                        } else {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.lines) * NoteBounce.lines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.lines) * NoteBounce.lines;
                        }
                        tmptriangle.setPos(v);
                    }
                } break;
                case modifier: {
                    if(tmpmodifier == null) {
                        tmpmodifier = new Modifier(Inputs.mouse, NoteBounce.scalePercent, modifierState, edgeState);
                    }

                    if(Inputs.one) {
                        modifierState = UserData.ModifierType.accelerator;
                        tmpmodifier.setModifier(modifierState);
                    } else if(Inputs.two) {
                        modifierState = UserData.ModifierType.dampener;
                        tmpmodifier.setModifier(modifierState);
                    } else if(Inputs.three) {
                        modifierState = UserData.ModifierType.gravity;
                        tmpmodifier.setModifier(modifierState);
                    }

                    if(Inputs.w) {
                        edgeState = UserData.Edge.top;
                        tmpmodifier.setEdge(edgeState);
                    } else if(Inputs.a) {
                        edgeState = UserData.Edge.left;
                        tmpmodifier.setEdge(edgeState);
                    } else if(Inputs.s) {
                        edgeState = UserData.Edge.bot;
                        tmpmodifier.setEdge(edgeState);
                    } else if(Inputs.d) {
                        edgeState = UserData.Edge.right;
                        tmpmodifier.setEdge(edgeState);
                    }

                    if(Gdx.input.justTouched()) {
                        NoteBounce.modifiers.add(tmpmodifier);
                        tmpmodifier = null;
                        saved = false;
                    } else {
                        Vector2 v = new Vector2(0, 0);
                        if(Inputs.lshift) {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.midlines) * NoteBounce.midlines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.midlines) * NoteBounce.midlines;
                        } else {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.lines) * NoteBounce.lines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.lines) * NoteBounce.lines;
                        }
                        tmpmodifier.setPos(v);
                    }
                } break;
                case goal: {

                    // Create a temporary goal if there is not already one
                    // Otherwise we update the temporary
                    if(tmpgoal == null) {
                        tmpgoal = new Goal(Inputs.mouse, NoteBounce.scalePercent);
                    }

                    // Set the temporary goal's alpha to 1 and put it into the goals array
                    // to become permanent. Set the temp back to null.
                    if(Gdx.input.justTouched()) {
                        NoteBounce.goals.add(tmpgoal);
                        tmpgoal = null;
                        saved = false;
                    } else {
                        Vector2 v = new Vector2(0, 0);
                        if(Inputs.lshift) {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.midlines) * NoteBounce.midlines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.midlines) * NoteBounce.midlines;
                        } else {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.lines) * NoteBounce.lines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.lines) * NoteBounce.lines;
                        }
                        tmpgoal.setPos(v);
                    }
                } break;
                case gun: {

                    // Use the numberkey pad to select where a gun should be placed/destroyed
                    int id = -1;
                    Vector2 position = new Vector2(0, 0);
                    if(Inputs.numone) {
                        id = 0;
                        position = GunPosition.one;
                    } else if(Inputs.numtwo) {
                        id = 1;
                        position = GunPosition.two;
                    } else if(Inputs.numthree) {
                        id = 2;
                        position = GunPosition.three;
                    } else if(Inputs.numfour) {
                        id = 3;
                        position = GunPosition.four;
                    } else if(Inputs.numfive) {
                        id = 4;
                        position = GunPosition.five;
                    } else if(Inputs.numsix) {
                        id = 5;
                        position = GunPosition.six;
                    } else if(Inputs.numseven) {
                        id = 6;
                        position = GunPosition.seven;
                    } else if(Inputs.numeight) {
                        id = 7;
                        position = GunPosition.eight;
                    } else if(Inputs.numnine) {
                        id = 8;
                        position = GunPosition.nine;
                    }

                    // If the id has been set to anything other than one we check to see if there is
                    // a gun in that spot yet. If there is not we place a new gun, if there is we
                    // destroy that gun.
                    if(id != -1) {
                        if(NoteBounce.guns[id] == null) {
                            startgun = id;
                            NoteBounce.guns[id] = new Gun(position, NoteBounce.scalePercent, id);
                            NoteBounce.currentGun = id;
                            NoteBounce.ball.setPos(NoteBounce.guns[NoteBounce.currentGun].center);
                        } else {
                            NoteBounce.world.destroyBody(NoteBounce.guns[id].body);
                            NoteBounce.guns[id] = null;
                        }
                        saved = false;
                    }
                } break;
                case door: {

                    // Set the door to an open or shut state
                    if(Inputs.comma) {
                        if(doorState == Door.State.shut) doorState = Door.State.open;
                        else doorState = Door.State.shut;
                    } else if(Inputs.period) {
                        if(doorPlane == Door.Plane.horizontal) doorPlane = Door.Plane.vertical;
                        else doorPlane = Door.Plane.horizontal;
                    }

                    // Create a temporary door if there is not already one. Otherwise update the
                    // temporary door.
                    if(tmpdoor == null) {
                        tmpdoor = new Door(Inputs.mouse, doorState, doorPlane,
                            NoteBounce.scalePercent, NoteBounce.doors.size);
                    } else {
                        tmpdoor.update(Inputs.mouse, doorState, doorPlane);
                        Vector2 v = new Vector2(0,0);
                        v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.midlines) * NoteBounce.midlines;
                        v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.midlines) * NoteBounce.midlines;
                        tmpdoor.setPos(v);
                    }

                    // If we have clicked the mouse set the alpha of the temporary door
                    // and add it to the permanent door array. Then set the temp door back to null.
                    // Then we must set a switch for the door.
                    if(Gdx.input.justTouched()) {
                        NoteBounce.doors.add(tmpdoor);
                        tmpdoor = null;
                        typeState = UserData.Type.doorswitch;
                        saved = false;
                    }
                } break;
                case doorswitch: {

                    // If there is not already a switch we create one.
                    // Else we update the temporary switch.
                    if(tmpswitch == null) {
                        tmpswitch = new DoorSwitch(Inputs.mouse, NoteBounce.scalePercent,
                            NoteBounce.switches.size);
                    } else {
                        Vector2 v = new Vector2(0,0);
                        v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.midlines) * NoteBounce.midlines;
                        v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.midlines) * NoteBounce.midlines;
                        tmpswitch.update(v);
                    }

                    // If we have clicked the mouse we set the temp
                    // switch's alpha and make it permanent. Then set the temp switch to null.
                    // Finally we go back to placing doors.
                    if(Gdx.input.justTouched()) {
                        NoteBounce.switches.add(tmpswitch);
                        tmpswitch = null;
                        typeState = UserData.Type.door;
                    }
                } break;
                case mine: {
                    if(tmpmine == null) {
                        tmpmine = new Mine(Inputs.mouse, NoteBounce.scalePercent);
                    } else {
                        Vector2 v = new Vector2(0,0);
                        v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.midlines) * NoteBounce.midlines;
                        v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.midlines) * NoteBounce.midlines;
                        tmpmine.update(v);
                    }

                    if(Gdx.input.justTouched()) {
                        NoteBounce.mines.add(tmpmine);
                        tmpmine = null;
                        saved = false;
                    }
                } break;
            }
        } else if(toolState == Tool.erase) { // Erasing
            if(Inputs.mouseleft) {
                Vector2 click = new Vector2(Inputs.mouse);

                for(int i = 0; i < NoteBounce.boxes.size; i++) {
                    Box b = NoteBounce.boxes.get(i);

                    if(Utility.isInsideCircle(click, b.center, (b.sprite.getWidth() * b.scale) / 3)) {
                        NoteBounce.world.destroyBody(b.body);
                        NoteBounce.boxes.removeIndex(i);
                        saved = false;
                    }
                }
                for(int i = 0; i < NoteBounce.triangles.size; i++) {
                    Vector2 v = new Vector2(0,0);
                    Triangle t = NoteBounce.triangles.get(i);
                    float pad = (t.sprite.getWidth() / 4) * t.scale;
                    if(t.triangle == UserData.TriangleType.botleft) {
                        v = new Vector2(t.center.x - pad, t.center.y - pad);
                    } else if(t.triangle == UserData.TriangleType.botright) {
                        v = new Vector2(t.center.x + pad, t.center.y - pad);
                    } else if(t.triangle == UserData.TriangleType.topleft) {
                        v = new Vector2(t.center.x - pad, t.center.y + pad);
                    } else if(t.triangle == UserData.TriangleType.topright) {
                        v = new Vector2(t.center.x + pad, t.center.y + pad);
                    }
                    if(Utility.isInsideCircle(click, v, (t.sprite.getWidth() * t.scale) / 4)) {
                        NoteBounce.world.destroyBody(t.body);
                        NoteBounce.triangles.removeIndex(i);
                        saved = false;
                    }
                }
                for(int i = 0; i < NoteBounce.modifiers.size; i++) {
                    Modifier m = NoteBounce.modifiers.get(i);
                    Vector2 v = new Vector2(0,0);
                    float pad = (m.sprite.getWidth() / 2) * m.scale;
                    if(m.edge == UserData.Edge.top) {
                        v = new Vector2(m.center.x, m.center.y + pad);
                    } else if(m.edge == UserData.Edge.bot) {
                        v = new Vector2(m.center.x, m.center.y - pad);
                    } else if(m.edge == UserData.Edge.left) {
                        v = new Vector2(m.center.x - pad, m.center.y);
                    } else if(m.edge == UserData.Edge.right) {
                        v = new Vector2(m.center.x + pad, m.center.y);
                    }
                    if(Utility.isInsideCircle(click, v, (m.sprite.getWidth() * m.scale) / 8)) {
                        NoteBounce.world.destroyBody(m.body);
                        NoteBounce.modifiers.removeIndex(i);
                        saved = false;
                    }
                }
                for(int i = 0; i < NoteBounce.goals.size; i++) {
                    if(Utility.isInsideCircle(click, NoteBounce.goals.get(i).center,
                            NoteBounce.goals.get(i).sprite.getWidth() / 3)) {
                        NoteBounce.world.destroyBody(NoteBounce.goals.get(i).body);
                        NoteBounce.goals.removeIndex(i);
                        saved = false;
                    }
                }
                for(int i = 0; i < NoteBounce.doors.size && i < NoteBounce.switches.size; i++) {
                    if(Utility.isInsideCircle(click, NoteBounce.doors.get(i).center,
                            NoteBounce.doors.get(i).sprite.getWidth() / 8) ||
                        Utility.isInsideCircle(click, NoteBounce.switches.get(i).center,
                            NoteBounce.switches.get(i).sprite.getWidth() / 8)) {
                        NoteBounce.world.destroyBody(NoteBounce.doors.get(i).body);
                        NoteBounce.world.destroyBody(NoteBounce.switches.get(i).body);
                        NoteBounce.doors.removeIndex(i);
                        NoteBounce.switches.removeIndex(i);
                        saved = false;
                    }
                }
                for(int i = 0; i < NoteBounce.mines.size; i++) {
                    if(Utility.isInsideCircle(click, NoteBounce.mines.get(i).center,
                        NoteBounce.mines.get(i).sprite.getWidth() / 8)) {
                        NoteBounce.world.destroyBody(NoteBounce.mines.get(i).body);
                        NoteBounce.mines.removeIndex(i);
                        saved = false;
                    }
                }
                for(int i = 0; i < NoteBounce.guns.length; i++) {
                    if(NoteBounce.guns[i] != null) {
                        if(Utility.isInsideCircle(click, NoteBounce.guns[i].center,
                            NoteBounce.guns[i].sprite.getWidth() / 8)) {
                            NoteBounce.world.destroyBody(NoteBounce.guns[i].body);
                            NoteBounce.guns[i] = null;
                            saved = false;
                        }
                    }
                }
            }
        }
    }
}
