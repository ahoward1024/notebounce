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
    Vector2 center;
    UserData userData = new UserData(UserData.Type.triangle);

    Triangle(UserData.Triangle triangle, UserData.Color color, float x, float y, float scale) {
        userData.triangle = triangle;
        userData.color = color;
        create(x, y, scale);
    }

    Triangle(UserData.Triangle triangle, UserData.Color color, UserData.Shade shade, float x, float y, float scale) {
        userData.triangle = triangle;
        userData.color = color;
        userData.shade = shade;
        create(x, y, scale);
    }

    private void create(float x, float y, float scale) {
        FileHandle image = Gdx.files.internal("art/tiles/triangles/" + userData.color +
            userData.triangle + userData.shade.ordinal() + ".png");
        sprite = new Sprite(new Texture(image));
        sprite.setCenter(x, y);
        sprite.setOriginCenter();
        sprite.setScale(scale);

        center = new Vector2(sprite.getX() + (sprite.getWidth() / 2),
            sprite.getY() + (sprite.getHeight() / 2));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS);

        body = NoteBounce.getWorld().createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.0f;


        FileHandle trianglesFile = Gdx.files.internal("fixtures/triangle" + userData.triangle + ".json");

        float base = 0.0f;
        if(sprite.getWidth() == sprite.getHeight()) base = (sprite.getHeight() / 100);

        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(trianglesFile);
        bodyEditorLoader.attachFixture(body, "hyp", fixtureDef, userData, UserData.Edge.hyp, base * scale);

        if(userData.triangle == UserData.Triangle.BotLeft) {
            bodyEditorLoader.attachFixture(body, "bot", fixtureDef, userData, UserData.Edge.bot, base * scale);
            bodyEditorLoader.attachFixture(body, "left", fixtureDef, userData, UserData.Edge.left, base * scale);
        } else if(userData.triangle == UserData.Triangle.TopLeft) {
            bodyEditorLoader.attachFixture(body, "top", fixtureDef, userData, UserData.Edge.top, base * scale);
            bodyEditorLoader.attachFixture(body, "left", fixtureDef, userData, UserData.Edge.left, base * scale);
        } else if(userData.triangle == UserData.Triangle.BotRight) {
            bodyEditorLoader.attachFixture(body, "bot", fixtureDef, userData, UserData.Edge.bot, base * scale);
            bodyEditorLoader.attachFixture(body, "right", fixtureDef, userData, UserData.Edge.right, base * scale);
        } else if(userData.triangle == UserData.Triangle.TopRight) {
            bodyEditorLoader.attachFixture(body, "top", fixtureDef, userData, UserData.Edge.top, base * scale);
            bodyEditorLoader.attachFixture(body, "right", fixtureDef, userData, UserData.Edge.right, base * scale);
        }
    }
}