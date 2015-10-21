package com.esw.notebounce;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by Alex on 9/11/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class CollisionDetection implements ContactListener {

    private float timeSinceLastBlueNote    = 0.0f;
    private float timeSinceLastGreenNote   = 0.0f;
    private float timeSinceLastYellowNote  = 0.0f;
    private float timeSinceLastBoundNote   = 0.0f;
    private float timeSinceLastCyanNote    = 0.0f;
    private float timeSinceLastMagentaNote = 0.0f;

    private boolean boundaryFlip = true; // Flips the notes when the ball hits the boundary
    private boolean yellowFlip = true;   // Flips the notes when the ball hits a yellow block
    private boolean cyanFlip = true;     // Flips the chord when the ball hits a cyan block
    private boolean magentaFlip = true;  // Flips the chord when the ball hits a magenta block

    public boolean simhit = false; // Returns true if the simulated ball collides with another object

//=====================================================================================================//

    /**
     * Update all the times since a note was played.
     * @param deltaTime The delta time of each frame
     */
    public void updateTimes(float deltaTime) {
        timeSinceLastBlueNote += deltaTime;
        timeSinceLastGreenNote += deltaTime;
        timeSinceLastYellowNote += deltaTime;
        timeSinceLastBoundNote += deltaTime;
        timeSinceLastCyanNote += deltaTime;
        timeSinceLastMagentaNote += deltaTime;
    }

    /**
     * Tests whether a dynamic body is moving at a speed greater than a threshold velocity in the
     * X direction.
     * @param f The fixture of the dynamic body.
     * @return True when the velocity is greather than threshold velocity.
     */
    private boolean thresholdVelocityX(Fixture f, float velX) {
        return Math.abs(f.getBody().getLinearVelocity().x) > velX;
    }

    /**
     * Tests whether a dynamic body is moving at a speed greater than a threshold velocity in the
     * Y direction.
     * @param f The fixture of the dynamic body.
     * @return True when the velocity is greather than threshold velocity.
     */
    private boolean thresholdVelocityY(Fixture f, float velY) {
         return Math.abs(f.getBody().getLinearVelocity().y) > velY;
    }

    //              0   1     2     3
    // EDGES     : [TOP, BOT, LEFT, RIGHT]
    // MODIFIERS : [UP, DOWN, LEFT, RIGHT]

    /**
     * Handles the beginning of a Box2D collision.
     * @param c The Contact object from the collision. Holds both fixtures involved in the collision.
     */
    public void beginContact(Contact c) {
        final float lastNoteTime = 0.8f; // The minimum time between two notes

        Fixture fa = c.getFixtureA(); // Static or kinematic fixture
        Fixture fb = c.getFixtureB(); // Dynamic fixture

        UserData uda = (UserData)fa.getUserData(); // Static or kinematic user data
        UserData udb = (UserData)fb.getUserData(); // Dynamic user data

        //if(udb.type.equals(UserData.Type.sim)) {
        if(udb.type.equals(UserData.Type.ball)) {
            System.out.println("UD a: " + uda.toString()); // DEBUG
            System.out.println("UD b: " + udb.toString()); // DEBUG
        }  // DEBUG

        int notePtr = NoteBounce.notePtr;

        //DEBUG
        //if(udb.type.equals(UserData.Type.sim)) simhit = true;

        // SIMULATION BALL: =============================================================================
        if(udb.type.equals(UserData.Type.sim) && !uda.type.equals(UserData.Type.doorswitch)) {
            if(uda.type.equals(UserData.Type.gun) && !(uda.id == NoteBounce.currentGun)) {
                simhit = true;
            }
        }

        // SEMI DEBUG:
        // We need to calculate this for the simulation and the ball itself
        // todo clean this up later
        if((udb.type.equals(UserData.Type.ball) || udb.type.equals(UserData.Type.sim))) {

            // Accelerator
            if(uda.modifier.equals(UserData.Modifier.accelerator)) {
                if(uda.edge.equals(UserData.Edge.top)) {
                    NoteBounce.addImpulseToBall(NoteBounce.ImpulseType.up);
                } else if(uda.edge.equals(UserData.Edge.bot)) {
                    NoteBounce.addImpulseToBall(NoteBounce.ImpulseType.down);

                } else if(uda.edge.equals(UserData.Edge.left)) {
                    NoteBounce.addImpulseToBall(NoteBounce.ImpulseType.left);

                } else if(uda.edge.equals(UserData.Edge.right)) {
                    NoteBounce.addImpulseToBall(NoteBounce.ImpulseType.right);
                }
            }
        }
        //===============================================================================================

        // BALL: ========================================================================================

        if(udb.type.equals(UserData.Type.ball)) {
            // If the ball hits a goal.
            if(uda.type.equals(UserData.Type.goal)) {
                NoteBounce.goalHit = true;
                // Play the goal noise if it was not already playing
                NoteBounce.playGoalNoise();
            }

            if(NoteBounce.playNotes) {
                // If the ball hits anything that plays will play a note, play a note.

                int notenum = 0;

                if(uda.type.equals(UserData.Type.box)) {
                    switch(uda.color) {
                        case blue: {
                            notenum = 1;
                        }
                        break;
                        case green: {
                            notenum = 2;
                        }
                        break;
                        case cyan: {
                            notenum = 3;
                        }
                        break;
                        case magenta: {
                            notenum = 4;
                        }
                        break;
                        case yellow: {
                            notenum = 5;
                        }
                        break;
                    }
                }

                if(uda.type.equals(UserData.Type.box) || uda.type.equals(UserData.Type.triangle) ||
                    uda.type.equals(UserData.Type.boundary)) {
                    if(NoteBounce.gravityDirection.equals("Down") || NoteBounce.gravityDirection.equals("Up")) {
                        if(uda.edge.equals(UserData.Edge.bot) || uda.edge.equals(UserData.Edge.top)) {
                            if(thresholdVelocityY(fb, 3.2f)) {
                                NoteBounce.playNote(notenum);
                            }
                        } else {
                            NoteBounce.playNote(notenum);
                        }
                    } else if(NoteBounce.gravityDirection.equals("Left") ||
                        NoteBounce.gravityDirection.equals("Right")) {
                        if(uda.edge.equals(UserData.Edge.bot) || uda.edge.equals(UserData.Edge.top)) {
                            if(thresholdVelocityX(fb, 3.2f)) {
                                NoteBounce.playNote(notenum);
                            }
                        } else {
                            NoteBounce.playNote(notenum);
                        }
                    }
                }
            }

            // If the ball hits a gun
            if(uda.type.equals(UserData.Type.gun)) {
                // If the hit gun is not the current gun
                if(uda.id != NoteBounce.currentGun) {
                    NoteBounce.currentGun = uda.id; // Then set the current gun to the hit gun
                    // We set reset to true because calling an external function that updates the
                    // Box2D world while the world is locked (eg. when calculating collisions)
                    // we will get at assertion
                    NoteBounce.reset = true;
                }
            }

            // If the ball hits a doorswitch
            // TODO fix playing notes on door collision (??? perhaps do not play notes ???)
            if(uda.type.equals(UserData.Type.doorswitch)) {
                DoorSwitch s = NoteBounce.switches.get(uda.id);
                // Only switch if the door switch is active (has not been hit before)
                if(s.active) {
                    s.trip();
                    Door d = NoteBounce.doors.get(uda.id);
                    d.body.getFixtureList().first().setSensor(!d.body.getFixtureList().first().isSensor());
                    if(d.state == Door.State.open) d.shut();
                    else if(d.state == Door.State.shut) d.open();
                }
            }
        }

        //===============================================================================================

        // If notes are allowed to be played at this time then we handle all of the
        // collisions involved with a note block.
       /* if(NoteBounce.playNotes() && fb.getUserData().equals("ball) {
            // TODO reimplement collision detection for each object
            // Boundary Edge collision
            if(fa.getUserData().equals("bot") || fa.getUserData().equals("top") ||
                fa.getUserData().equals("left") || fa.getUserData().equals("right"))
            {
                if(velocityThresholdX(fb, 1.0f) && velocityThresholdY(fb, 2.0f)) {
                    if(boundaryFlip) NoteBounce.playNote(1);
                    else NoteBounce.playNote(6);
                    boundaryFlip = ! boundaryFlip;
                    timeSinceLastBoundNote = 0.0f;
                    NoteBounce.playRipple(fb);
                }
            }

            // Blue note block collision
            if(fa.getUserData().equals("blue") && timeSinceLastBlueNote > lastNoteTime) {
                if (notePtr == NoteBounce.notesLength() - 1) notePtr = 0;
                else notePtr++;
                NoteBounce.playNote(notePtr);
                timeSinceLastBlueNote = 0.0f;
            }

            // Green note block collision
            if(fa.getUserData().equals("green") && timeSinceLastGreenNote > lastNoteTime) {
                if (notePtr == 0) notePtr = NoteBounce.notesLength() - 1;
                else notePtr--;
                NoteBounce.playNote(notePtr);
                timeSinceLastGreenNote = 0.0f;
            }

            // Yellow note block collision
            if(fa.getUserData().equals("yellow") && timeSinceLastYellowNote > lastNoteTime) {
                if (yellowFlip) {
                    yellowFlip = false;
                    notePtr += 4;
                    if (notePtr > NoteBounce.notesLength() - 1) {
                        int i = notePtr - (NoteBounce.notesLength() - 1);
                        notePtr = 0;
                        notePtr += i;
                    }
                    NoteBounce.playNote(notePtr);
                } else {
                    yellowFlip = true;
                    notePtr -= 4;
                    if (notePtr < 0) {
                        notePtr = Math.abs(notePtr);
                    }
                    NoteBounce.playNote(notePtr);
                }
                timeSinceLastYellowNote = 0.0f;
            }

            if(fa.getUserData().equals("topyellow") && timeSinceLastBoundNote > lastNoteTime) {
                if (yellowFlip) {
                    yellowFlip = false;
                    notePtr += 4;
                    if (notePtr > NoteBounce.notesLength() - 1) {
                        int i = notePtr - (NoteBounce.notesLength() - 1);
                        notePtr = 0;
                        notePtr += i;
                    }
                    NoteBounce.playNote(notePtr);
                } else {
                    yellowFlip = true;
                    notePtr -= 4;
                    if (notePtr < 0) {
                        notePtr = Math.abs(notePtr);
                    }
                    NoteBounce.playNote(notePtr);
                }
                timeSinceLastYellowNote = 0.0f;
                NoteBounce.addImpulseToBall();
            }

            // Cyan note block collision
            if(fa.getUserData().equals("cyan") && timeSinceLastCyanNote > lastNoteTime) {
                if(cyanFlip) {
                    // C Major
                    NoteBounce.playNote(0);
                    NoteBounce.playNote(2);
                    NoteBounce.playNote(6);
                    cyanFlip = !cyanFlip;
                } else {
                    // A minor 2nd inv.
                    NoteBounce.playNote(2);
                    NoteBounce.playNote(5);
                    NoteBounce.playNote(7);
                    cyanFlip = !cyanFlip;
                }
                timeSinceLastCyanNote = 0.0f;
            }

            // Magenta note block collision
            if(fa.getUserData().equals("magenta") && timeSinceLastMagentaNote > lastNoteTime) {
                if(cyanFlip) {
                    // D minor
                    NoteBounce.playNote(2);
                    NoteBounce.playNote(4);
                    NoteBounce.playNote(6);
                    magentaFlip = !magentaFlip;
                } else {
                    // G Major 2nd. inv
                    NoteBounce.playNote(1);
                    NoteBounce.playNote(4);
                    NoteBounce.playNote(6);
                    magentaFlip = !magentaFlip;
                }
                timeSinceLastMagentaNote = 0.0f;
            }

            if(fa.getUserData().equals("gravityUp")) {
                NoteBounce.getWorld().setGravity(new Vector2(0, -NoteBounce.gravity));
            }
            if(fa.getUserData().equals("gravityDown")) {
                NoteBounce.getWorld().setGravity(new Vector2(0, NoteBounce.gravity));
            }
            if(fa.getUserData().equals("gravityLeft")) {
                NoteBounce.getWorld().setGravity(new Vector2(-NoteBounce.gravity, 0));
            }
            if(fa.getUserData().equals("gravityRight")) {
                NoteBounce.getWorld().setGravity(new Vector2(NoteBounce.gravity, 0));
            }
        }*/
    }

    public void endContact(Contact c) {

    }

    public void preSolve(Contact c, Manifold m) {

    }

    public void postSolve(Contact c, ContactImpulse ci) {

    }
}
