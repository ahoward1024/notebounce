package com.esw.notebounce;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by Alex on 9/11/2015.
 */
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

    public void updateTimes(float deltaTime) {
        timeSinceLastBlueNote += deltaTime;
        timeSinceLastGreenNote += deltaTime;
        timeSinceLastYellowNote += deltaTime;
        timeSinceLastBoundNote += deltaTime;
        timeSinceLastCyanNote += deltaTime;
        timeSinceLastMagentaNote += deltaTime;
    }

    private boolean simhit = false;
    public boolean isSimhit() {
        return simhit;
    }

    /**
     * Handles the beginning of a Box2D collision.
     * @param c The Contact object from the collision. Holds both fixtures involved in the collision.
     */
    public void beginContact(Contact c) {
        final float lastNoteTime = 0.8f; // The minimum time between two notes

        Fixture fa = c.getFixtureA(); // Usually a static object
        Fixture fb = c.getFixtureB(); // Usually a dynamic object

        int notePtr = NoteBounce.getNotePtr();

        if(fb.getUserData().equals("sim") && !fa.getUserData().equals("ball")) {
            simhit = true;
        } else {
            simhit = false;
        }

        // Test if goal was hit
        if(fa.getUserData().equals("goal") && fb.getUserData().equals("ball")) {
            NoteBounce.setGoalHit(true);
            // Play the goal noise if it was not already playing
            if(!NoteBounce.goalNoisePlaying()) {
                NoteBounce.playGoalNoise();//goalNoise.play();
            }
        }

        if(fb.getUserData().equals("ball") &&
            (!fa.getUserData().equals("boundary") || !fa.getUserData().equals("boundaryBot"))) {
            if(Math.abs(fb.getBody().getLinearVelocity().y) > 4.0f &&
                Math.abs(fb.getBody().getLinearVelocity().x) > 2.0f)
            {
                NoteBounce.playRipple(fb);
            }
        }

        // If notes are allowed to be played at this time then we handle all of the
        // collisions involved with a note block.
        if(NoteBounce.playNotes() && fb.getUserData().equals("ball")) {
            // Boundary Edge collision (not including bottom edge)
            if(fa.getUserData().equals("boundary") && timeSinceLastBoundNote > lastNoteTime) {
                if (boundaryFlip) NoteBounce.playNote(1);
                else NoteBounce.playNote(6);
                boundaryFlip = !boundaryFlip;
                timeSinceLastBoundNote = 0.0f;
            }

            // Boundary Edge collision (only for the bottom edge)
            if(fa.getUserData().equals("boundaryBot") && timeSinceLastBoundNote > lastNoteTime) {
                if (Math.abs(fb.getBody().getLinearVelocity().y) > 4.0f) {
                    if (boundaryFlip) NoteBounce.playNote(1);
                    else NoteBounce.playNote(6);
                    boundaryFlip = !boundaryFlip;
                    timeSinceLastBoundNote = 0.0f;
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

                NoteBounce.addImpulseToBall(fa);
            }

            // Cyan note block collision
            if(fa.getUserData().equals("cyan") && timeSinceLastCyanNote > 0.2f) {
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
            if(fa.getUserData().equals("magenta") && timeSinceLastMagentaNote > 0.2f) {
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
        }
    }

    public void endContact(Contact c) {

    }

    public void preSolve(Contact c, Manifold m) {

    }

    public void postSolve(Contact c, ContactImpulse ci) {

    }
}
