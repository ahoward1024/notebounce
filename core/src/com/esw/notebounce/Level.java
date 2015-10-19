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

    Ball ball;

    Level(FileHandle file, String name, int id) {
        this.file = file;
        this.name = name;
        this.id = id;
    }

}
