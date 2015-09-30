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
 * Created by Alex on 9/1/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class Gun {

    Vector2 center;
    Sprite sprite;
    Vector2 gunEnd;
    Body body;

    class GunPosition {
        float padding = 30 * NoteBounce.scalePercent;
        public final Vector2 botLeft = new Vector2(padding, padding);
        public final Vector2 left = new Vector2(padding, NoteBounce.ScreenHeight / 2);
        public final Vector2 topLeft = new Vector2(padding, NoteBounce.ScreenHeight - padding);
        public final Vector2 top = new Vector2(NoteBounce.ScreenWidth / 2, NoteBounce.ScreenHeight - padding);
        public final Vector2 topRight = new Vector2(NoteBounce.ScreenWidth - padding, NoteBounce.ScreenHeight - padding);
        public final Vector2 right = new Vector2(NoteBounce.ScreenWidth - padding, NoteBounce.ScreenHeight / 2);
        public final Vector2 botRight = new Vector2(NoteBounce.ScreenWidth - padding, padding);
        public final Vector2 bot = new Vector2(NoteBounce.ScreenWidth / 2, padding);
        public final Vector2 center = new Vector2(NoteBounce.ScreenWidth / 2, NoteBounce.ScreenHeight / 2);
    }

    /**
     * Calls the create method to make a gun at (x, y) with a scale of 1.
     * @param x The x position of the center of the gun.
     * @param y The y position of the center of the gun.
     */
    Gun(float x, float y) {
        create(x, y, 1.0f);
    }

    /**
     * Calls the create method to make a gun at (x, y) with a scale of: scale.
     * @param x The x position of the center of the gun.
     * @param y The y position of the center of the gun.
     */
    Gun(float x, float y, float scale) { create(x, y, scale); }

    Gun(Vector2 position, float scale) {
        create(position.x, position.y, scale);
    }

    /**
     * Creates method to make a gun at (x, y) with a scale of: scale.
     * @param x The x position of the center of the gun.
     * @param y The y position of the center of the gun.
     */
    private void create(float x, float y, float scale) {
        sprite = new Sprite(new Texture("art/gun.png"));
        sprite.setCenter(x, y);
        sprite.setOriginCenter();
        sprite.setScale(scale);

        center = new Vector2(sprite.getX() + (sprite.getWidth() / 2),
                sprite.getY() + (sprite.getHeight() / 2));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS);

        body = NoteBounce.getWorld().createBody(bodyDef);

        FileHandle fileHandle = Gdx.files.internal("fixtures/gun.json");
        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(fileHandle);
        FixtureDef fixtureDef = new FixtureDef();
        UserData userData = new UserData(UserData.Type.gun);
        float base = 0.0f;
        if(sprite.getWidth() == sprite.getHeight()) base = (sprite.getHeight() / 100);
        bodyEditorLoader.attachFixture(body, "gun", fixtureDef, userData, UserData.Edge.none, base * scale);
    }

    /**
     * Get the x position of the end of the gun based on the angle of the gun.
     * @param angle The current angle the gun is pointing.
     * @return The x position of the end of the gun.
     */
    public float endX(float angle) {
        return (float)(center.x + ((sprite.getHeight() / 2) * Math.cos(angle * Math.PI / 180)));
    }

    /**
     * Get the y position of the end of the gun based on the angle of the gun.
     * @param angle The current angle the gun is pointing.
     * @return The y position of the end of the gun.
     */
    public float endY(float angle) {
        return (float)(center.y + ((sprite.getWidth() / 2) * Math.sin(angle * Math.PI / 180)));
    }

    /**
     * Get the x,y position of the end of the gun based on the angle of the gun as a Vector2.
     * @param angle The current angle the gun is pointing.
     * @return The x,y position of the end of the gun as a Vector2.
     */
    public Vector2 end(float angle) {
        return new Vector2(endX(angle), endY(angle));
    }

    /**
     * Rotate the gun and it's fixture to the specified angle.
     * @param angle The angle the gun needs to be rotated to.
     */
    public void rotate(float angle) {
        sprite.setRotation(angle);
        body.setTransform(body.getPosition(), (angle / NoteBounce.PIXELS2METERS) * 1.75f);
        // WARNING: 1.75f is a magical number!!! DO NOT CHANGE IT. I can't explain this one...

    }
}
