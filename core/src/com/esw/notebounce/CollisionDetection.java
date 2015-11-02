package com.esw.notebounce;

import com.badlogic.gdx.math.Vector2;
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

    public boolean simhit = false; // Returns true if the simulated ball collides with another object

//=====================================================================================================//

    // ALL TYPES FOR EASY REFERENCE =====================================================================
    private UserData.Type boundary   = UserData.Type.boundary;
    private UserData.Type box        = UserData.Type.box;
    private UserData.Type goal       = UserData.Type.goal;
    private UserData.Type triangle   = UserData.Type.triangle;
    private UserData.Type ball       = UserData.Type.ball;
    private UserData.Type sim        = UserData.Type.sim;
    private UserData.Type gun        = UserData.Type.gun;
    private UserData.Type door       = UserData.Type.door;
    private UserData.Type doorswitch = UserData.Type.doorswitch;
    private UserData.Type mine       = UserData.Type.mine;
    private UserData.Type modifier   = UserData.Type.modifier;

    private UserData.TriangleType botleft  = UserData.TriangleType.botleft;
    private UserData.TriangleType topleft  = UserData.TriangleType.topleft;
    private UserData.TriangleType botright = UserData.TriangleType.botright;
    private UserData.TriangleType topright = UserData.TriangleType.topright;

    private UserData.ModifierType accelerator = UserData.ModifierType.accelerator;
    private UserData.ModifierType dampener    = UserData.ModifierType.dampener;
    private UserData.ModifierType gravity     = UserData.ModifierType.gravity;

    private UserData.Color blue  = UserData.Color.blue;
    private UserData.Color green = UserData.Color.green;
    private UserData.Color yellow   = UserData.Color.yellow;
    private UserData.Color grey  = UserData.Color.grey;

    private UserData.Edge top   = UserData.Edge.top;
    private UserData.Edge bot   = UserData.Edge.bot;
    private UserData.Edge left  = UserData.Edge.left;
    private UserData.Edge right = UserData.Edge.right;
    //===================================================================================================

    //              0   1     2     3
    // EDGES     : [TOP, BOT, LEFT, RIGHT]
    // MODIFIERS : [UP, DOWN, LEFT, RIGHT]

    /**
     * Handles the beginning of a Box2D collision.
     * @param c The Contact object from the collision. Holds both fixtures involved in the collision.
     */
    public void beginContact(Contact c) {

        int notePtr = NoteBounce.notePtr;

        Fixture fa = c.getFixtureA(); // Static or kinematic fixture
        Fixture fb = c.getFixtureB(); // Dynamic fixture

        UserData uda = (UserData)fa.getUserData();
        UserData udb = (UserData)fb.getUserData();

        //if(udb.type.equals(sim) || uda.type.equals(sim)) {
        if(udb.type.equals(ball) || uda.type.equals(ball)) {
           System.out.println("UD a: " + uda.toString()); // DEBUG
           System.out.println("UD b: " + udb.toString()); // DEBUG
        }  // DEBUG


        // TODO debug this more
        // SIMULATION BALL: =============================================================================
        if(udb.type.equals(sim) || uda.type.equals(sim)) {
            if(uda.type.equals(gun) && uda.id != NoteBounce.currentGun) simhit = true;
            if(uda.type.equals(goal) || udb.type.equals(goal)) simhit = true;
            if(uda.type.equals(doorswitch)) simhit = false;
        }

        // SEMI DEBUG:
        // We need to calculate both accelerators and dampeners for the
        if((udb.type.equals(ball) || udb.type.equals(sim))) {

            if(uda.type.equals(modifier)) {
                if(uda.modifier.equals(accelerator)) {
                    if(uda.edge.equals(top)) {
                        accelerate(ImpulseType.down);
                    } else if(uda.edge.equals(bot)) {
                        accelerate(ImpulseType.up);
                    } else if(uda.edge.equals(left)) {
                        accelerate(ImpulseType.right);
                    } else if(uda.edge.equals(right)) {
                        accelerate(ImpulseType.left);
                    }
                } else if(uda.modifier.equals(dampener)) {
                    if(uda.edge.equals(top)) {
                        dampen(ImpulseType.up);
                    } else if(uda.edge.equals(bot)) {
                        dampen(ImpulseType.down);
                    } else if(uda.edge.equals(left)) {
                        dampen(ImpulseType.right);
                    } else if(uda.edge.equals(right)) {
                        dampen(ImpulseType.left);
                    }
                }
            }
        }
        //===============================================================================================

        // BALL: ========================================================================================

        if(udb.type.equals(ball) || uda.type.equals(ball)) {
            // If the ball hits a goal.
            if(uda.type.equals(goal) || udb.type.equals(goal)) {
                NoteBounce.goalHit = true;
                NoteBounce.playGoalNoise();
            }

            if(NoteBounce.playNotes) {
                // If the ball hits anything that plays will play a note, play a note.

                int notenum = 0;

                if(uda.type.equals(box)) {
                    switch(uda.color) {
                        case blue: {
                            notenum = 1;
                        }
                        break;
                        case green: {
                            notenum = 3;
                        }
                        break;
                        case yellow: {
                            notenum = 5;
                        }
                        break;
                    }
                }

                if(uda.type.equals(box) || uda.type.equals(triangle) ||
                    uda.type.equals(boundary)) {
                    if(NoteBounce.gravityDirection.equals("Down") || NoteBounce.gravityDirection.equals("Up")) {
                        if(uda.edge.equals(bot) || uda.edge.equals(top)) {
                            if(thresholdVelocityY(fb, 3.2f)) {
                                NoteBounce.playNote(notenum);
                            }
                        } else {
                            NoteBounce.playNote(notenum);
                        }
                    } else if(NoteBounce.gravityDirection.equals("Left") ||
                        NoteBounce.gravityDirection.equals("Right")) {
                        if(uda.edge.equals(bot) || uda.edge.equals(top)) {
                            if(thresholdVelocityX(fb, 3.2f)) {
                                NoteBounce.playNote(notenum);
                            }
                        } else {
                            NoteBounce.playNote(notenum);
                        }
                    }
                }

                if(uda.type.equals(modifier)) {
                    if(uda.modifier.equals(gravity)) {
                        if(uda.edge.equals(top)) {
                            NoteBounce.world.setGravity(new Vector2(0.0f, NoteBounce.gravity));
                        } else if(uda.edge.equals(bot)) {
                            NoteBounce.world.setGravity(new Vector2(0.0f, - NoteBounce.gravity));
                        } else if(uda.edge.equals(left)) {
                            NoteBounce.world.setGravity(new Vector2(NoteBounce.gravity, 0.0f));
                        } else if(uda.edge.equals(right)) {
                            NoteBounce.world.setGravity(new Vector2(- NoteBounce.gravity, 0.0f));
                        }
                    }
                }
            }

            // If the ball hits a gun
            if(uda.type.equals(gun)) {
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
            if(uda.type.equals(doorswitch)) {
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
    }

    /* CHORDS:
       CM = 0,2,6
       Am 2nd inv. = 2,5,7
       Dm =  2,4,6
       GM 2nd inv. = 1,4,6

     */

    public void endContact(Contact c) {

    }

    public void preSolve(Contact c, Manifold m) {

    }

    public void postSolve(Contact c, ContactImpulse ci) {

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

    public enum ImpulseType {
        up,
        down,
        left,
        right
    }

    void accelerate(ImpulseType type) {
        float additionalImpulseForce = 2.2f;
        if(NoteBounce.scalePercent != 1.0f) additionalImpulseForce *= (NoteBounce.scalePercent / 2);
        Vector2 direction = new Vector2(0,0);
        switch(type) {
            case up: {
                NoteBounce.ball.body.setLinearVelocity(NoteBounce.ball.body.getLinearVelocity().x, 0.0f);
                direction.set(0.0f, additionalImpulseForce);
            } break;
            case down: {
                NoteBounce.ball.body.setLinearVelocity(NoteBounce.ball.body.getLinearVelocity().x, 0.0f);
                direction.set(0.0f, -additionalImpulseForce);
            } break;
            case left: {
                NoteBounce.ball.body.setLinearVelocity(0.0f, NoteBounce.ball.body.getLinearVelocity().y);
                direction.set(-additionalImpulseForce, 0.0f);
            } break;
            case right: {
                NoteBounce.ball.body.setLinearVelocity(0.0f, NoteBounce.ball.body.getLinearVelocity().y);
                direction.set(additionalImpulseForce, 0.0f);
            } break;
        }
        NoteBounce.ball.body.applyLinearImpulse(direction, NoteBounce.ball.body.getWorldCenter(), true);
    }

    static void dampen(ImpulseType type) {
		switch(type) {
			case up: {
                NoteBounce.ball.body.setLinearVelocity(NoteBounce.ball.body.getLinearVelocity().x, 0.0f);
			} break;
			case down: {
                NoteBounce.ball.body.setLinearVelocity(NoteBounce.ball.body.getLinearVelocity().x, 0.0f);
			} break;
			case left: {
                NoteBounce.ball.body.setLinearVelocity(0.0f, NoteBounce.ball.body.getLinearVelocity().y);
			} break;
			case right: {
                NoteBounce.ball.body.setLinearVelocity(0.0f, NoteBounce.ball.body.getLinearVelocity().y);
			} break;
		}
        NoteBounce.dampenType = type;
        NoteBounce.didDampen = true;
    }
}
