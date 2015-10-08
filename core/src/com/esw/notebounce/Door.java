package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by Alex on 10/7/2015.
 * Copyright echosoftworks 2015
 */
public class Door {

    Sprite sprite;
    final Sprite openSprite;
    final Sprite shutSprite;
    Vector2 center = new Vector2(0,0);
    Body body;
    UserData userData = new UserData(UserData.Type.door);
    float scale;
    float alpha;
    State state;
    Plane plane;

    public enum State {
        open,
        shut
    }

    public enum Plane {
        vertical,
        horizontal
    }

    Door(Vector2 v, float scale, State state, Plane plane) {
        this.scale = scale;
        this.state = state;
        this.plane = plane;

        openSprite = new Sprite(new Texture(Gdx.files.internal("art/doors/open" + plane + ".png")));
        shutSprite = new Sprite(new Texture(Gdx.files.internal("art/doors/shut" + plane + ".png")));

        if(state == State.open) sprite = new Sprite(openSprite);
        else sprite = new Sprite(shutSprite);

        sprite.setOrigin(0.0f, 0.0f);
        sprite.setScale(scale);
        //sprite.setAlpha(alpha);
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

        FileHandle fileHandle = Gdx.files.internal("fixtures/door" + plane + ".json");

        float base = 0.0f;
        if(sprite.getWidth() == sprite.getHeight()) base = (sprite.getHeight() / 100);

        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(fileHandle);
        bodyEditorLoader.attachFixture(body, "cap1", fixtureDef, userData, base * scale);
        bodyEditorLoader.attachFixture(body, "cap2", fixtureDef, userData, base * scale);

        if(state == State.shut) bodyEditorLoader.attachFixture(body, "strut", fixtureDef, userData, base * scale);
    }

    public void update(Vector2 v, float scale, float alpha) {
        sprite.setOrigin(0.0f, 0.0f);
        sprite.setAlpha(alpha);
        sprite.setScale(scale);

        setPos(v);
    }

    public void setPos(Vector2 v) {
        sprite.setPosition(v.x, v.y);
        center.x = (sprite.getX() + ((sprite.getWidth() / 2) * scale));
        center.y = (sprite.getY() + ((sprite.getHeight() / 2) * scale));
        body.setTransform(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS, 0.0f);
    }

    // TODO collision masks
    public void open() {
        if(state == State.shut) state = State.open;
        else throw new IllegalStateException("Door is already open!!");

        Vector2 v = new Vector2(sprite.getX(), sprite.getY());

        sprite = new Sprite(openSprite);
        setPos(v);


    }

    public void shut() {
        if(state == State.open) state = State.shut;
        else throw new IllegalStateException("Door is already shut!!");

        Vector2 v = new Vector2(sprite.getX(), sprite.getY());

        sprite = new Sprite(shutSprite);
        setPos(v);
    }
}
