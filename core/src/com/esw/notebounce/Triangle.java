package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.ArrayList;

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
    Color color;
    Shade shade;
    Array<Modifier> modifiers;

    /**
     * The type of the Box.
     */
    public enum Color {
        blueBotLeft,
        blueTopLeft,
        blueTopRight,
        blueBotRight,

        greenBotLeft,
        greenTopLeft,
        greenTopRight,
        greenBotRight,

        cyanBotLeft,
        cyanTopLeft,
        cyanTopRight,
        cyanBotRight,

        magentaBotLeft,
        magentaTopLeft,
        magentaTopRight,
        magentaBotRight,

        yellowBotLeft,
        yellowTopLeft,
        yellowTopRight,
        yellowBotRight,

        none
    }

    public enum Shade {
        zero,
        one,
        two,
        three,
        four,
        five,
        six,
        seven,
        eight,
    }

    // TODO modifiers for triangle hypotenuses
    public enum Modifier {
        acceleratorUp,
        acceleratorDown,
        acceleratorLeft,
        acceleratorRight,
        acceleratorHyp,

        dampenerUp,
        dampenerDown,
        dampenerLeft,
        dampenerRight,
        dampenerHyp,

        none
    }

    /**
     * Creates a new Box centered at (x,y) with at specified Box Type and no modifiers.
     * @param x The x position of the center of the Box.
     * @param y The y position of the center of the Box.
     * @param color The Box's color.
     */
    Triangle(float x, float y, float scale, Color color) {
        this.modifiers = new Array<Modifier>();
        modifiers.add(Modifier.none);

        create(x, y, scale, color, Shade.zero, modifiers);
    }

    /**
     * Creates a new Box centered at (x,y) with at specified Box Type and no modifiers.
     * @param x The x position of the center of the Box.
     * @param y The y position of the center of the Box.
     * @param color The Box's color.
     */
    Triangle(float x, float y, float scale, Color color, Shade shade) {
        this.modifiers = new Array<Modifier>();
        modifiers.add(Modifier.none);
        create(x, y, scale, color, shade, modifiers);
    }

    /**
     * Creates a new Box centered at (x,y) with at specified Box Type with the specified modifiers.
     * @param x The x position of the center of the Box.
     * @param y The y position of the center of the Box.
     * @param color The Box's color.
     * @param modifiers An array of modifiers to be set on the box
     */
    Triangle(float x, float y, float scale, Color color, Array<Modifier> modifiers) {
        create(x, y, scale, color, Shade.zero, modifiers);
    }

    /**
     * Creates a new Box centered at (x,y) with at specified Box Type with the specified modifiers.
     * @param x The x position of the center of the Box.
     * @param y The y position of the center of the Box.
     * @param color The Box's color.
     * @param modifiers An array of modifiers to be set on the box
     */
    Triangle(float x, float y, float scale, Color color, Shade shade, Array<Modifier> modifiers) {
        create(x, y, scale, color, shade, modifiers);
    }

    private void create(float x, float y, float scale, Color color,
                        Shade shade, Array<Modifier> modifiers) {
        this.color = color;
        this.shade = shade;

        // Example: greenBotLeft8.png.
        FileHandle image = Gdx.files.internal("art/tiles/triangles/" + color + shade.ordinal() + ".png");
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
        FileHandle fileHandle = Gdx.files.internal("fixtures/triangles.json");
        float base = 0.0f;
        if(sprite.getWidth() == sprite.getHeight()) base = (sprite.getHeight() / 100);

        /*BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(fileHandle);
        bodyEditorLoader.attachFixture(body, "top", fixtureDef,
            new UserData(color, UserData.Edge.top), base * scale);
        bodyEditorLoader.attachFixture(body, "bot", fixtureDef,
            new UserData(color, UserData.Edge.bot), base * scale);
        bodyEditorLoader.attachFixture(body, "left", fixtureDef,
            new UserData(color, UserData.Edge.left), base * scale);
        bodyEditorLoader.attachFixture(body, "right", fixtureDef,
            new UserData(color, UserData.Edge.right), base * scale);*/

    }
}