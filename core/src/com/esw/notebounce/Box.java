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
    Vector2 center;
    UserData userData = new UserData(UserData.Type.box);

    /**
     * Creates a new Box centered at (x,y) with at specified Box Type and no modifiers.
     * @param x The x position of the center of the Box.
     * @param y The y position of the center of the Box.
     * @param color The Box's color.
     */
    Box(float x, float y, float scale, UserData.Color color) {
        userData.color = color;
        create(x, y, scale);
    }

    /**
     * Creates a new Box centered at (x,y) with at specified Box Type and no modifiers.
     * @param x The x position of the center of the Box.
     * @param y The y position of the center of the Box.
     * @param color The Box's color.
     */
    Box(float x, float y, float scale, UserData.Color color, UserData.Shade shade) {
        userData.color = color;
        userData.shade = shade;
        create(x, y, scale);
    }

    Box(float x, float y, float scale, UserData.Color color, UserData.Modifier modifier) {
        userData.color = color;
        userData.modifiers.add(modifier);
        create(x, y, scale);
    }

    /**
     * Creates a new Box centered at (x,y) with at specified Box Type with the specified modifiers.
     * @param x The x position of the center of the Box.
     * @param y The y position of the center of the Box.
     *
     */
    Box(float x, float y, float scale, UserData userData) {
        this.userData = userData;
        create(x, y, scale);
    }

    private void create(float x, float y, float scale) {

        // Example blue0.png. Call ordinal on shade because we cannot have ints;
        FileHandle image = Gdx.files.internal("art/tiles/boxes/" + userData.color +
            userData.shade.ordinal() + ".png");
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

        // Load the edges.json file to get all of the edge types (top, bot, left, right)
        // This is so we can specify what is the top of the box if we needs
        FileHandle fileHandle = Gdx.files.internal("fixtures/boxes.json");
        float base = 0.0f;
        if(sprite.getWidth() == sprite.getHeight()) base = (sprite.getHeight() / 100);

        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(fileHandle);
        bodyEditorLoader.attachFixture(body, "top", fixtureDef, userData, UserData.Edge.top, base * scale);
        bodyEditorLoader.attachFixture(body, "bot", fixtureDef, userData, UserData.Edge.bot, base * scale);
        bodyEditorLoader.attachFixture(body, "left", fixtureDef, userData, UserData.Edge.left, base * scale);
        bodyEditorLoader.attachFixture(body, "right", fixtureDef, userData, UserData.Edge.right, base * scale);

    }
}