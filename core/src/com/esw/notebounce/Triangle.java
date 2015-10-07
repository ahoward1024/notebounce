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
    UserData userData = new UserData(UserData.Type.triangle);
    float scale;
    float alpha;
    Sprite[] modifierSprites = new Sprite[4];

    Triangle(UserData.Triangle triangle, Vector2 v, float scale,
             UserData.Color color, UserData.Shade shade, float alpha)
    {
        userData.color = color;
        userData.shade = shade;
        userData.triangle = triangle;
        this.scale = scale;
        this.alpha = alpha;

        loadSprite(v);
        loadFixture();
    }

    private void loadSprite(Vector2 v) {
        FileHandle image = Gdx.files.internal("art/tiles/triangles/" + userData.color +
            userData.triangle + userData.shade.ordinal() + ".png");
        sprite = new Sprite(new Texture(image));
        sprite.setOrigin(0.0f, 0.0f);
        sprite.setScale(scale);
        sprite.setAlpha(alpha);
        sprite.setPosition(v.x, v.y);

        center.x = (sprite.getX() + ((sprite.getWidth() / 2) * scale));
        center.y = (sprite.getY() + ((sprite.getHeight() / 2) * scale));
    }

    private void loadFixture() {
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

    public void update(Vector2 v, float scale, UserData.Triangle triangle,
                       UserData.Color color, UserData.Shade shade, float alpha) {
        sprite.getTexture().dispose();
        NoteBounce.world.destroyBody(body); // NOTE: we must destroy the body so we can create the new one

        userData.color = color;
        userData.shade = shade;
        userData.triangle = triangle;
        this.scale = scale;
        this.alpha = alpha;

        loadSprite(v);
        loadFixture();
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
                s.setAlpha(alpha);
                s.setPosition(sprite.getX(), sprite.getY());
            }
        }
    }
}