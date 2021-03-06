package com.esw.notebounce;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Created by Alex on 9/15/2015.
 * Copyright echosoftworks 2015
 */
public class Boundary {

    Body body;
    UserData userData = new UserData(UserData.Type.boundary);

    /**
     *  This is called to create the ModifierType Lines for the boundaries of the screen
     *  so the ball will stay within the screen's bounds. If the edge is supposed to be
     *  for the bottom of the screen the bottom value should be set to true. This simplifies
     *  some of the collision detection code for later.
     * @param x1 The beginning x coordinate.
     * @param y1 The beginning y coordinate.
     * @param x2 The ending x coordinate.
     * @param y2 The ending y coordinate.
     * @param edge Indicates the type of the boundary (bot, top, left, right)
     */
    public Boundary(float x1, float y1, float x2, float y2, UserData.Edge edge) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0.0f, 0.0f);

        body = NoteBounce.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(x1 / NoteBounce.PIXELS2METERS, y1 / NoteBounce.PIXELS2METERS,
            x2 / NoteBounce.PIXELS2METERS, y2 / NoteBounce.PIXELS2METERS);
        fixtureDef.shape = edgeShape;

        userData.edge = edge;

        body.createFixture(fixtureDef).setUserData(userData);

        edgeShape.dispose();
    }
}
