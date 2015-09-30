package com.esw.notebounce;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

/**
 * Created by Alex on 9/21/2015.
 * Copyright echsoftworks 2015
 */
@SuppressWarnings("unused")
public class LevelLoader {

    Array<Level> levels = new Array<Level>();
    int lvlPtr = -1;

    LevelLoader(String path) {
        loadAllLevels(path);
    }

    private void unloadLevel() {
        Level level = levels.get(lvlPtr);
        for(Box box : level.getBoxesArray()) {
            box.body.getFixtureList().removeAll(box.body.getFixtureList(), true);
            box.sprite.getTexture().dispose();
        }
    }
    private Level loadLevel(int level) {
        lvlPtr = level;
        // TODO 1 json reader
        JsonReader jsonReader = new JsonReader();
        // TODO 2 push all bodies/fixtures to the world
        // TODO 3 push all the sprites to the renderer
        return null;
    }

    public void loadAllLevels(String path) {
        FileHandle folder = new FileHandle(path);
        FileHandle[] fileList = folder.list();
        if(fileList.length > 1) {
            for (int i = 0; i < fileList.length; i++) {
                if (!fileList[i].isDirectory()) {
                    System.out.println("Loading file: " + fileList[i].path());
                    levels.add(new Level((new FileHandle(fileList[i].path())),
                            fileList[i].name(), i));
                }
            }
        }
        lvlPtr = 0;
    }

    public void experimental() {
        Ball theball = new Ball(10, 10, 1);

        FileHandle output = new FileHandle("levels/test.json");
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.addClassTag("ball", Ball.class);
        json.setElementType(Ball.class, "center", Vector2.class);
        json.setSerializer(Ball.class, new Json.Serializer<Ball>() {
            public void write(Json json, Ball ball, Class knownType) {
                json.writeObjectStart();
                json.writeValue("center", ball.getCenter());
                json.writeObjectEnd();
            }

            public Ball read(Json json, JsonValue jsonValue, Class type) {
                return null;
            }
        });
        System.out.println(json.prettyPrint(theball));
        json.toJson(theball, output);
    }

    public Level loadNextLevel() {
        unloadLevel();
        lvlPtr++;
        loadLevel(lvlPtr);
        // Load the level
        return null;
    }

    public int currentLevel() {
        return -1;
    }

    public String currentLevelName() {
        return null;
    }
}
