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
     * @param fb The fixture of the dynamic body.
     * @return True when the velocity is greather than threshold velocity.
     */
    private boolean velocityThresholdX(Fixture fb, float velX) {
        return Math.abs(fb.getBody().getLinearVelocity().x) > velX;
    }

    /**
     * Tests whether a dynamic body is moving at a speed greater than a threshold velocity in the
     * Y direction.
     * @param fb The fixture of the dynamic body.
     * @return True when the velocity is greather than threshold velocity.
     */
    private boolean velocityThresholdY(Fixture fb, float velY) {
         return Math.abs(fb.getBody().getLinearVelocity().y) > velY;
    }

    /**
     * Handles the beginning of a Box2D collision.
     * @param c The Contact object from the collision. Holds both fixtures involved in the collision.
     */
    public void beginContact(Contact c) {
        final float lastNoteTime = 0.8f; // The minimum time between two notes

        UserData uda = (UserData)c.getFixtureA().getUserData(); // Static or kinematic user data
        UserData udb = (UserData)c.getFixtureB().getUserData(); // Dynamic user data

        int notePtr = NoteBounce.getNotePtr();

        if(udb.getType().equals(UserData.Type.sim) && !uda.getType().equals(UserData.Type.gun))
        { simhit = true; }

        // Test if goal was hit
        if(uda.getStyle().equals(Box.Style.goal) && udb.getType().equals(UserData.Type.ball)) {
            NoteBounce.setGoalHit(true);
            // Play the goal noise if it was not already playing
            if(!NoteBounce.goalNoisePlaying()) {
                NoteBounce.playGoalNoise();//goalNoise.play();
            }
        }

        if(NoteBounce.playNotes() && udb.getType().equals(UserData.Type.ball)) {
            if(uda.getStyle().equals(Box.Style.yellow)) {
                if(uda.getEdge().equals(UserData.Edge.top)) {
                    NoteBounce.addImpulseToBall(NoteBounce.ImpulseType.up);
                }
                else if(uda.getEdge().equals(UserData.Edge.bot)) {
                    NoteBounce.addImpulseToBall(NoteBounce.ImpulseType.down);
                }
                else if(uda.getEdge().equals(UserData.Edge.left)) {
                    NoteBounce.addImpulseToBall(NoteBounce.ImpulseType.left);
                }
                else if(uda.getEdge().equals(UserData.Edge.right)) {
                    NoteBounce.addImpulseToBall(NoteBounce.ImpulseType.right);
                }
            }
        }

        // If notes are allowed to be played at this time then we handle all of the
        // collisions involved with a note block.
       /* if(NoteBounce.playNotes() && fb.getUserData().equals("ball) {
            // TODO reimplement collision detection for each object
            // TODO different boundary collision detection for each different gravity setting
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
