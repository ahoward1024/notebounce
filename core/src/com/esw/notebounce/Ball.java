package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Created by Alex on 9/1/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class Ball {

    Vector2 center;
    Sprite sprite;
    Body body;

    /**
     * Calls the create() method to create a new ball at point (x, y) with a scale of 1.
     * @param x The x position for the center of the ball.
     * @param y The y position for the center of the ball.
     */
    Ball(float x, float y) {
        create(x, y, 1);
    }

    /**
     * Calls the create method to create a ball at point (x, y) with a scale of: scale.
     * @param x The x position for the center of the ball.
     * @param y The y position for the center of the ball.
     * @param scale The scale size of the ball.
     */
    Ball(float x, float y, float scale) {
        create(x, y, scale);
    }

    /**
     * Create a ball at point (x,y) with scale of: scale.
     * @param x The x position for the center of the ball.
     * @param y The y position for the center of the ball.
     * @param scale The scale size of the ball.
     */
    private void create(float x, float y, float scale) {
        sprite = new Sprite(new Texture(Gdx.files.internal("art/ball.png")));
        sprite.setCenter(x, y);
        sprite.setOriginCenter();
        sprite.setScale(scale);

        this.center = new Vector2(sprite.getX() + sprite.getWidth() / 2,
                sprite.getY() + sprite.getHeight() / 2);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody; // So it doesn't move immediately when created
        bodyDef.position.set(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS);

        body = NoteBounce.getWorld().createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(((sprite.getWidth() * scale) / 2) / NoteBounce.PIXELS2METERS);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.7f;
        fixtureDef.restitution = 0.5f;
        body.createFixture(fixtureDef).setUserData(new UserData(UserData.Type.ball));
        circleShape.dispose();
    }

    /**
     * Set the ball and it's body to a specified Vector2 position.
     * @param v The desired Vector2 postition of the ball.
     */
    public void setPos(Vector2 v) {
        sprite.setCenter(v.x, v.y);
        center.x = v.x;
        center.y = v.y;
        body.setTransform(v.x / NoteBounce.PIXELS2METERS, v.y / NoteBounce.PIXELS2METERS, 0.0f);
    }

    /**
     * Sets the sprite's position to the body's position.
     */
    public void setSpriteToBodyPosition() {
        center.x = body.getPosition().x * NoteBounce.PIXELS2METERS;
        center.y = body.getPosition().y * NoteBounce.PIXELS2METERS;
        sprite.setCenter(center.x, center.y);
    }
}
