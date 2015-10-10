package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

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

    static Pixmap pencil;
    static Pixmap eraser;

    static Box tmpbox = null;
    static Goal tmpgoal = null;
    static Triangle tmptriangle = null;
    static Door tmpdoor = null;
    static boolean update = false;
    static boolean updateModifier = false;

    static boolean drawGrid = false;

    public static void editLevel() {
        // todo create a destroyAllOthers() function
        Inputs.getEditInputs();

        // Toggle grid
        if(!drawGrid && Edit.grid == Edit.Grid.off) {
            drawGrid = true;
            Edit.grid = Edit.Grid.on;
        }

        // Edit states (also we must reset all tmp objects to null so they don't continue to appear)
        if(Inputs.b) { // Box
            Edit.typeState = UserData.Type.box;
            if(tmptriangle != null) {
                NoteBounce.world.destroyBody(tmptriangle.body);
                tmptriangle = null;
            }
            if(tmpgoal != null) {
                NoteBounce.world.destroyBody(tmpgoal.body);
                tmpgoal = null;
            }
            if(tmpdoor != null) {
                NoteBounce.world.destroyBody(tmpdoor.body);
                tmpdoor = null;
            }
        } else if(Inputs.t) { // Triangle
            Edit.typeState = UserData.Type.triangle;
            if(tmpbox != null) {
                NoteBounce.world.destroyBody(tmpbox.body);
                tmpbox = null;
            }
            if(tmpgoal != null) {
                NoteBounce.world.destroyBody(tmpgoal.body);
                tmpgoal = null;
            }
            if(tmpdoor != null) {
                NoteBounce.world.destroyBody(tmpdoor.body);
                tmpdoor = null;
            }
        } else if(Inputs.g) { // Gun
            Edit.typeState = UserData.Type.gun;
            if(tmpbox != null) {
                NoteBounce.world.destroyBody(tmpbox.body);
                tmpbox = null;
            }
            if(tmptriangle != null) {
                NoteBounce.world.destroyBody(tmptriangle.body);
                tmptriangle = null;
            }
            if(tmpgoal != null) {
                NoteBounce.world.destroyBody(tmpgoal.body);
                tmpgoal = null;
            }
            if(tmpdoor != null) {
                NoteBounce.world.destroyBody(tmpdoor.body);
                tmpdoor = null;
            }
        } else if(Inputs.v) { // Goal
            Edit.typeState = UserData.Type.goal;
            if(tmpbox != null) {
                NoteBounce.world.destroyBody(tmpbox.body);
                tmpbox = null;
            }
            if(tmptriangle != null) {
                NoteBounce.world.destroyBody(tmptriangle.body);
                tmptriangle = null;
            }
            if(tmpdoor != null) {
                NoteBounce.world.destroyBody(tmpdoor.body);
                tmpdoor = null;
            }
        } else if(Inputs.m) {
            Edit.typeState = UserData.Type.door;
            if(tmpbox != null) {
                NoteBounce.world.destroyBody(tmpbox.body);
                tmpbox = null;
            }
            if(tmptriangle != null) {
                NoteBounce.world.destroyBody(tmptriangle.body);
                tmptriangle = null;
            }
            if(tmpgoal != null) {
                NoteBounce.world.destroyBody(tmpgoal.body);
                tmpgoal = null;
            }
        } else if (Inputs.c) {
            if(Edit.toolState == Edit.Tool.paint) {
                Edit.toolState = Edit.Tool.erase;

            } else if(Edit.toolState == Edit.Tool.erase) {
                Edit.toolState = Edit.Tool.paint;
            }

            if(tmpbox != null) {
                NoteBounce.world.destroyBody(tmpbox.body);
                tmpbox = null;
            }
            if(tmptriangle != null) {
                NoteBounce.world.destroyBody(tmptriangle.body);
                tmptriangle = null;
            }
            if(tmpgoal != null) {
                NoteBounce.world.destroyBody(tmpgoal.body);
                tmpgoal = null;
            }
            if(tmpdoor != null) {
                NoteBounce.world.destroyBody(tmpdoor.body);
                tmpdoor = null;
            }
        }

        // Painting
        if(Edit.toolState == Edit.Tool.paint) {
            Gdx.input.setCursorImage(pencil, 0, 0);
            switch(Edit.typeState) {
                case box: {

                    // Get all inputs to set the box's color
                    if(Inputs.y) {
                        Edit.colorState = UserData.Color.blue;
                        update = true;
                    } else if(Inputs.u) {
                        Edit.colorState = UserData.Color.green;
                        update = true;
                    } else if(Inputs.i) {
                        Edit.colorState = UserData.Color.cyan;
                        update = true;
                    } else if(Inputs.o) {
                        Edit.colorState = UserData.Color.magenta;
                        update = true;
                    } else if(Inputs.p) {
                        Edit.colorState = UserData.Color.yellow;
                        update = true;
                    }

                    // Get all inputs to set the box's shade
                    if(Inputs.one) {
                        Edit.shadeState = UserData.Shade.zero;
                        update = true;
                    } else if(Inputs.two) {
                        Edit.shadeState = UserData.Shade.one;
                        update = true;
                    } else if(Inputs.three) {
                        Edit.shadeState = UserData.Shade.two;
                        update = true;
                    } else if(Inputs.four) {
                        Edit.shadeState = UserData.Shade.three;
                        update = true;
                    } else if(Inputs.five) {
                        Edit.shadeState = UserData.Shade.four;
                        update = true;
                    } else if(Inputs.six) {
                        Edit.shadeState = UserData.Shade.five;
                        update = true;
                    } else if(Inputs.seven) {
                        Edit.shadeState = UserData.Shade.six;
                        update = true;
                    } else if(Inputs.eight) {
                        Edit.shadeState = UserData.Shade.seven;
                        update = true;
                    } else if(Inputs.nine) {
                        Edit.shadeState = UserData.Shade.eight;
                        update = true;
                    }

                    // CREATE THE BOX
                    if(tmpbox == null) {
                        tmpbox = new Box(Inputs.mouse, NoteBounce.scalePercent, Edit.colorState, Edit.shadeState, 0.5f);
                    } else if(update) {
                        tmpbox.update(Inputs.mouse, NoteBounce.scalePercent, Edit.colorState, Edit.shadeState, 0.5f);
                        update = false;
                    }

                    // Now we set the modifier attributes because the box (and userdata) is not null.
                    // Get inputs to set box's modifierSprites
                    if(Inputs.a) {
                        Edit.modifierState = UserData.Modifier.accelerator;
                        updateModifier = true;
                        for(int i = 0; i < tmpbox.userData.modifierTypes.length; i++) {
                            if(tmpbox.userData.modifierTypes[i].equals(UserData.ModifierType.gravityUp) ||
                                    tmpbox.userData.modifierTypes[i].equals(UserData.ModifierType.gravityDown) ||
                                    tmpbox.userData.modifierTypes[i].equals(UserData.ModifierType.gravityLeft) ||
                                    tmpbox.userData.modifierTypes[i].equals(UserData.ModifierType.gravityRight)) {

                                tmpbox.userData.modifierTypes[i] = UserData.ModifierType.none;
                                tmpbox.modifierSprites[i] = null;
                            }
                        }
                        if(tmpbox.modifierSprites[4] != null) {
                            tmpbox.modifierSprites[4] = null;
                        }
                    } else if(Inputs.s) {
                        Edit.modifierState = UserData.Modifier.gravity;
                        updateModifier = true;
                        for(int i = 0; i < tmpbox.userData.modifierTypes.length; i++) {
                            tmpbox.userData.modifierTypes[i] = UserData.ModifierType.none;
                            tmpbox.modifierSprites[i] = null;
                        }
                        if(tmpbox.modifierSprites[4] == null) {
                            tmpbox.modifierSprites[4] = new Sprite(new Texture("art/modifiers/g.png"));
                        }
                    } else if(Inputs.d) {
                        Edit.modifierState = UserData.Modifier.dampener;
                        updateModifier = true;
                        for(int i = 0; i < tmpbox.userData.modifierTypes.length; i++) {
                            if(tmpbox.userData.modifierTypes[i] == UserData.ModifierType.gravityUp ||
                                    tmpbox.userData.modifierTypes[i] == UserData.ModifierType.gravityDown ||
                                    tmpbox.userData.modifierTypes[i] == UserData.ModifierType.gravityLeft ||
                                    tmpbox.userData.modifierTypes[i] == UserData.ModifierType.gravityRight) {

                                tmpbox.userData.modifierTypes[i] = UserData.ModifierType.none;
                                tmpbox.modifierSprites[i] = null;
                            }
                        }
                        if(tmpbox.modifierSprites[4] != null) {
                            tmpbox.modifierSprites[4] = null;
                        }
                    } else {
                        updateModifier = false;
                    }

                    int id = - 1;
                    UserData.ModifierType tmptype = UserData.ModifierType.none;
                    String file = "";
                    if(Edit.modifierState == UserData.Modifier.accelerator) {
                        if(Inputs.up) {
                            id = 0;
                            tmptype = UserData.ModifierType.acceleratorUp;
                            file = "up";
                        } else if(Inputs.down) {
                            id = 1;
                            tmptype = UserData.ModifierType.acceleratorDown;
                            file = "down";
                        } else if(Inputs.left) {
                            id = 2;
                            tmptype = UserData.ModifierType.acceleratorLeft;
                            file = "left";
                        } else if(Inputs.right) {
                            id = 3;
                            tmptype = UserData.ModifierType.acceleratorRight;
                            file = "right";
                        }
                    } else if(Edit.modifierState == UserData.Modifier.dampener) {
                        if(Inputs.up) {
                            id = 0;
                            tmptype = UserData.ModifierType.dampenerUp;
                            file = "upX";
                        } else if(Inputs.down) {
                            id = 1;
                            tmptype = UserData.ModifierType.dampenerDown;
                            file = "downX";
                        } else if(Inputs.left) {
                            id = 2;
                            tmptype = UserData.ModifierType.dampenerLeft;
                            file = "leftX";
                        } else if(Inputs.right) {
                            id = 3;
                            tmptype = UserData.ModifierType.dampenerRight;
                            file = "rightX";
                        }
                    } else if(Edit.modifierState == UserData.Modifier.gravity) {
                        if(Inputs.up) {
                            id = 0;
                            tmptype = UserData.ModifierType.gravityUp;
                            file = "up";
                        } else if(Inputs.down) {
                            id = 1;
                            tmptype = UserData.ModifierType.gravityDown;
                            file = "down";
                        } else if(Inputs.left) {
                            id = 2;
                            tmptype = UserData.ModifierType.gravityLeft;
                            file = "left";
                        } else if(Inputs.right) {
                            id = 3;
                            tmptype = UserData.ModifierType.gravityRight;
                            file = "right";
                        }
                    }

                    if(id != -1) {
                        if(tmpbox.modifierSprites[id] == null) {
                            tmpbox.modifierSprites[id] =
                                    new Sprite(new Texture(Gdx.files.internal("art/modifiers/" + file + ".png")));
                            tmpbox.userData.modifierTypes[id] = tmptype;
                        } else {
                            tmpbox.modifierSprites[id] = null;
                            tmpbox.userData.modifierTypes[id] = UserData.ModifierType.none;
                        }
                    }

                    if(! Gdx.input.justTouched()) {
                        Vector2 v = new Vector2(0, 0);
                        if(Inputs.ctrl) {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.midlines) * NoteBounce.midlines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.midlines) * NoteBounce.midlines;
                        } else {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.lines) * NoteBounce.lines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.lines) * NoteBounce.lines;
                        }
                        tmpbox.setPos(v);
                    } else {
                        tmpbox.sprite.setAlpha(1.0f);
                        NoteBounce.boxes.add(tmpbox);
                        tmpbox = null;
                    }

                }
                break;
                case triangle: {

                    if(Inputs.q) {
                        Edit.triangleState = UserData.Triangle.TopLeft;
                        update = true;
                    } else if(Inputs.w) {
                        Edit.triangleState = UserData.Triangle.BotLeft;
                        update = true;
                    } else if(Inputs.e) {
                        Edit.triangleState = UserData.Triangle.BotRight;
                        update = true;
                    } else if(Inputs.r) {
                        Edit.triangleState = UserData.Triangle.TopRight;
                        update = true;
                    } else {
                        update = false;
                    }

                    if(Inputs.y) {
                        Edit.colorState = UserData.Color.blue;
                        update = true;
                    } else if(Inputs.u) {
                        Edit.colorState = UserData.Color.green;
                        update = true;
                    } else if(Inputs.i) {
                        Edit.colorState = UserData.Color.cyan;
                        update = true;
                    } else if(Inputs.o) {
                        Edit.colorState = UserData.Color.magenta;
                        update = true;
                    } else if(Inputs.p) {
                        Edit.colorState = UserData.Color.yellow;
                        update = true;
                    }

                    if(Inputs.one) {
                        Edit.shadeState = UserData.Shade.zero;
                        update = true;
                    } else if(Inputs.two) {
                        Edit.shadeState = UserData.Shade.one;
                        update = true;
                    } else if(Inputs.three) {
                        Edit.shadeState = UserData.Shade.two;
                        update = true;
                    } else if(Inputs.four) {
                        Edit.shadeState = UserData.Shade.three;
                        update = true;
                    } else if(Inputs.five) {
                        Edit.shadeState = UserData.Shade.four;
                        update = true;
                    } else if(Inputs.six) {
                        Edit.shadeState = UserData.Shade.five;
                        update = true;
                    } else if(Inputs.seven) {
                        Edit.shadeState = UserData.Shade.six;
                        update = true;
                    } else if(Inputs.eight) {
                        Edit.shadeState = UserData.Shade.seven;
                        update = true;
                    } else if(Inputs.nine) {
                        Edit.shadeState = UserData.Shade.eight;
                        update = true;
                    }

                    if(tmptriangle == null) {
                        tmptriangle = new Triangle(Edit.triangleState, Inputs.mouse, NoteBounce.scalePercent,
                                Edit.colorState, Edit.shadeState, 0.5f);
                    } else if(update) {
                        tmptriangle.update(Inputs.mouse, NoteBounce.scalePercent, Edit.triangleState,
                                Edit.colorState, Edit.shadeState, 0.5f);
                        update = false;
                    }

                    // Now we set the modifier attributes because the box (and userdata) is not null.
                    // Get inputs to set box's modifierSprites
                    if(Inputs.a) {
                        Edit.modifierState = UserData.Modifier.accelerator;
                        updateModifier = true;
                    } else if(Inputs.d) {
                        Edit.modifierState = UserData.Modifier.dampener;
                        updateModifier = true;
                    } else {
                        updateModifier = false;
                    }

                    int id = - 1;
                    UserData.ModifierType tmptype = UserData.ModifierType.none;
                    String file = "";
                    if(Edit.modifierState == UserData.Modifier.accelerator) {
                        if(Edit.triangleState == UserData.Triangle.BotLeft) {
                            if(Inputs.down) {
                                id = 1;
                                tmptype = UserData.ModifierType.acceleratorDown;
                                file = "down";
                            } else if(Inputs.left) {
                                id = 2;
                                tmptype = UserData.ModifierType.acceleratorLeft;
                                file = "left";
                            }
                        } else if(Edit.triangleState == UserData.Triangle.TopLeft) {
                            if(Inputs.up) {
                                id = 0;
                                tmptype = UserData.ModifierType.acceleratorUp;
                                file = "up";
                            } else if(Inputs.left) {
                                id = 2;
                                tmptype = UserData.ModifierType.acceleratorLeft;
                                file = "left";
                            }
                        } else if(Edit.triangleState == UserData.Triangle.BotRight) {
                            if(Inputs.down) {
                                id = 1;
                                tmptype = UserData.ModifierType.acceleratorDown;
                                file = "down";
                            } else if(Inputs.right) {
                                id = 3;
                                tmptype = UserData.ModifierType.acceleratorRight;
                                file = "right";
                            }
                        } else if(Edit.triangleState == UserData.Triangle.TopRight) {
                            if(Inputs.up) {
                                id = 0;
                                tmptype = UserData.ModifierType.acceleratorUp;
                                file = "up";
                            } else if(Inputs.right) {
                                id = 3;
                                tmptype = UserData.ModifierType.acceleratorRight;
                                file = "right";
                            }
                        }
                    } else if(Edit.modifierState == UserData.Modifier.dampener) {
                        if(Edit.triangleState == UserData.Triangle.BotLeft) {
                            if(Inputs.down) {
                                id = 1;
                                tmptype = UserData.ModifierType.dampenerDown;
                                file = "downX";
                            } else if(Inputs.left) {
                                id = 2;
                                tmptype = UserData.ModifierType.dampenerLeft;
                                file = "leftX";
                            }
                        } else if(Edit.triangleState == UserData.Triangle.TopLeft) {
                            if(Inputs.up) {
                                id = 0;
                                tmptype = UserData.ModifierType.dampenerUp;
                                file = "upX";
                            } else if(Inputs.left) {
                                id = 2;
                                tmptype = UserData.ModifierType.dampenerLeft;
                                file = "leftX";
                            }
                        } else if(Edit.triangleState == UserData.Triangle.BotRight) {
                            if(Inputs.down) {
                                id = 1;
                                tmptype = UserData.ModifierType.dampenerDown;
                                file = "downX";
                            } else if(Inputs.right) {
                                id = 3;
                                tmptype = UserData.ModifierType.dampenerRight;
                                file = "rightX";
                            }
                        } else if(Edit.triangleState == UserData.Triangle.TopRight) {
                            if(Inputs.up) {
                                id = 0;
                                tmptype = UserData.ModifierType.dampenerUp;
                                file = "upX";
                            } else if(Inputs.right) {
                                id = 3;
                                tmptype = UserData.ModifierType.dampenerRight;
                                file = "rightX";
                            }
                        }
                    }

                    if(id != -1) {
                        if(tmptriangle.modifierSprites[id] == null) {
                            tmptriangle.modifierSprites[id] =
                                    new Sprite(new Texture(Gdx.files.internal("art/modifiers/" + file + ".png")));
                            tmptriangle.userData.modifierTypes[id] = tmptype;
                        } else {
                            tmptriangle.modifierSprites[id] = null;
                            tmptriangle.userData.modifierTypes[id] = UserData.ModifierType.none;
                        }
                    }

                    if(! Gdx.input.justTouched()) {
                        Vector2 v = new Vector2(0, 0);
                        if(Inputs.ctrl) {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.midlines) * NoteBounce.midlines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.midlines) * NoteBounce.midlines;
                        } else {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.lines) * NoteBounce.lines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.lines) * NoteBounce.lines;
                        }
                        tmptriangle.setPos(v);
                    } else {
                        tmptriangle.sprite.setAlpha(1.0f);
                        NoteBounce.triangles.add(tmptriangle);
                        tmptriangle = null;
                    }

                }
                break;
                case goal: {

                    if(tmpgoal == null) {
                        tmpgoal = new Goal(Inputs.mouse, NoteBounce.scalePercent, 0.5f);
                    }

                    if(! Gdx.input.justTouched()) {
                        Vector2 v = new Vector2(0, 0);
                        if(Inputs.ctrl) {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.midlines) * NoteBounce.midlines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.midlines) * NoteBounce.midlines;
                        } else {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.lines) * NoteBounce.lines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.lines) * NoteBounce.lines;
                        }
                        tmpgoal.setPos(v);
                    } else {
                        tmpgoal.sprite.setAlpha(1.0f);
                        NoteBounce.goals.add(tmpgoal);
                        tmpgoal = null;
                    }
                }
                case gun: {

                    int id = - 1;
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

                    if(id != - 1) {
                        if(NoteBounce.guns[id] == null) {
                            NoteBounce.guns[id] = new Gun(position, NoteBounce.scalePercent, id);
                            NoteBounce.currentGun = id;
                            NoteBounce.ball.setPos(NoteBounce.guns[NoteBounce.currentGun].center);
                        } else {
                            NoteBounce.world.destroyBody(NoteBounce.guns[id].body);
                            NoteBounce.guns[id] = null;
                        }
                    }
                } break;
                case door: {
                    // TODO fix bug with door scale percentage
                    if(Inputs.comma) {
                        Edit.doorState = Door.State.shut;
                        update = true;
                    } else if(Inputs.period) {
                        Edit.doorState = Door.State.open;
                        update = true;
                    }

                    if(Inputs.semicolon) {
                        Edit.doorPlane = Door.Plane.vertical;
                        update = true;
                    } else if(Inputs.singlequote) {
                        Edit.doorPlane = Door.Plane.horizontal;
                        update = true;
                    }

                    if(tmpdoor == null) {
                        tmpdoor = new Door(Inputs.mouse, Edit.doorState, Edit.doorPlane, NoteBounce.scalePercent, 0.5f);
                    } else if(update) {
                        tmpdoor.update(Inputs.mouse, Edit.doorState, Edit.doorPlane, NoteBounce.scalePercent, 0.5f);
                        update = false;
                    }

                    if(! Gdx.input.justTouched()) {
                        Vector2 v = new Vector2(0, 0);
                        v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.midlines) * NoteBounce.midlines;
                        v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.midlines) * NoteBounce.midlines;
                        tmpdoor.setPos(v);
                    } else {
                        tmpdoor.sprite.setAlpha(1.0f);
                        NoteBounce.doors.add(tmpdoor);
                        tmpdoor = null;
                    }
                } break;
            }
        } else if(Edit.toolState == Edit.Tool.erase) { // Erasing
            Gdx.input.setCursorImage(eraser, 0, 0);
            if(Inputs.mouseleft) {
                Vector2 click = new Vector2(Inputs.mouse);
                for(int i = 0; i < NoteBounce.boxes.size; i++) {
                    if(Utility.isInsideCircle(click, NoteBounce.boxes.get(i).center,
                            NoteBounce.boxes.get(i).sprite.getWidth() / 2)) {
                        NoteBounce.world.destroyBody(NoteBounce.boxes.get(i).body);
                        NoteBounce.boxes.removeIndex(i);
                    }
                }
                for(int i = 0; i < NoteBounce.triangles.size; i++) {
                    if(Utility.isInsideCircle(click, NoteBounce.triangles.get(i).center,
                            NoteBounce.triangles.get(i).sprite.getWidth() / 2)) {
                        NoteBounce.world.destroyBody(NoteBounce.triangles.get(i).body);
                        NoteBounce.triangles.removeIndex(i);
                    }
                }
                for(int i = 0; i < NoteBounce.goals.size; i++) {
                    if(Utility.isInsideCircle(click, NoteBounce.goals.get(i).center,
                            NoteBounce.goals.get(i).sprite.getWidth() / 2)) {
                        NoteBounce.world.destroyBody(NoteBounce.goals.get(i).body);
                        NoteBounce.goals.removeIndex(i);
                    }
                }
                for(int i = 0; i < NoteBounce.doors.size; i++) {
                    if(Utility.isInsideCircle(click, NoteBounce.doors.get(i).center,
                            NoteBounce.doors.get(i).sprite.getWidth())) {
                        NoteBounce.world.destroyBody(NoteBounce.doors.get(i).body);
                        NoteBounce.doors.removeIndex(i);
                    }
                }
            }
        }
    }
}
