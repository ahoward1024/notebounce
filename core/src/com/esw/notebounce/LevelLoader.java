package com.esw.notebounce;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Created by Alex on 9/21/2015.
 * Copyright echsoftworks 2015
 */
@SuppressWarnings("unused")
public class LevelLoader { // TODO Level loader/writer

    static Array<Level> levels = new Array<Level>();
    static int levelPtr = 0;

    LevelLoader(String path) {
        createLevelsArray(path);
    }

    public static void unloadLevel() {

        for(Box o : NoteBounce.boxes) {
            NoteBounce.world.destroyBody(o.body);
            o.sprite.getTexture().dispose();
        }
        NoteBounce.boxes.clear();
        for(Triangle o : NoteBounce.triangles) {
            NoteBounce.world.destroyBody(o.body);
            o.sprite.getTexture().dispose();
        }
        NoteBounce.triangles.clear();
        for(Goal o : NoteBounce.goals) {
            NoteBounce.world.destroyBody(o.body);
            o.sprite.getTexture().dispose();
        }
        NoteBounce.goals.clear();
        for(Door o : NoteBounce.doors) {
            NoteBounce.world.destroyBody(o.body);
            o.sprite.getTexture().dispose();
        }
        NoteBounce.doors.clear();
        for(DoorSwitch o : NoteBounce.switches) {
            NoteBounce.world.destroyBody(o.body);
            o.sprite.getTexture().dispose();
        }
        NoteBounce.switches.clear();
        for(Mine o : NoteBounce.mines) {
            NoteBounce.world.destroyBody(o.body);
            o.sprite.getTexture().dispose();
        }
        NoteBounce.mines.clear();
        for(Gun o : NoteBounce.guns) {
            NoteBounce.world.destroyBody(o.body);
            o.sprite.getTexture().dispose();
        }
        for(int i = 0; i < NoteBounce.guns.length; i++) {
            NoteBounce.guns[i] = null;
        }
        NoteBounce.world.destroyBody(NoteBounce.ball.body);
        NoteBounce.ball.sprite.getTexture().dispose();
        NoteBounce.ball = null;
    }

    // TODO: load a level
    public static Level loadLevel(int lvl) {
        levelPtr = lvl;
        Level level = levels.get(levelPtr);

        JsonValue json = new JsonReader().parse(level.file);
        JsonValue array = json.get("boxes");
        for(JsonValue jv : array.iterator()) {
            Vector2 v = new Vector2(0,0);
            v.x = (jv.getFloat("x") + NoteBounce.bufferWidth);
            v.y = (jv.getFloat("y") + NoteBounce.bufferHeight);
            UserData.Color color = UserData.Color.valueOf(jv.getString("color"));
            UserData.Shade shade = UserData.Shade.valueOf(jv.getString("shade"));
            boolean g = jv.getBoolean("gravity");
            String[] strings = new String[4];
            for(int i = 0; i < strings.length; i++) {
                strings[i] = jv.getString("m" + i);
            }
            Box b = new Box(v, NoteBounce.scalePercent, color, shade, g, strings);
            NoteBounce.boxes.add(b);
        }

        return null;
    }

    public static void createLevelsArray(String path) {
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
        levelPtr = 0;
    }

    public static void saveLevel(String levelname) {

        if(Edit.startgun == -1) { System.out.println("Error! No starting gun!"); return; }

        // TODO MOVE
        boolean save = false;
        FileHandle fileHandle = new FileHandle("levels/" + levelname + ".json");
        if(fileHandle.exists()) {
            JFrame jFrame = new JFrame("Overwrite");
            int ov = JOptionPane.showConfirmDialog(jFrame, "Level exists. Overwrite?", "Overwrite file",
                JOptionPane.OK_CANCEL_OPTION);
            if(ov == 0) {
                System.out.println("Saving: levels/" + levelname + ".json");
                String string = "{\n";

                // BOXES
                string += "\t\"boxes\":[\n";
                for(int i = 0; i < NoteBounce.boxes.size; i++) {
                    Box o = NoteBounce.boxes.get(i);
                    if(o != null) {
                        string += o.toString();
                        if(i != NoteBounce.boxes.size - 1) string += ",\n";
                        else string += "\n";
                    }
                }
                string += "\t],\n";
                //=======================================================================================

               /* // TRIANGLES
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
                //=======================================================================================

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
                //=======================================================================================


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
                //=======================================================================================

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
                //=======================================================================================

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
                //=======================================================================================

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
                //=======================================================================================
                */
                string += "\t\"startgun\":" + Edit.startgun + "\n";

                string += "}\n";
                fileHandle.writeString(string, false);
            }
        }
    }

    public static void loadNextLevel() {
        unloadLevel();
        levelPtr++;
        loadLevel(levelPtr);
    }
}
