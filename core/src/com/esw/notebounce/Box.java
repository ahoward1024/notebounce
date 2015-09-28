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
 * Created by Alex on 9/20/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class Box {

    Sprite sprite;
    Body body;
    Vector2 center;
    Style style;
    Array<Modifier> modifiers;

    /**
     * The type of the Box.
     */
    public enum Style {
        blue0,
        blue1,
        blue2,
        blue3,
        blue4,
        blue5,
        blue6,
        blue7,
        blue8,
        blueTriBotLeft,
        blueTriTopLeft,
        blueTriTopRight,
        blueTriBotRight,

        green0,
        green1,
        green2,
        green3,
        green4,
        green5,
        green6,
        green7,
        green8,
        greenTriBotLeft,
        greenTriTopLeft,
        greenTriTopRight,
        greenTriBotRight,

        cyan0,
        cyan1,
        cyan2,
        cyan3,
        cyan4,
        cyan5,
        cyan6,
        cyan7,
        cyan8,
        cyan9,
        cyanTriBotLeft,
        cyanTriTopLeft,
        cyanTriTopRight,
        cyanTriBotRight,

        magenta0,
        magenta1,
        magenta2,
        magenta3,
        magenta4,
        magenta5,
        magenta6,
        magenta7,
        magenta8,
        magentaTriBotLeft,
        magentaTriTopLeft,
        magentaTriTopRight,
        magentaTriBotRight,

        yellow0,
        yellow1,
        yellow2,
        yellow3,
        yellow4,
        yellow5,
        yellow6,
        yellow7,
        yellow8,
        yellowTriBotLeft,
        yellowTriTopLeft,
        yellowTriTopRight,
        yellowTriBotRight,

        goal,
        gUp,
        gDown,
        gLeft,
        gRight,
        gAll,
        noBox
    }

    // TODO modifiers for triangle hypotenuses
    public enum Modifier {
        acceleratorUp,
        acceleratorDown,
        acceleratorLeft,
        acceleratorRight,
        acceleratorAll,

        dampenerUp,
        dampenerDown,
        dampenerLeft,
        dampenerRight,
        dampenerAll,

        noModifier
    }

    /**
     * Creates a new Box centered at (x,y) with at specified Box Type and no modifiers.
     * @param x The x position of the center of the Box.
     * @param y The y position of the center of the Box.
     * @param style The Box's style.
     */
    Box(float x, float y, float scale, Style style) {
        this.modifiers = new Array<Modifier>();
        modifiers.add(Modifier.noModifier);
        create(x, y, scale, style, modifiers);
    }

    /**
     * Creates a new Box centered at (x,y) with at specified Box Type with the specified modifiers.
     * @param x The x position of the center of the Box.
     * @param y The y position of the center of the Box.
     * @param style The Box's style.
     * @param modifiers An array of modifiers to be set on the box
     */
    Box(float x, float y, float scale, Style style, Array<Modifier> modifiers) {
        create(x, y, scale, style, modifiers);
    }

    private void create(float x, float y, float scale, Style style, Array<Modifier> modifiers) {
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
        FileHandle fileHandle = Gdx.files.internal("fixtures/edges.json");
        float base = 0.0f;
        if(sprite.getWidth() == sprite.getHeight()) base = (sprite.getHeight() / 100);

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