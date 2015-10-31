package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by Alex on 9/20/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class Goal {

    Sprite sprite;
    Body body;
    Vector2 center = new Vector2(0,0);
    UserData userData = new UserData(UserData.Type.goal);
    float scale;

    Goal(Vector2 v, float scale) {
        this.scale = scale;

        sprite = new Sprite(new Texture(Gdx.files.internal("art/goal.png")));
        sprite.setScale(scale);
        sprite.setPosition(v.x, v.y);

        center.x = (sprite.getX() + ((sprite.getWidth() / 2) * scale));
        center.y = (sprite.getY() + ((sprite.getHeight() / 2) * scale));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS);

        body = NoteBounce.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.0f;

        CircleShape shape = new CircleShape();
        shape.setRadius(((sprite.getWidth() * scale) / 4) / NoteBounce.PIXELS2METERS);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(userData);
        shape.dispose();
    }

    public void setPos(Vector2 v) {
        sprite.setPosition(v.x, v.y);
        center.x = (sprite.getX() + ((sprite.getWidth() / 2) * scale));
        center.y = (sprite.getY() + ((sprite.getHeight() / 2) * scale));
        body.setTransform(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS, 0.0f);
    }

    @Override
    public String toString() {
        String s = "\t\t{\n";
        s += "\t\t\t\"x\":" + sprite.getX() + ",\n";
        s += "\t\t\t\"y\":" + sprite.getY() + "\n";
        s += "\t\t}";
        return s;
    }
}