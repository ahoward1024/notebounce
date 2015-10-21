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
    Sprite[] modifierSprites = new Sprite[4];
    String[] modifierStrings = new String[4];
    UserData.Color color;
    UserData.Shade shade;
    UserData.Triangle triangle;

    Triangle(Vector2 v, UserData.Triangle triangle, float scale,
             UserData.Color color, UserData.Shade shade, String[] mods) {
        this.triangle = triangle;
        this.color = color;
        this.shade = shade;
        this.scale = scale;

        loadSprite(v);

        for(int i = 0; i < mods.length; i++) {
            if(!mods[i].equals("none")) {
                modifierSprites[i] = new Sprite(new Texture("art/modifiers/" + mods[i] + ".png"));
                modifierSprites[i].setOrigin(0.0f, 0.0f);
                modifierSprites[i].setScale(scale);
                modifierSprites[i].setPosition(sprite.getX(), sprite.getY());
            }
        }

        UserData.Modifier[] modifiers = UserData.createModifierArray();
        for(int i = 0; i < mods.length; i++) {
            if(!mods[i].equals("none")) {
                char c = mods[i].charAt(mods[i].length() - 1);
                if(c == 'X') modifiers[i] = UserData.Modifier.dampener;
                else modifiers[i] = UserData.Modifier.accelerator;
            }
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS);

        body = NoteBounce.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.0f;

        UserData userData = new UserData(UserData.Type.triangle);
        userData.color = color;
        userData.shade = shade;
        FileHandle trianglesFile = Gdx.files.internal("fixtures/triangle" + triangle + ".json");

        float base = 0.0f;
        if(sprite.getWidth() == sprite.getHeight()) base = (sprite.getHeight() / 100);
        float basescale = base * scale;

        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(trianglesFile);
        bodyEditorLoader.attachFixture(body, "hyp", fixtureDef, base * scale,
            userData, UserData.Edge.hyp, UserData.Modifier.none);

        if(triangle == UserData.Triangle.BotLeft) {
            bodyEditorLoader.attachFixture(body, "bot", fixtureDef, basescale,
                userData, UserData.Edge.bot, modifiers[1]);
            bodyEditorLoader.attachFixture(body, "left", fixtureDef, basescale,
                userData, UserData.Edge.left, modifiers[2]);
        } else if(triangle == UserData.Triangle.TopLeft) {
            bodyEditorLoader.attachFixture(body, "top", fixtureDef, basescale,
                userData, UserData.Edge.top, modifiers[0]);
            bodyEditorLoader.attachFixture(body, "left", fixtureDef, basescale,
                userData, UserData.Edge.left, modifiers[2]);
        } else if(triangle == UserData.Triangle.BotRight) {
            bodyEditorLoader.attachFixture(body, "bot", fixtureDef, basescale,
                userData, UserData.Edge.bot, modifiers[1]);
            bodyEditorLoader.attachFixture(body, "right", fixtureDef, basescale,
                userData, UserData.Edge.right, modifiers[3]);
        } else if(triangle == UserData.Triangle.TopRight) {
            bodyEditorLoader.attachFixture(body, "top", fixtureDef, basescale,
                userData, UserData.Edge.top, modifiers[0]);
            bodyEditorLoader.attachFixture(body, "right", fixtureDef,basescale,
                userData, UserData.Edge.right, modifiers[3]);
        }
    }

    Triangle(UserData.Triangle triangle, Vector2 v, float scale,
             UserData.Color color, UserData.Shade shade)
    {
        UserData userData = new UserData(UserData.Type.triangle);
        this.color = userData.color = color;
        this.shade = userData.shade = shade;
        this.triangle = userData.triangle = triangle;
        this.scale = scale;

        loadSprite(v);
        loadFixture(userData, UserData.createModifierArray());
    }

    private void loadSprite(Vector2 v) {
        FileHandle image = Gdx.files.internal("art/tiles/triangles/" + color +
            triangle + shade.ordinal() + ".png");
        sprite = new Sprite(new Texture(image));
        sprite.setOrigin(0.0f, 0.0f);
        sprite.setScale(scale);
        sprite.setPosition(v.x, v.y);

        center.x = (sprite.getX() + ((sprite.getWidth() / 2) * scale));
        center.y = (sprite.getY() + ((sprite.getHeight() / 2) * scale));
    }

    private void loadFixture(UserData userData, UserData.Modifier[] modifiers) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS);

        body = NoteBounce.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.0f;

        FileHandle trianglesFile = Gdx.files.internal("fixtures/triangle" + userData.triangle + ".json");

        float base = 0.0f;
        if(sprite.getWidth() == sprite.getHeight()) base = (sprite.getHeight() / 100);
        float basescale = base * scale;

        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(trianglesFile);
       bodyEditorLoader.attachFixture(body, "hyp", fixtureDef, base * scale,
           userData, UserData.Edge.hyp, UserData.Modifier.none);

        if(userData.triangle == UserData.Triangle.BotLeft) {
            bodyEditorLoader.attachFixture(body, "bot", fixtureDef, basescale,
                userData, UserData.Edge.bot, modifiers[1]);
            bodyEditorLoader.attachFixture(body, "left", fixtureDef, basescale,
                userData, UserData.Edge.left, modifiers[2]);
        } else if(userData.triangle == UserData.Triangle.TopLeft) {
            bodyEditorLoader.attachFixture(body, "top", fixtureDef, basescale,
                userData, UserData.Edge.top, modifiers[0]);
            bodyEditorLoader.attachFixture(body, "left", fixtureDef, basescale,
                userData, UserData.Edge.left, modifiers[2]);
        } else if(userData.triangle == UserData.Triangle.BotRight) {
            bodyEditorLoader.attachFixture(body, "bot", fixtureDef, basescale,
                userData, UserData.Edge.bot, modifiers[1]);
            bodyEditorLoader.attachFixture(body, "right", fixtureDef, basescale,
                userData, UserData.Edge.right, modifiers[3]);
        } else if(userData.triangle == UserData.Triangle.TopRight) {
            bodyEditorLoader.attachFixture(body, "top", fixtureDef, basescale,
                userData, UserData.Edge.top, modifiers[0]);
            bodyEditorLoader.attachFixture(body, "right", fixtureDef,basescale,
                userData, UserData.Edge.right, modifiers[3]);
        }
    }

    public void update(Vector2 v, float scale, UserData.Triangle triangle,
                       UserData.Color color, UserData.Shade shade, UserData.Modifier[] modifiers) {
        UserData userData = new UserData(UserData.Type.triangle);
        sprite.getTexture().dispose();
        NoteBounce.world.destroyBody(body); // NOTE: we must destroy the body so we can create the new one

        this.color = userData.color = color;
        this.shade = userData.shade = shade;
        this.triangle = userData.triangle = triangle;
        this.scale = scale;

        loadSprite(v);
        loadFixture(userData, modifiers);
        setPos(v);
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
    }

    @Override
    public String toString() {
        String s = "\t\t{\n";
        s += "\t\t\t\"x\":" + sprite.getX() + ",\n";
        s += "\t\t\t\"y\":" + sprite.getY() + ",\n";
        s += "\t\t\t\"color\":" + "\"" + color + "\",\n";
        s += "\t\t\t\"shade\":\"" + shade + "\",\n";
        s += "\t\t\t\"triangle\":\"" + triangle + "\",\n";
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