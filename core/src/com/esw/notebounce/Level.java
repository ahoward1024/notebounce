package com.esw.notebounce;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Alex on 9/21/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class Level {

    FileHandle file;
    String name;
    int id;

    Array<Box> boxes = new Array<Box>();
    Vector2 gunPos;
    Vector2 ballPos;

    Level(FileHandle file, String name, int id) {
        this.file = file;
        this.name = name;
        this.id = id;
    }

    public FileHandle getFile() {
        return file;
    }

    public String getPath() {
        return file.path();
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

    public Array<Box> getBoxesArray() {
        return boxes;
    }

    public Vector2 getGunPos() {
        return gunPos;
    }

    public Vector2 getBallPos() {
        return ballPos;
    }

}
