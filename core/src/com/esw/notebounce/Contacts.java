package com.esw.notebounce;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by Alex on 9/2/2015.
 */
public class Contacts implements ContactListener {

    public void beginContact(Contact c) {
        Fixture fa = c.getFixtureA(); // Usually a static object
        Fixture fb = c.getFixtureB(); // Usually a dynamic object

        // Test if goal was hit
        if(fa.getUserData() == "goal") notebounce.hitGoal();
    }

    public void endContact(Contact c) {

    }

    public void preSolve(Contact c, Manifold m) {

    }

    public void postSolve(Contact c, ContactImpulse ci) {

    }
}
