package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

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
    static DoorSwitch tmpswitch = null;
    static Mine tmpmine = null;

    static int startgun = -1;

    static UserData.Modifier[] modifiers = UserData.createModifierArray();

    static boolean drawGrid = false;

    public static void destroyAll() {
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
        if(tmpswitch != null) {
            NoteBounce.world.destroyBody(tmpswitch.body);
            tmpswitch = null;
        }
        if(tmpmine != null) {
            NoteBounce.world.destroyBody(tmpmine.body);
            tmpmine = null;
        }
        modifiers = UserData.createModifierArray();
    }

    static boolean saved = false;
    UserData.Triangle lastTriangleState = UserData.Triangle.BotLeft;
    public static void editLevel() {
        // todo create a destroyAllOthers() function
        // todo create a destroyAll() function
        Inputs.getEditInputs();

        // Toggle grid
        if(!drawGrid && grid == Grid.off) {
            drawGrid = true;
            grid = Grid.on;
        }

        // Save level
        if(Inputs.lctrl && Inputs.s) {
            // TODO save
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            JFrame jFrame = new JFrame("Save");
            String levelname = JOptionPane.showInputDialog(jFrame, "Level name:");
            if(levelname != null && !levelname.equals("")) {
                LevelLoader.saveLevel(levelname);
            } else {
                System.out.println("Level name null. Not saving.");
            }
        } else if(Inputs.lctrl && Inputs.n) {
            destroyAll();
        }

        // Edit states (also we must reset all tmp objects to null so they don't continue to appear)
        else if(Inputs.b && !Inputs.lctrl) { // Box
            typeState = UserData.Type.box;
            destroyAll();
        } else if(Inputs.t && !Inputs.lctrl) { // Triangle
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
            Gdx.input.setCursorImage(pencil, 0, 0);
            switch(typeState) {
                case box: {

                    // Get all inputs to set the box's color
                    if(Inputs.y) {
                        colorState = UserData.Color.blue;
                    } else if(Inputs.u) {
                        colorState = UserData.Color.green;
                    } else if(Inputs.i) {
                        colorState = UserData.Color.cyan;
                    } else if(Inputs.o) {
                        colorState = UserData.Color.magenta;
                    } else if(Inputs.p) {
                        colorState = UserData.Color.yellow;
                    }

                    // Get all inputs to set the box's shade
                    if(Inputs.one) {
                        shadeState = UserData.Shade.zero;
                    } else if(Inputs.two) {
                        shadeState = UserData.Shade.one;
                    } else if(Inputs.three) {
                        shadeState = UserData.Shade.two;
                    } else if(Inputs.four) {
                        shadeState = UserData.Shade.three;
                    } else if(Inputs.five) {
                        shadeState = UserData.Shade.four;
                    } else if(Inputs.six) {
                        shadeState = UserData.Shade.five;
                    } else if(Inputs.seven) {
                        shadeState = UserData.Shade.six;
                    } else if(Inputs.eight) {
                        shadeState = UserData.Shade.seven;
                    } else if(Inputs.nine) {
                        shadeState = UserData.Shade.eight;
                    }

                    // CREATE THE BOX
                    if(tmpbox == null) {
                        tmpbox = new Box(Inputs.mouse, NoteBounce.scalePercent, colorState, shadeState);
                    }

                    // Now we set the modifier attributes because the box (and userdata) is not null.
                    // Get inputs to set box's modifierSprites
                    if(Inputs.a) {
                        modifierState = UserData.Modifier.accelerator;
                    } else if(Inputs.s && !Inputs.lctrl) {
                        modifierState = UserData.Modifier.gravity;
                    } else if(Inputs.d) {
                        modifierState = UserData.Modifier.dampener;
                    }


                    int id = -1;
                    String file = "";
                    if(modifierState == UserData.Modifier.accelerator) {
                        if(Inputs.up) {
                            id = 0;
                            file = "up";
                        } else if(Inputs.down) {
                            id = 1;
                            file = "down";
                        } else if(Inputs.left) {
                            id = 2;
                            file = "left";
                        } else if(Inputs.right) {
                            id = 3;
                            file = "right";
                        }
                    } else if(modifierState == UserData.Modifier.dampener) {
                        if(Inputs.up) {
                            id = 0;
                            file = "upX";
                        } else if(Inputs.down) {
                            id = 1;
                            file = "downX";
                        } else if(Inputs.left) {
                            id = 2;
                            file = "leftX";
                        } else if(Inputs.right) {
                            id = 3;
                            file = "rightX";
                        }
                    } else if(modifierState == UserData.Modifier.gravity) {
                        if(Inputs.up) {
                            id = 0;
                            file = "up";
                        } else if(Inputs.down) {
                            id = 1;
                            file = "down";
                        } else if(Inputs.left) {
                            id = 2;
                            file = "left";
                        } else if(Inputs.right) {
                            id = 3;
                            file = "right";
                        }

                    }

                    if(id != -1) {
                        if(tmpbox.modifierSprites[id] == null) {
                            tmpbox.modifierSprites[id] =
                                    new Sprite(
                                        new Texture(Gdx.files.internal("art/modifiers/" + file + ".png")));
                        } else {
                            tmpbox.modifierSprites[id] = null;
                        }

                        if(modifiers[id] == modifierState) modifiers[id] = UserData.Modifier.none;
                        else modifiers[id] = modifierState;
                    }

                    if(Gdx.input.justTouched()) {
                        tmpbox.sprite.setAlpha(1.0f);
                        NoteBounce.boxes.add(tmpbox);
                        tmpbox = null;

                    } else {
                        Vector2 v = new Vector2(0, 0);
                        if(Inputs.lshift) {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.midlines) * NoteBounce.midlines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.midlines) * NoteBounce.midlines;
                        } else {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.lines) * NoteBounce.lines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.lines) * NoteBounce.lines;
                        }
                        tmpbox.update(v, colorState, shadeState, modifiers);
                    }

                }
                break;
                case triangle: {

                    if(Inputs.q) {
                        triangleState = UserData.Triangle.TopLeft;
                        for(int i = 0; i < tmptriangle.modifierSprites.length; i++)
                            tmptriangle.modifierSprites[i] = null;
                        modifiers = UserData.createModifierArray();
                    } else if(Inputs.w) {
                        triangleState = UserData.Triangle.BotLeft;
                        for(int i = 0; i < tmptriangle.modifierSprites.length; i++)
                            tmptriangle.modifierSprites[i] = null;
                        modifiers = UserData.createModifierArray();
                    } else if(Inputs.e) {
                        triangleState = UserData.Triangle.BotRight;
                        for(int i = 0; i < tmptriangle.modifierSprites.length; i++)
                            tmptriangle.modifierSprites[i] = null;
                        modifiers = UserData.createModifierArray();
                    } else if(Inputs.r) {
                        triangleState = UserData.Triangle.TopRight;
                        for(int i = 0; i < tmptriangle.modifierSprites.length; i++)
                            tmptriangle.modifierSprites[i] = null;
                        modifiers = UserData.createModifierArray();
                    }

                    if(Inputs.y) {
                        colorState = UserData.Color.blue;
                    } else if(Inputs.u) {
                        colorState = UserData.Color.green;
                    } else if(Inputs.i) {
                        colorState = UserData.Color.cyan;
                    } else if(Inputs.o) {
                        colorState = UserData.Color.magenta;
                    } else if(Inputs.p) {
                        colorState = UserData.Color.yellow;
                    }

                    if(Inputs.one) {
                        shadeState = UserData.Shade.zero;
                    } else if(Inputs.two) {
                        shadeState = UserData.Shade.one;
                    } else if(Inputs.three) {
                        shadeState = UserData.Shade.two;
                    } else if(Inputs.four) {
                        shadeState = UserData.Shade.three;
                    } else if(Inputs.five) {
                        shadeState = UserData.Shade.four;
                    } else if(Inputs.six) {
                        shadeState = UserData.Shade.five;
                    } else if(Inputs.seven) {
                        shadeState = UserData.Shade.six;
                    } else if(Inputs.eight) {
                        shadeState = UserData.Shade.seven;
                    } else if(Inputs.nine) {
                        shadeState = UserData.Shade.eight;
                    }

                    if(tmptriangle == null) {
                        tmptriangle = new Triangle(triangleState, Inputs.mouse, NoteBounce.scalePercent,
                                colorState, shadeState);
                    }

                    // Now we set the modifier attributes because the box (and userdata) is not null.
                    // Get inputs to set box's modifierSprites
                    if(Inputs.a) {
                        modifierState = UserData.Modifier.accelerator;
                    } else if(Inputs.d) {
                        modifierState = UserData.Modifier.dampener;
                    }

                    int id = - 1;
                    String file = "";
                    if(modifierState == UserData.Modifier.accelerator) {
                        if(triangleState == UserData.Triangle.BotLeft) {
                            if(Inputs.down) {
                                id = 1;
                                file = "down";
                            } else if(Inputs.left) {
                                id = 2;
                                file = "left";
                            }
                        } else if(triangleState == UserData.Triangle.TopLeft) {
                            if(Inputs.up) {
                                id = 0;
                                file = "up";
                            } else if(Inputs.left) {
                                id = 2;
                                file = "left";
                            }
                        } else if(triangleState == UserData.Triangle.BotRight) {
                            if(Inputs.down) {
                                id = 1;
                                file = "down";
                            } else if(Inputs.right) {
                                id = 3;
                                file = "right";
                            }
                        } else if(triangleState == UserData.Triangle.TopRight) {
                            if(Inputs.up) {
                                id = 0;
                                file = "up";
                            } else if(Inputs.right) {
                                id = 3;
                                file = "right";
                            }
                        }

                    } else if(modifierState == UserData.Modifier.dampener) {
                        if(triangleState == UserData.Triangle.BotLeft) {
                            if(Inputs.down) {
                                id = 1;
                                file = "downX";
                            } else if(Inputs.left) {
                                id = 2;
                                file = "leftX";
                            }
                        } else if(triangleState == UserData.Triangle.TopLeft) {
                            if(Inputs.up) {
                                id = 0;
                                file = "upX";
                            } else if(Inputs.left) {
                                id = 2;
                                file = "leftX";
                            }
                        } else if(triangleState == UserData.Triangle.BotRight) {
                            if(Inputs.down) {
                                id = 1;
                                file = "downX";
                            } else if(Inputs.right) {
                                id = 3;
                                file = "rightX";
                            }
                        } else if(triangleState == UserData.Triangle.TopRight) {
                            if(Inputs.up) {
                                id = 0;
                                file = "upX";
                            } else if(Inputs.right) {
                                id = 3;
                                file = "rightX";
                            }
                        }

                    }

                    if(id != -1) {
                        if(tmptriangle.modifierSprites[id] == null) {
                            tmptriangle.modifierSprites[id] =
                                    new Sprite(new Texture(Gdx.files.internal("art/modifiers/" + file + ".png")));
                            modifiers[id] = modifierState;
                        } else {
                            tmptriangle.modifierSprites[id] = null;
                            modifiers[id] = UserData.Modifier.none;
                        }
                    }

                    if(Gdx.input.justTouched()) {
                        tmptriangle.sprite.setAlpha(1.0f);
                        NoteBounce.triangles.add(tmptriangle);
                        tmptriangle = null;
                    } else {
                        Vector2 v = new Vector2(0, 0);
                        if(Inputs.lshift) {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.midlines) * NoteBounce.midlines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.midlines) * NoteBounce.midlines;
                        } else {
                            v.x = (float) Math.floor(Inputs.mouse.x / NoteBounce.lines) * NoteBounce.lines;
                            v.y = (float) Math.floor(Inputs.mouse.y / NoteBounce.lines) * NoteBounce.lines;
                        }
                        tmptriangle.update(v, NoteBounce.scalePercent, triangleState, colorState, shadeState, modifiers);
                    }
                }
                break;
                case goal: {

                    // Create a temporary goal if there is not already one
                    // Otherwise we update the temporary
                    if(tmpgoal == null) {
                        tmpgoal = new Goal(Inputs.mouse, NoteBounce.scalePercent);
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

                    // Set the temporary goal's alpha to 1 and put it into the goals array
                    // to become permanent. Set the temp back to null.
                    if(Gdx.input.justTouched()) {
                        tmpgoal.sprite.setAlpha(1.0f);
                        NoteBounce.goals.add(tmpgoal);
                        tmpgoal = null;
                    }
                }
                case gun: {

                    // Use the numberkey pad to select where a gun should be placed/destroyed
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

                    // If the id has been set to anything other than one we check to see if there is
                    // a gun in that spot yet. If there is not we place a new gun, if there is we
                    // destroy that gun.
                    if(id != - 1) {
                        if(NoteBounce.guns[id] == null) {
                            startgun = id;
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

                    // Set the door to an open or shut state
                    if(Inputs.comma) {
                        doorState = Door.State.shut;
                    } else if(Inputs.period) {
                        doorState = Door.State.open;
                    }

                    // Set the door to horizontal or vertical
                    if(Inputs.semicolon) {
                        doorPlane = Door.Plane.vertical;
                    } else if(Inputs.singlequote) {
                        doorPlane = Door.Plane.horizontal;
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
                        tmpdoor.sprite.setAlpha(1.0f);
                        NoteBounce.doors.add(tmpdoor);
                        tmpdoor = null;
                        typeState = UserData.Type.doorswitch;
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
                        tmpswitch.sprite.setAlpha(1.0f);
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
                        tmpmine.sprite.setAlpha(1.0f);
                        NoteBounce.mines.add(tmpmine);
                        tmpmine = null;
                    }
                } break;
            }
        } else if(toolState == Tool.erase) { // Erasing
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
