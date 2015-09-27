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

    private Vector2 center;
    private Sprite sprite;
    private Vector2 gunEnd;
    Body body;

    public enum Position {
        botleft,
        left,
        topleft,
        bot,
        mid,
        top,
        botright,
        right,
        topright
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

    Gun(Position position) {
        switch(position) {
            case botleft:  break;
            case left:  break;
            case topleft:  break;
            case bot:  break;
            case mid:  break;
            case top:  break;
            case botright:  break;
            case right:  break;
            case topright:  break;
            default:
        }
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

        FileHandle fileHandle = Gdx.files.internal("json/gun.json");
        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(fileHandle, "art/gun.png");
        FixtureDef fixtureDef = new FixtureDef();
        UserData userData = new UserData(UserData.Type.gun);
        bodyEditorLoader.attachFixture(body, "gun", fixtureDef, userData, 2.4f * scale);
        // WARNING: 2.4f is a magical number!!! DO NOT CHANGE IT.
        // NOT JUST A MAGIC NUMBER!! When these fixtures are created, they are made at a scale
        // of 100 pixels. Because the gun is 240px the fixture needs to be scaled up to 2.4f
        // in order for it to fit!
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

    /**
     * Get the x position of the end of the gun based on the angle of the gun.
     * @param angle The current angle the gun is pointing.
     * @return The x position of the end of the gun.
     */
    public float endX(float angle) {
        return (float)(getCenterX()+((sprite.getHeight() / 2) * Math.cos(angle * Math.PI / 180)));
    }

    /**
     * Get the y position of the end of the gun based on the angle of the gun.
     * @param angle The current angle the gun is pointing.
     * @return The y position of the end of the gun.
     */
    public float endY(float angle) {
        return (float)(getCenterY()+((sprite.getWidth() / 2) * Math.sin(angle * Math.PI / 180)));
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
