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
    Style style;

    /**
     * The type of the Box.
     */
    public enum Style {
        blue,
        green,
        cyan,
        magenta,
        yellow,
        goal,
        gUp,
        gDown,
        gLeft,
        gRight,
        gAll,    // TODO create "All" gravity box image/logic
        rotate,  // TODO create rotate box ??
        generic, // TODO create generic box image/logic
        noBox
    }

    /**
     * Creates a new Box centered at (x,y) with at specified Box Type.
     * @param x The x position of the center of the Box.
     * @param y The y position of the center of the Box.
     * @param style The Box's style.
     */
    Box(float x, float y, float scale, Style style) {
        this.style = style;
        String imagePath = "art/tiles/" + style + ".png";

        sprite = new Sprite(new Texture(imagePath));
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

        // TODO: I think we actually want _all_ Boxes to be of the edge fixture type so
        // todo: we don't have to base last note times on a counter.
        // Load the edges.json file to get all of the edge types (top, bot, left, right)
        // This is so we can specify what is the top of the box if we needs
        FileHandle fileHandle = Gdx.files.internal("json/edges.json");
        float base = 1.2f; // MAGIC NUMBERS AGAIN I'M SO SORRY......
        // NOT JUST A MAGIC NUMBER!! When these fixtures are created, they are made at a scale
        // of 100 pixels. Because all of the box tiles are 120px the box fixtures need to be scaled
        // up to 1.2f in order for them to fit!
        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(fileHandle, imagePath);
        bodyEditorLoader.attachFixture(body, "top", fixtureDef,
            new UserData(style, UserData.Edge.top), base * scale);
        bodyEditorLoader.attachFixture(body, "bot", fixtureDef,
            new UserData(style, UserData.Edge.bot), base * scale);
        bodyEditorLoader.attachFixture(body, "left", fixtureDef,
            new UserData(style, UserData.Edge.left), base * scale);
        bodyEditorLoader.attachFixture(body, "right", fixtureDef,
            new UserData(style, UserData.Edge.right), base * scale);

    }
}