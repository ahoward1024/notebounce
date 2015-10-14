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
public class LevelLoader { // TODO Level loader/writer

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

    public static void saveLevel(String levelname) {

        String string = "{\n";
        string += "\t{\"boxes\":\n";
        string += "\t[\n";
        for(Box b : NoteBounce.boxes) {
            if(b != null) string += b.toString();
        }
        string += "\t]}\n";

        string += "\t{\"triangles\":\n";
        string += "\t[\n";
        for(Triangle t : NoteBounce.triangles) {
            if(t != null) string += t.toString();
        }
        string += "\t]}\n";



        string += "}\n";
        System.out.println(string);
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
