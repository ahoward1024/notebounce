package com.esw.notebounce;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Created by Alex on 9/15/2015.
 */
public class Boundary {

    Body body;
    Type type;

    public enum Type {
        bot,
        top,
        left,
        right
    }

    /**
     *  This is called to create the Edge Lines for the boundaries of the screen
     *  so the ball will stay within the screen's bounds. If the edge is supposed to be
     *  for the bottom of the screen the bottom value should be set to true. This simplifies
     *  some of the collision detection code for later.
     * @param x1 The beginning x coordinate.
     * @param y1 The beginning y coordinate.
     * @param x2 The ending x coordinate.
     * @param y2 The ending y coordinate.
     * @param type Indicates the type of the boundary (bot, top, left, right)
     */
    public Boundary(float x1, float y1, float x2, float y2, Boundary.Type type) {
        this.type = type;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0.0f, 0.0f);

        body = NoteBounce.getWorld().createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(x1 / NoteBounce.PIXELS2METERS, y1 / NoteBounce.PIXELS2METERS,
            x2 / NoteBounce.PIXELS2METERS, y2 / NoteBounce.PIXELS2METERS);
        fixtureDef.shape = edgeShape;

        if(type == Boundary.Type.bot)
            body.createFixture(fixtureDef).setUserData("bot");
        else if(type == Boundary.Type.top)
            body.createFixture(fixtureDef).setUserData("top");
        else if(type == Boundary.Type.left)
            body.createFixture(fixtureDef).setUserData("left");
        else if(type == Boundary.Type.right)
            body.createFixture(fixtureDef).setUserData("right");

        edgeShape.dispose();
    }
}
