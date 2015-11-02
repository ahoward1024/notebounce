package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by Alex on 9/20/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class Box {

    Sprite sprite;
    Body body;
    Vector2 center = new Vector2(0,0);
    float scale;
    UserData.Color color;

    Box(Vector2 v, float scale, UserData.Color color) {
        this.scale = scale;

        UserData userData = new UserData(UserData.Type.box);
        userData.color = color;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS);

        body = NoteBounce.world.createBody(bodyDef);

        setColor(color);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.0f;

        // Load the edges.json file to get all of the edge types (top, bot, left, right)
        // This is so we can specify what is the top of the tmpbox if we needs
        float base = (sprite.getHeight() / 100);
        float basescale = base * scale;

        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(Gdx.files.internal("fixtures/boxedges.json"));
        bodyEditorLoader.attachFixture(body, "top", fixtureDef, basescale,
            userData, UserData.Edge.top);
        bodyEditorLoader.attachFixture(body, "bot", fixtureDef, basescale,
            userData, UserData.Edge.bot);
        bodyEditorLoader.attachFixture(body, "left", fixtureDef, basescale, userData, UserData.Edge.left);
        bodyEditorLoader.attachFixture(body, "right", fixtureDef, basescale, userData, UserData.Edge.right);
    }

    public void setColor(UserData.Color color) {
        this.color = color;
        Vector2 v = new Vector2(0,0);
        if(sprite != null) {
            v = new Vector2(sprite.getX(), sprite.getY());
            sprite.getTexture().dispose();
            sprite = null;
        }
        sprite = new Sprite(new Texture(Gdx.files.internal("art/" + color + ".png")));
        sprite.setScale(scale);
        setPos(v);
    }

    public void setPos(Vector2 v) {
        sprite.setPosition(v.x, v.y);
        center.x = (sprite.getX() + ((sprite.getWidth() / 2) * scale));
        center.y = (sprite.getY() + ((sprite.getHeight() / 2) * scale));
        body.setTransform(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS, 0.0f);
    }

    public String toJson() {
        String s = "\t\t{\n";
        s += "\t\t\t\"x\":" + sprite.getX() + ",\n";
        s += "\t\t\t\"y\":" + sprite.getY() + ",\n";
        s += "\t\t\t\"color\":" + "\"" + color + "\",\n";
        s += "\n\t\t}";
        return s;
    }
}