package com.esw.notebounce;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Created by Alex on 9/2/2015.
 */
public class Box {

    Vector2 center;

    Sprite sprite;
    Body body;
    boolean isGoal = false;

    public enum BoxType {
        box,
        goal,
        blue,
        yellow,
        green
    }

    // Create a standard box at (X, Y) with standard scale
    Box(float x, float y) {
        create(x, y, 1, BoxType.box);
    }

    // Create a standard box at (X, Y) scaled by the scale amount
    Box(float x, float y, float scale) {
        create(x, y, scale, BoxType.box);
    }

    // Create a typed box at (X, Y) with a standard scale
    Box(float x, float y, BoxType type) {
        create(x, y, 1, type);
    }

    // Create at typed box at (X, Y) scaled by the scale amount
    Box(float x, float y, float scale, BoxType type) {
        create(x, y, scale, type);
    }

    // Create a box
    private void create(float x, float y, float scale, BoxType type) {

        switch(type) {
            case goal: {
                sprite = new Sprite(new Texture("goal.png"));
            } break;
            case blue: {
                sprite = new Sprite(new Texture("bluebox.png"));
            } break;
            case green: {
                sprite = new Sprite(new Texture("greenbox.png"));
            } break;
            case yellow: {
                sprite = new Sprite(new Texture("yellowbox.png"));
            } break;
            default: sprite = new Sprite(new Texture("box.png"));
        }

        sprite.setCenter(x, y);
        sprite.setOriginCenter();
        sprite.setScale(scale);

        center = new Vector2(sprite.getX() + sprite.getWidth() / 2,
                             sprite.getY() + sprite.getHeight() / 2);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(center.x / notebounce.PIXELS2METERS, center.y / notebounce.PIXELS2METERS);

        body = notebounce.getWorld().createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(((sprite.getWidth() / 2) * scale) / notebounce.PIXELS2METERS,
                ((sprite.getHeight() / 2) * scale) / notebounce.PIXELS2METERS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;

        switch(type) {
            case goal: {
                body.createFixture(fixtureDef).setUserData("goal");
            } break;
            case blue: {
                body.createFixture(fixtureDef).setUserData("bluebox");
            } break;
            case green: {
                body.createFixture(fixtureDef).setUserData("greenbox");
            } break;
            case yellow: {
                body.createFixture(fixtureDef).setUserData("yellowbox");
            } break;
            default: body.createFixture(fixtureDef).setUserData("box");
        }

        polygonShape.dispose();
    }

    public Sprite sprite() { return sprite; }

    public Body body() { return body; }

    public float getCenterX() { return center.x; }

    public float getCenterY() { return center.y; }

    public boolean isGoal()   { return isGoal; }
}
