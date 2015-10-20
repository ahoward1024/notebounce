package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.io.Serializable;

import javax.jws.soap.SOAPBinding;

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
    UserData.Shade shade;
    boolean gravity = false;
    Sprite gravitySprite;
    Sprite[] modifierSprites = new Sprite[4];
    String[] modifierStrings = new String[4];

    // TODO Rethink modifiers again.
    // TODO Need a clear goal of what modifiers do.

    Box(Vector2 v, float scale, UserData.Color color, UserData.Shade shade, boolean gravity, String[] mods) {
        UserData userData = new UserData(UserData.Type.box);
        this.color = userData.color = color;
        this.shade = userData.shade = shade;
        this.scale = scale;
        this.gravity = gravity;
        this.modifierStrings = mods;

        // Example blue0.png. Call ordinal on shade because we cannot have ints;
        FileHandle image = Gdx.files.internal("art/tiles/boxes/" + userData.color +
            userData.shade.ordinal() + ".png");
        sprite = new Sprite(new Texture(image));
        gravitySprite = new Sprite(new Texture(Gdx.files.internal("art/modifiers/g.png")));
        sprite.setOrigin(0.0f, 0.0f);
        sprite.setScale(scale);
        sprite.setPosition(v.x, v.y);

        center.x = (sprite.getX() + ((sprite.getWidth() / 2) * scale));
        center.y = (sprite.getY() + ((sprite.getHeight() / 2) * scale));

        if(gravity) {
            gravitySprite.setOrigin(0.0f, 0.0f);
            gravitySprite.setScale(scale);
            gravitySprite.setPosition(sprite.getX(), sprite.getY());
        }

        for(int i = 0; i < mods.length; i++) {
            if(!mods[i].equals("none")) {
                modifierSprites[i] = new Sprite(new Texture("art/modifiers/" + mods[i] + ".png"));
                modifierSprites[i].setOrigin(0.0f, 0.0f);
                modifierSprites[i].setScale(scale);
                modifierSprites[i].setPosition(sprite.getX(), sprite.getY());
            }
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS);

        body = NoteBounce.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.0f;

        UserData.Modifier[] modifiers = UserData.createModifierArray();
        for(int i = 0; i < mods.length; i++) {
            if(!mods[i].equals("none")) {
                char c = mods[i].charAt(mods[i].length() - 1);
                if(gravity) modifiers[i] = UserData.Modifier.gravity;
                else if(c == 'X') modifiers[i] = UserData.Modifier.dampener;
                else modifiers[i] = UserData.Modifier.accelerator;
            }
        }

        // Load the edges.json file to get all of the edge types (top, bot, left, right)
        // This is so we can specify what is the top of the tmpbox if we needs
        float base = 0.0f;
        if(sprite.getWidth() == sprite.getHeight()) base = (sprite.getHeight() / 100);

        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(Gdx.files.internal("fixtures/boxes.json"));
        bodyEditorLoader.attachFixture(body, "top", fixtureDef, base * scale,
            userData, UserData.Edge.top, modifiers[0]);
        bodyEditorLoader.attachFixture(body, "bot", fixtureDef, base * scale,
            userData, UserData.Edge.bot, modifiers[1]);
        bodyEditorLoader.attachFixture(body, "left", fixtureDef, base * scale,
            userData, UserData.Edge.left, modifiers[2]);
        bodyEditorLoader.attachFixture(body, "right", fixtureDef, base * scale,
            userData, UserData.Edge.right, modifiers[3]);
    }

    Box(Vector2 v, float scale, UserData.Color color, UserData.Shade shade) {
        UserData userData = new UserData(UserData.Type.box);
        this.color = userData.color = color;
        this.shade = userData.shade = shade;
        this.scale = scale;

        // Example blue0.png. Call ordinal on shade because we cannot have ints;
        FileHandle image = Gdx.files.internal("art/tiles/boxes/" + userData.color +
            userData.shade.ordinal() + ".png");
        sprite = new Sprite(new Texture(image));
        gravitySprite = new Sprite(new Texture(Gdx.files.internal("art/modifiers/g.png")));
        sprite.setOrigin(0.0f, 0.0f);
        sprite.setScale(scale);
        sprite.setPosition(v.x, v.y);

        center.x = (sprite.getX() + ((sprite.getWidth() / 2) * scale));
        center.y = (sprite.getY() + ((sprite.getHeight() / 2) * scale));

        loadFixtures(userData, UserData.createModifierArray());
    }

    public void update(Vector2 v, UserData.Color color, UserData.Shade shade,
                       UserData.Modifier[] modifiers) {
        UserData userData = new UserData(UserData.Type.box);
        this.color = userData.color = color;
        this.shade = userData.shade = shade;

        sprite.getTexture().dispose();
        sprite.setTexture(new Texture("art/tiles/boxes/" + color + shade.ordinal() + ".png"));
        sprite.setOrigin(0.0f, 0.0f);
        sprite.setScale(scale);

        setPos(v);
        loadFixtures(userData, modifiers);
    }

    public void setPos(Vector2 v) {
        sprite.setPosition(v.x, v.y);
        center.x = (sprite.getX() + ((sprite.getWidth() / 2) * scale));
        center.y = (sprite.getY() + ((sprite.getHeight() / 2) * scale));
        body.setTransform(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS, 0.0f);

        for(Sprite s : modifierSprites) {
            if(s != null) {
                s.setOrigin(0.0f, 0.0f);
                s.setScale(scale);
                s.setPosition(sprite.getX(), sprite.getY());
            }
        }

        if(gravity) {
            gravitySprite.setOrigin(0.0f, 0.0f);
            gravitySprite.setScale(scale);
            gravitySprite.setPosition(sprite.getX(), sprite.getY());
        }
    }

    public void loadFixtures(UserData userData, UserData.Modifier[] modifiers) {
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

        // Load the edges.json file to get all of the edge types (top, bot, left, right)
        // This is so we can specify what is the top of the tmpbox if we needs
        FileHandle fileHandle = Gdx.files.internal("fixtures/boxes.json");
        float base = 0.0f;
        if(sprite.getWidth() == sprite.getHeight()) base = (sprite.getHeight() / 100);

        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(fileHandle);
        bodyEditorLoader.attachFixture(body, "top", fixtureDef, base * scale,
            userData, UserData.Edge.top, modifiers[0]);
        bodyEditorLoader.attachFixture(body, "bot", fixtureDef, base * scale,
            userData, UserData.Edge.bot, modifiers[1]);
        bodyEditorLoader.attachFixture(body, "left", fixtureDef, base * scale,
            userData, UserData.Edge.left, modifiers[2]);
        bodyEditorLoader.attachFixture(body, "right", fixtureDef, base * scale,
            userData, UserData.Edge.right, modifiers[3]);
    }

    @Override
    public String toString() {
        String s = "\t\t{\n";
        s += "\t\t\t\"x\":" + sprite.getX() + ",\n";
        s += "\t\t\t\"y\":" + sprite.getY() + ",\n";
        s += "\t\t\t\"color\":" + "\"" + color + "\",\n";
        s += "\t\t\t\"shade\":\"" + shade + "\",\n";
        s += "\t\t\t\"gravity\":\"" + gravity + "\",\n";
        for(int i = 0; i < modifierStrings.length; i++) {
            s += "\t\t\t\"m" + i + "\":";
            if(modifierStrings[i] != null) s+= "\"" + modifierStrings[i] + "\"";
            else s += "\"none\"";
            if(i != modifierStrings.length - 1) s += ",\n";
        }
        s += "\n\t\t}";
        return s;
    }
}