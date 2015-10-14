package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Created by Alex on 10/12/2015.
 * Copyright echosoftworks 2015
 */
public class DoorSwitch {

    Sprite sprite;
    Vector2 center = new Vector2(0,0);
    Body body;

    float scale;

    UserData userData = new UserData(UserData.Type.doorswitch);

    boolean active = true;

    DoorSwitch(Vector2 v, float scale, int id) {
        this.scale = scale;

        userData.id = id;

        sprite = new Sprite(new Texture(Gdx.files.internal("art/doors/switch.png")));
        sprite.setOrigin(0.0f, 0.0f);
        sprite.setScale(scale);
        sprite.setPosition(v.x, v.y);

        center.x = (sprite.getX() + ((sprite.getWidth() / 2) * scale));
        center.y = (sprite.getY() + ((sprite.getHeight() / 2) * scale));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS);

        body = NoteBounce.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(((sprite.getWidth() / 16) * scale) / NoteBounce.PIXELS2METERS,
            (((sprite.getHeight() / 16) * scale) / NoteBounce.PIXELS2METERS));
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(userData);
    }

    public void update(Vector2 v) {
        sprite.setPosition(v.x, v.y);
        center.x = (sprite.getX() + ((sprite.getWidth() / 2) * scale));
        center.y = (sprite.getY() + ((sprite.getHeight() / 2) * scale));
        body.setTransform(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS, 0.0f);
    }

    public void trip() {
        Vector2 v = new Vector2(sprite.getX(), sprite.getY());

        sprite = new Sprite(new Texture(Gdx.files.internal("art/doors/trippedswitch.png")));
        sprite.setOrigin(0.0f, 0.0f);
        sprite.setScale(scale);
        sprite.setPosition(v.x, v.y);

        active = false;
    }

    @Override
    public String toString() {
        String s = "\t\t{\n";
        s += "\t\t\t\"position\":{\"x\":" + sprite.getX() + ",\"y\":" + sprite.getY() + "}\n";
        s += "\t\t}";
        return s;
    }
}
