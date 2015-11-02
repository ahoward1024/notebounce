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
 * Created by Alex on 9/28/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class Triangle {

    Sprite sprite;
    Body body;
    Vector2 center = new Vector2(0,0);
    float scale;
    UserData.Color color;
    UserData.TriangleType triangle;

    Triangle(Vector2 v, float scale, UserData.TriangleType triangle, UserData.Color color) {
        this.triangle = triangle;
        this.color = color;
        this.scale = scale;

        setTriangle(triangle);
    }

    public void setTriangle(UserData.TriangleType triangle) {

        this.triangle = triangle;

        if(body != null) {
            NoteBounce.world.destroyBody(body);
            body = null;
        }

        UserData userData = new UserData(UserData.Type.triangle);
        userData.triangle = triangle;
        userData.color = color;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS);

        body = NoteBounce.world.createBody(bodyDef);

        setColor(color);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.0f;

        float base = (sprite.getHeight() / 100);
        float basescale = base * scale;

        BodyEditorLoader bodyEditorLoader =
            new BodyEditorLoader(Gdx.files.internal("fixtures/triangle" + triangle + ".json"));
       bodyEditorLoader.attachFixture(body, "hyp", fixtureDef, basescale, userData, UserData.Edge.hyp);

        if(userData.triangle == UserData.TriangleType.botleft) {
            bodyEditorLoader.attachFixture(body, "bot", fixtureDef, basescale,
                userData, UserData.Edge.bot);
            bodyEditorLoader.attachFixture(body, "left", fixtureDef, basescale,
                userData, UserData.Edge.left);
        } else if(userData.triangle == UserData.TriangleType.topleft) {
            bodyEditorLoader.attachFixture(body, "top", fixtureDef, basescale,
                userData, UserData.Edge.top);
            bodyEditorLoader.attachFixture(body, "left", fixtureDef, basescale,
                userData, UserData.Edge.left);
        } else if(userData.triangle == UserData.TriangleType.botright) {
            bodyEditorLoader.attachFixture(body, "bot", fixtureDef, basescale,
                userData, UserData.Edge.bot);
            bodyEditorLoader.attachFixture(body, "right", fixtureDef, basescale,
                userData, UserData.Edge.right);
        } else if(userData.triangle == UserData.TriangleType.topright) {
            bodyEditorLoader.attachFixture(body, "top", fixtureDef, basescale,
                userData, UserData.Edge.top);
            bodyEditorLoader.attachFixture(body, "right", fixtureDef,basescale,
                userData, UserData.Edge.right);
        }
    }

    public void setColor(UserData.Color color) {
        this.color = color;
        Vector2 v = new Vector2(0,0);
        if(sprite != null) {
            v = new Vector2(sprite.getX(), sprite.getY());
            sprite.getTexture().dispose();
            sprite = null;
        }
        sprite = new Sprite(new Texture(Gdx.files.internal("art/" + color + triangle + ".png")));
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
        s += "\t\t\t\"triangle\":\"" + triangle + "\",\n";
        s += "\n\t\t}";
        return s;
    }
}