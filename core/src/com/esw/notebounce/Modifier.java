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
 * Created by Alex on 10/30/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class Modifier {

    Vector2 center = new Vector2(0,0);
    Sprite sprite;
    Body body;
    float scale;
    UserData.ModifierType modifier;
    UserData.Edge edge;

    public Modifier(Vector2 v, float scale, UserData.ModifierType modifier, UserData.Edge edge) {
        this.scale = scale;
        this.modifier = modifier;
        this.edge = edge;

        sprite = new Sprite(new Texture(Gdx.files.internal("art/" + modifier + edge + ".png")));
        sprite.setPosition(v.x, v.y);

        center.x = sprite.getX() + ((sprite.getWidth() / 2) * scale);
        center.y = sprite.getY() + ((sprite.getHeight() / 2) * scale);

        setFixture();
    }

    private void setFixture() {

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

        float base = (sprite.getHeight() / 100);

        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(Gdx.files.internal("fixtures/edges.json"));

        // Create the large fixture that makes up the backside of the tile
        UserData backsideData = new UserData(UserData.Type.modifier);
        backsideData.modifier = UserData.ModifierType.none;
        bodyEditorLoader.attachFixture(body, edge.name(), fixtureDef, base * scale, backsideData, edge);

        // Create the small "active" edge of the fixture that will do the actual modification
        UserData modifierData = new UserData(UserData.Type.modifier);
        modifierData.modifier = modifier;
        bodyEditorLoader.attachFixture(body, edge.name() + "active", fixtureDef, base * scale, modifierData, edge);
    }

    public void setPos(Vector2 v) {
        sprite.setPosition(v.x, v.y);
        center.x = sprite.getX() + ((sprite.getWidth() / 2) * scale);
        center.y = sprite.getY() + ((sprite.getHeight() / 2) * scale);
        body.setTransform(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS, 0.0f);
    }

    public void setModifier(UserData.ModifierType modifier) {
        this.modifier = modifier;
        setFixture();
        Vector2 v = new Vector2(0,0);
        if(sprite != null) {
            v = new Vector2(sprite.getX(), sprite.getY());
            sprite.getTexture().dispose();
            sprite = null;
        }
        sprite = new Sprite(new Texture(Gdx.files.internal("art/" + modifier + edge + ".png")));
        sprite.setScale(scale);
        setPos(v);
    }

    public void setEdge(UserData.Edge edge) {
        this.edge = edge;
        setFixture();
        Vector2 v = new Vector2(0,0);
        if(sprite != null) {
            v = new Vector2(sprite.getX(), sprite.getY());
            sprite.getTexture().dispose();
            sprite = null;
        }
        sprite = new Sprite(new Texture(Gdx.files.internal("art/" + modifier + edge + ".png")));
        sprite.setScale(scale);
        setPos(v);
    }

    public String toJson() {
        String s = "\t\t{\n";
        s += "\t\t\t\"x\":" + sprite.getX() + ",\n";
        s += "\t\t\t\"y\":" + sprite.getY() + ",\n";
        s += "\t\t\t\"modifier\":\"" + modifier + "\",\n";
        s += "\t\t\t\"edge\":\"" + edge + "\",\n";
        s += "\n\t\t}";
        return s;
    }

}
