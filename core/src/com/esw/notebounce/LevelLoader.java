package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

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

        if(Edit.startgun == -1) { System.out.println("Error! No starting gun!"); return; }

        String string = "{\n";

        // BOXES
        string += "\t\"boxes\":\n\t[\n";
        for(int i = 0; i < NoteBounce.boxes.size; i++) {
            Box o = NoteBounce.boxes.get(i);
            if(o != null) {
                string += o.toString();
                if(i != NoteBounce.boxes.size - 1) string += ",\n";
                else string += "\n";
            }
        }
        string += "\t],\n";
//=======================================================================================================

        // TRIANGLES
        string += "\t\"triangles\":\n\t[\n";
        for(int i = 0; i < NoteBounce.triangles.size; i++) {
            Triangle o = NoteBounce.triangles.get(i);
            if(o != null) {
                string += o.toString();
                if(i != NoteBounce.triangles.size - 1) string += ",\n";
                else string += "\n";
            }
        }
        string += "\t],\n";
//=======================================================================================================

        // GOALS
        string += "\t\"goals\":\n\t[\n";
        for(int i = 0; i < NoteBounce.goals.size; i++) {
            Goal o = NoteBounce.goals.get(i);
            if(o != null) {
                string += o.toString();
                if(i != NoteBounce.goals.size - 1) string += ",\n";
                else string += "\n";
            }
        }
        string += "\t],\n";
//=======================================================================================================

        // DOORS
        string += "\t\"doors\":\n\t[\n";
        for(int i = 0; i < NoteBounce.doors.size; i++) {
            Door o = NoteBounce.doors.get(i);
            if(o != null) {
                string += o.toString();
                if(i != NoteBounce.doors.size - 1) string += ",\n";
                else string += "\n";
            }
        }
        string += "\t],\n";
//=======================================================================================================

        // SWITCHES
        string += "\t\"switches\":\n\t[\n";
        for(int i = 0; i < NoteBounce.switches.size; i++) {
            DoorSwitch o = NoteBounce.switches.get(i);
            if(o != null) {
                string += o.toString();
                if(i != NoteBounce.switches.size - 1) string += ",\n";
                else string += "\n";
            }
        }
        string += "\t],\n";
//=======================================================================================================

        // MINES
        string += "\t\"mines\":\n\t[\n";
        for(int i = 0; i < NoteBounce.mines.size; i++) {
            Mine o = NoteBounce.mines.get(i);
            if(o != null) {
                string += o.toString();
                if(i != NoteBounce.mines.size - 1) string += ",\n";
                else string += "\n";
            }
        }
        string += "\t],\n";
//=======================================================================================================

        // GUNS
        string += "\t\"guns\":\n\t[\n";
        for(int i = 0; i < NoteBounce.guns.length; i++) {
            Gun o = NoteBounce.guns[i];
            if(o != null) {
                string += o.toString();
                if(i != NoteBounce.guns.length - 1) string += ",\n";
                else string += "\n";
            }
        }
        string += "\t],\n";
//=======================================================================================================

        string += "\t\"startgun\":" + Edit.startgun + "\n";

        string += "}\n";

        // TODO MOVE
        boolean save = false;
        FileHandle fileHandle = new FileHandle("levels/" + "test" + ".json");
        if(fileHandle.exists()) {
            JFrame jFrame = new JFrame("Overwrite");
            int ov = JOptionPane.showConfirmDialog(jFrame, "Level exists. Overwrite?", "Overwrite file", JOptionPane.OK_CANCEL_OPTION);
            if(ov == 0) {
                System.out.println("Saving: levels/" + levelname + ".json");
                fileHandle.writeString(string, false);
            }
        }
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
