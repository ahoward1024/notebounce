package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
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
    Vector2 center;
    UserData userData = new UserData(UserData.Type.goal);

    Goal(Vector2 v, float scale, float alpha) {

        FileHandle image = Gdx.files.internal("art/goal.png");
        sprite = new Sprite(new Texture(image));
        sprite.setAlpha(alpha);
        sprite.setCenter(v.x, v.y);
        sprite.setOriginCenter();
        sprite.setScale(scale);

        center = new Vector2(sprite.getX() + (sprite.getWidth() / 2),
            sprite.getY() + (sprite.getHeight() / 2));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS);

        body = NoteBounce.getWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(((sprite.getWidth() * scale) / 2) / NoteBounce.PIXELS2METERS,
            ((sprite.getHeight() * scale) / 2) / NoteBounce.PIXELS2METERS);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef).setUserData(userData);

        shape.dispose();
    }

    public void setPos(Vector2 v) {
        center = v;
        sprite.setCenter(v.x, v.y);
        body.setTransform(v.x / NoteBounce.PIXELS2METERS, v.y / NoteBounce.PIXELS2METERS, 0.0f);
    }

    public void update(Vector2 v, float scale, UserData.Color color, UserData.Shade shade, float alpha) {
        userData.color = color;
        userData.shade = shade;

        sprite.getTexture().dispose();
        sprite.setTexture(new Texture("art/tiles/boxes/" + color + shade.ordinal() + ".png"));
        sprite.setAlpha(alpha);
        sprite.setCenter(v.x, v.y);
        sprite.setOriginCenter();
        sprite.setScale(scale);
        center = v;

        body.setTransform(v.x / NoteBounce.PIXELS2METERS, v.y / NoteBounce.PIXELS2METERS, 0.0f);
    }
}