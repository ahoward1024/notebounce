package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by Alex on 10/7/2015.
 * Copyright echosoftworks 2015
 */
public class Door {

    Sprite sprite;
    Vector2 center = new Vector2(0,0);
    Body body;
    UserData userData = new UserData(UserData.Type.door);
    float scale;
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

    Door(Vector2 v, State state, Plane plane, float scale, int id) {
        this.scale = scale;
        this.state = state;
        this.plane = plane;
        userData.id = id;

        sprite = new Sprite(new Texture(Gdx.files.internal("art/" + state + plane + ".png")));

        sprite.setOrigin(0.0f, 0.0f);
        sprite.setScale(scale);
        sprite.setPosition(v.x, v.y);

        center.x = (sprite.getX() + ((sprite.getWidth() / 2) * scale));
        center.y = (sprite.getY() + ((sprite.getHeight() / 2) * scale));

        loadfixtures();
    }

    public void update(Vector2 v, State state, Plane plane) {
        sprite.setOrigin(0.0f, 0.0f);

        this.state = state;
        this.plane = plane;

        if(state == State.open) open();
        else shut();

        setPos(v);

        loadfixtures();
    }

    public void setPos(Vector2 v) {
        sprite.setPosition(v.x, v.y);
        center.x = (sprite.getX() + ((sprite.getWidth() / 2) * scale));
        center.y = (sprite.getY() + ((sprite.getHeight() / 2) * scale));
        body.setTransform(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS, 0.0f);
    }

    public void open() {
        Vector2 v = new Vector2(sprite.getX(), sprite.getY());

        sprite = new Sprite(new Texture(Gdx.files.internal("art/open" + plane + ".png")));
        sprite.setOrigin(0.0f, 0.0f);
        sprite.setScale(scale);
        sprite.setPosition(v.x, v.y);

        state = State.open;
        body.getFixtureList().first().setSensor(true);
    }

    public void shut() {
        Vector2 v = new Vector2(sprite.getX(), sprite.getY());

        sprite = new Sprite(new Texture(Gdx.files.internal("art/shut" + plane + ".png")));
        sprite.setOrigin(0.0f, 0.0f);
        sprite.setScale(scale);
        sprite.setPosition(v.x, v.y);

        state = State.shut;
        body.getFixtureList().first().setSensor(false);
    }

    public void loadfixtures() {

        if(body != null) {
            NoteBounce.world.destroyBody(body);
            body = null;
        }

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
        bodyEditorLoader.attachFixture(body, "strut", fixtureDef, userData, base * scale);
        bodyEditorLoader.attachFixture(body, "cap1", fixtureDef, userData, base * scale);
        bodyEditorLoader.attachFixture(body, "cap2", fixtureDef, userData, base * scale);

        if(state == State.open) body.getFixtureList().first().setSensor(true);
    }

    @Override
    public String toString() {
        String s = "\t\t{\n";
        s += "\t\t\t\"x\":" + sprite.getX() + ",\n";
        s += "\t\t\t\"y\":" + sprite.getY() + ",\n";
        s += "\t\t\t\"state\":\"" + state + "\",\n";
        s += "\t\t\t\"plane\":\"" + plane + "\",\n";
        s += "\t\t\t\"id\":\"" + userData.id + "\"\n";
        s += "\t\t}";
        return s;
    }
}
