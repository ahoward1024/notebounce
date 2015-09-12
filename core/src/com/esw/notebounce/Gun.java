package com.esw.notebounce;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Alex on 9/1/2015.
 */
@SuppressWarnings("unused")
public class Gun {

    private Vector2 center;
    private Sprite sprite;
    private Vector2 gunEnd;

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
}
