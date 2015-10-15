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

    Vector2 position;
    Vector2 center;
    Sprite sprite;
    Vector2 gunEnd;
    Body body;
    int id;

    Gun(Vector2 position, float scale, int id) {
        this.position = position;
        this.id = id;
        sprite = new Sprite(new Texture("art/gun.png"));
        sprite.setCenter(position.x, position.y);
        sprite.setOriginCenter();
        sprite.setScale(scale);

        center = new Vector2(sprite.getX() + (sprite.getWidth() / 2),
            sprite.getY() + (sprite.getHeight() / 2));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS);

        body = NoteBounce.world.createBody(bodyDef);

        FileHandle fileHandle = Gdx.files.internal("fixtures/gun.json");
        BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(fileHandle);
        FixtureDef fixtureDef = new FixtureDef();
        UserData userData = new UserData(UserData.Type.gun);
        userData.id = id;
        System.out.println("USER DATA ID :" + id);
        float base = 0.0f;
        if(sprite.getWidth() == sprite.getHeight()) base = (sprite.getHeight() / 100);
        bodyEditorLoader.attachFixture(body, "gun", fixtureDef, userData, base * scale);
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
    public void rotate(float angle) { // TODO clamp gun's rotation values
        sprite.setRotation(angle);
        body.setTransform(body.getPosition(), (angle / NoteBounce.PIXELS2METERS) * 1.75f);
        // WARNING: 1.75f is a magical number!!! DO NOT CHANGE IT. I can't explain this one...
    }

    // TODO set/reset the gun's rotation. Set in the same way as the GunPosition

    @Override
    public String toString() {
        String s = "\t\t{\n";
        s += "\t\t\t\"position\":{\"x\":" + sprite.getX() + ",\"y\":" + sprite.getY() + "},\n";
        s += "\t\t\t\"id\":" + id + "\n";
        s += "\t\t}";
        return s;
    }
}
