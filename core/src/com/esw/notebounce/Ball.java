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
 */
public class Ball {

    private Vector2 center;
    private Sprite sprite;
    private Body body;

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
        sprite = new Sprite(new Texture(Gdx.files.internal("ball.png")));
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
        body.createFixture(fixtureDef).setUserData("ball");
        circleShape.dispose();
    }

    public void setPos(float x, float y) {
        sprite.setCenter(x, y);
        body.setTransform(x / NoteBounce.PIXELS2METERS, y / NoteBounce.PIXELS2METERS, 0.0f);
    }

    /**
     * Sets the sprite's position to the body's position.
     */
    public void setSpriteToBodyPosition() {
        sprite.setPosition((body.getPosition().x * NoteBounce.PIXELS2METERS) - sprite.getOriginX(),
                           (body.getPosition().y * NoteBounce.PIXELS2METERS) - sprite.getOriginY());
    }

    public float getCenterX() {
        return center.x;
    }

    public float getCenterY() {
        return center.y;
    }

    public Vector2 getCenter() {
        return center;
    }

    /**
     * Gets the sprite of the ball.
     * @return The sprite of the ball.
     */
    public Sprite sprite() { return sprite; }

    /**
     * Gets the body of the ball.
     * @return The body of the ball.
     */
    public Body body() { return body; }
}
