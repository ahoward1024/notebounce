package com.esw.notebounce;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by Alex on 9/20/2015.
 */
public class Box {

    Sprite sprite;
    Body body;
    Vector2 center;
    Type type;
    FixType fixType;

    /**
     * The type of the Box.
     */
    public enum Type {
        blue,
        green,
        cyan,
        magenta,
        yellow,
        goal,
        gUp,
        gDown,
        gLeft,
        gRight,
        gAll,
        rotate,
        box
    }

    /**
     * The fixture type of the Box
     */
    enum FixType {
        box,
        edges
    }

    /**
     * Creates a new Box centered at (x,y) with at specified Box Type.
     * @param x The x position of the center of the Box.
     * @param y The y position of the center of the Box.
     * @param type The Box's type.
     */
    Box(float x, float y, Type type) {
        this.type = type;
        String imagePath;
        String fixname;

        switch(type) {
            case blue: {
                imagePath = "art/tiles/blue.png";
                fixname = "blue";
                fixType = FixType.box;
            } break;
            case green: {
                imagePath = "art/tiles/green.png";
                fixname = "green";
                fixType = FixType.box;
            } break;
            case cyan: {
                imagePath = "art/tiles/cyan.png";
                fixname = "cyan";
                fixType = FixType.edges;
            } break;
            case magenta: {
                imagePath = "art/tiles/magenta.png";
                fixname = "magenta";
                fixType = FixType.edges;
            } break;
            case yellow: {
                imagePath = "art/tiles/yellow.png";
                fixname = "yellow";
                fixType = FixType.edges;
            } break;
            case goal: {
                imagePath = "art/tiles/goal.png";
                fixname = "goal";
                fixType = FixType.box;
            } break;
            case gUp: {
                imagePath = "art/tiles/gUp.png";
                fixname = "gUp";
                fixType = FixType.box;
            } break;
            case gDown: {
                imagePath = "art/tiles/gDown.png";
                fixname = "gDown";
                fixType = FixType.box;
            } break;
            case gLeft: {
                imagePath = "art/tiles/gLeft.png";
                fixname = "gLeft";
                fixType = FixType.box;
            } break;
            case gRight: {
                imagePath = "art/tiles/gRight.png";
                fixname = "gRight";
                fixType = FixType.box;
            } break;
            case gAll: {
                imagePath = "art/tiles/gAll.png"; // TODO create an "All" gravity box
                fixname = "gAll";
                fixType = FixType.edges;
            } break;
            case rotate: {
                imagePath = "art/tiles/rotate.png"; // TODO rotation?
                fixname = "rotate";
                fixType = FixType.edges;
            } break;
            case box:
            default: {
                imagePath = "art/tiles/box.png";
                fixname = "box";
                fixType = FixType.box;
            }
        }

        sprite = new Sprite(new Texture(imagePath));
        sprite.setCenter(x, y);
        sprite.setOriginCenter();

        center = new Vector2(sprite.getX() + (sprite.getWidth() / 2),
            sprite.getY() + (sprite.getHeight() / 2));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(center.x / NoteBounce.PIXELS2METERS, center.y / NoteBounce.PIXELS2METERS);

        body = NoteBounce.getWorld().createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.0f;

        // TODO: I think we actually want _all_ Boxes to be of the edge fixture type so we can
        // todo: we don't have to base last note times on a counter.
        if(fixType == FixType.edges) {
            // Load the edges.json file to get all of the edge types (top, bot, left, right)
            // This is so we can specify what is the top of the box if we needs
            FileHandle fileHandle = new FileHandle("json/edges.json");
            float scale = 1.2f; // MAGIC NUMBERS AGAIN I'M SO SORRY......
            BodyEditorLoader bodyEditorLoader = new BodyEditorLoader(fileHandle, imagePath);
            bodyEditorLoader.attachFixture(body, "top", "top" + fixname, fixtureDef, scale);
            bodyEditorLoader.attachFixture(body, "right", fixname, fixtureDef, scale);
            bodyEditorLoader.attachFixture(body, "bot", fixname, fixtureDef, scale);
            bodyEditorLoader.attachFixture(body, "left", fixname, fixtureDef, scale);
        } else {
            // Otherwise we just make a box shape
            PolygonShape shape = new PolygonShape();
            shape.setAsBox((sprite.getWidth() / 2) / NoteBounce.PIXELS2METERS,
                (sprite.getHeight() / 2) / NoteBounce.PIXELS2METERS);
            fixtureDef.shape = shape;

            body.createFixture(fixtureDef);

            shape.dispose();
        }


    }
}
