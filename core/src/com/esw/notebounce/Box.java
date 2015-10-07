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
 * Created by Alex on 9/20/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class Box {

    Sprite sprite;
    Body body;
    Vector2 center = new Vector2(0,0);
    UserData userData = new UserData(UserData.Type.box);
    float scale;
    float alpha;
    Sprite[] modifierSprites = new Sprite[5];

    Box(Vector2 v, float scale, UserData.Color color, UserData.Shade shade, float alpha) {
        userData.color = color;
        userData.shade = shade;
        this.scale = scale;
        this.alpha = alpha;

        // Example blue0.png. Call ordinal on shade because we cannot have ints;
        FileHandle image = Gdx.files.internal("art/tiles/boxes/" + userData.color +
            userData.shade.ordinal() + ".png");
        sprite = new Sprite(new Texture(image));
        sprite.setOrigin(0.0f, 0.0f);
        sprite.setScale(scale);
        sprite.setAlpha(alpha);
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

        // Load the edges.json file to get all of the edge types (top, bot, left, right)
        // This is so we can specify what is the top of the tmpbox if we needs
        FileHandle fileHandle = Gdx.files.internal("fixtures/boxes.json");
        float base = 0.0f;
        if(sprite.getWidth() == sprite.getHeight()) base = (sprite.getHeight() / 100);

        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(fileHandle);
        bodyEditorLoader.attachFixture(body, "top", fixtureDef, userData, UserData.Edge.top, base * scale);
        bodyEditorLoader.attachFixture(body, "bot", fixtureDef, userData, UserData.Edge.bot, base * scale);
        bodyEditorLoader.attachFixture(body, "left", fixtureDef, userData, UserData.Edge.left, base * scale);
        bodyEditorLoader.attachFixture(body, "right", fixtureDef, userData, UserData.Edge.right, base * scale);
    }

    public void update(Vector2 v, float scale, UserData.Color color, UserData.Shade shade, float alpha) {
        userData.color = color;
        userData.shade = shade;

        sprite.getTexture().dispose();
        sprite.setTexture(new Texture("art/tiles/boxes/" + color + shade.ordinal() + ".png"));
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

        for(int i = 0; i < modifierSprites.length; i++) {
            if(modifierSprites[i] != null) {
                modifierSprites[i].setOrigin(0.0f, 0.0f);
                modifierSprites[i].setScale(scale);
                modifierSprites[i].setAlpha(alpha);
                modifierSprites[i].setPosition(sprite.getX(), sprite.getY());
            }
        }
    }
}