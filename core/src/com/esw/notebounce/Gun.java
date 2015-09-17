package com.esw.notebounce;

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
 */
@SuppressWarnings("unused")
public class Gun {

    private Vector2 center;
    private Sprite sprite;
    private Vector2 gunEnd;
    Body body;

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
    Gun(float x, float y, float scale) {
        create(x, y, scale);
    }

    /**
     * Creates method to make a gun at (x, y) with a scale of: scale.
     * @param x The x position of the center of the gun.
     * @param y The y position of the center of the gun.
     */
    private void create(float x, float y, float scale) {
        sprite = new Sprite(new Texture("gun.png"));
        sprite.setCenter(x, y);
        sprite.setOriginCenter();
        sprite.setScale(scale);

        center = new Vector2(sprite.getX() + sprite.getWidth() / 2,
                sprite.getY() + sprite.getHeight() / 2);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(sprite.getX() / NoteBounce.PIXELS2METERS, sprite().getY() / NoteBounce.PIXELS2METERS);

        body = NoteBounce.getWorld().createBody(bodyDef);

        FileHandle fileHandle = new FileHandle("gun.json");
        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(fileHandle);
        FixtureDef fixtureDef = new FixtureDef();
        bodyEditorLoader.attachFixture(body, "gun", fixtureDef, 2.4f);
    }

    /**
     * Gets the sprite of the gun.
     * @return The sprite of the gun.
     */
    public Sprite sprite() {
        return sprite;
    }

    /**
     * Gets the center x of the gun relative to the screen it is in.
     * @return The x position of the center of the gun relative to the screen it is in.
     */
    public float getCenterX() {
        return center.x;
    }

    /**
     * Gets the center y of the gun relative to the screen it is in.
     * @return The y position of the center of the gun relative to the screen it is in.
     */
    public float getCenterY() {
        return center.y;
    }

    /**
     * Gets the center Vector2 of the gun relative to the screen it is in.
     * @return The Vector2 position of the center of the gun relative to the screen it is in.
     */
    public Vector2 getCenter() { return center; }

    public float endX(float angle) {
        return (float)(getCenterX()+((sprite.getHeight() / 2) * Math.cos(angle * Math.PI / 180)));
    }

    public float endY(float angle) {
        return (float)(getCenterY()+((sprite.getWidth() / 2) * Math.sin(angle * Math.PI / 180)));
    }

    public Vector2 end(float angle) {
        return new Vector2(endX(angle), endY(angle));
    }

    public void rotate(float angle) {
        sprite.setRotation(angle);
        body.setTransform(body.getPosition(), angle / NoteBounce.PIXELS2METERS);

    }
}
