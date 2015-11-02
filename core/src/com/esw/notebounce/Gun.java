package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
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
     * clamp the angle of the gun based on the position of the gun
     * @param angle the current angle the gun is pointing
     * @return float - the newly clamped angle the gun will rotate by
     */
    public float clampAngle(float angle){
        //currently not a switch because Vector2 is not a valid type, however I'm sure there is
        //a way around this -can be changed in future

        //clamp for position one
        if (position.equals(GunPosition.one)) {
                angle = MathUtils.clamp(angle,0,90);
            }

        //clamp for position two
        else if(position.equals(GunPosition.two)){
            angle = MathUtils.clamp(angle,0,180);
        }

        //clamp for position three
        else if(position.equals(GunPosition.three)) {
            angle = MathUtils.clamp(angle,90,180);
        }

        //clamp for position four
       else if (position.equals(GunPosition.four)) {
            if (angle < 180) {
                angle = MathUtils.clamp(angle, 0, 90);
            } else {
                angle = MathUtils.clamp(angle, 270, 360);
            }
        }

        //clamp for position 6
        else if (position.equals(GunPosition.six)) {
            angle = MathUtils.clamp(angle, 90,270);
        }

        //clamp for position 7
        else if (position.equals(GunPosition.seven)) {
            //0 is directly to the right, so the gun should be able to hit 0
            if (angle > 0) {
                angle = MathUtils.clamp(angle, 270, 360);
            }
        }

        //clamp for position eight
        else if (position.equals(GunPosition.eight)) {
            if(angle > 0) {
                angle = MathUtils.clamp(angle, 180, 360);
            }
        }

        //clamp for position nine
        else if (position.equals(GunPosition.nine)){
            angle = MathUtils.clamp(angle,180,270);
        }

        return angle;
    }
    /**
     * Rotate the gun and it's fixture to the specified angle.
     * @param angle The angle the gun needs to be rotated to.
     */
    public void rotate(float angle) {
        sprite.setRotation(clampAngle(angle));
        body.setTransform(body.getPosition(), (angle / NoteBounce.PIXELS2METERS) * 1.75f);
        // WARNING: 1.75f is a magical number!!! DO NOT CHANGE IT. I can't explain this one...
    }
    // TODO(frankie): clamp gun's rotation values

    @Override
    public String toString() {
        String s = "\t\t{\n";
        s += "\t\t\t\"x\":" + position.x + ",\n";
        s += "\t\t\t\"y\":" + position.y + ",\n";
        s += "\t\t\t\"id\":" + id + "\n";
        s += "\t\t}";
        return s;
    }
}
