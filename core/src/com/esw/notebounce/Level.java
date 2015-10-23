package com.esw.notebounce;

import com.badlogic.gdx.files.FileHandle;

/**
 * Created by Alex on 9/21/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class Level {

    FileHandle file;
    String name;

    Level(FileHandle file) {
        this.file = file;
        this.name = file.nameWithoutExtension();
    }

}
