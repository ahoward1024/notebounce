package com.esw.notebounce;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Alex on 9/1/2015.
 */
public class Gun {

    // This is REALLY convenient! Why doesn't LibGDX already do this for sprites??
    Vector2 center;

    Sprite sprite;

    Gun(float x, float y, float scale) {
        sprite = new Sprite(new Texture("gun.png"));
        sprite.setCenter(x, y);
        sprite.setOriginCenter();
        sprite.setScale(scale);

        center = new Vector2(sprite.getX() + sprite.getWidth() / 2,
                             sprite.getY() + sprite.getHeight() / 2);
    }

    public Sprite sprite() {
        return sprite;
    }

    public float getCenterX() {
        return center.x;
    }

    public float getCenterY() {
        return center.y;
    }
}
