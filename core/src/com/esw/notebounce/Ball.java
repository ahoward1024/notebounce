package com.esw.notebounce;

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
public class Ball extends Sprite {

    // This is REALLY convenient! Why doesn't LibGDX already do this for sprites??
    private Vector2 center;

    Body body;
    Sprite sprite;

    Ball(float x, float y, float scale) {
        sprite = new Sprite(new Texture("ball.png"));
        sprite.setCenter(x, y);
        sprite.setOriginCenter();
        sprite.setScale(scale);

        this.center = new Vector2(sprite.getX() + sprite.getWidth() / 2,
                                  sprite.getY() + sprite.getHeight() / 2);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(center.x / notebounce.PIXELS2METERS, center.y / notebounce.PIXELS2METERS);

        body = notebounce.getWorld().createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(((sprite.getWidth() * scale) / 2) / notebounce.PIXELS2METERS);
        // NOTE(alex): maybe set these as parameters in the future?
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;
        body.createFixture(fixtureDef);
        circleShape.dispose();
    }

    public Sprite sprite() {
        return sprite;
    }

    public Body body() {
        return body;
    }
}
