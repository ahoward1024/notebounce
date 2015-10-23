package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import javax.swing.JOptionPane;

/**
 * Created by Alex on 9/21/2015.
 * Copyright echsoftworks 2015
 */
@SuppressWarnings("unused")
public class LevelLoader {

    static Array<Level> levels = new Array<Level>();
    static int levelPtr = 0;

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
            if(o != null) {
                NoteBounce.world.destroyBody(o.body);
                o.sprite.getTexture().dispose();
            }
        }
        for(int i = 0; i < NoteBounce.guns.length; i++) {
            NoteBounce.guns[i] = null;
        }
        if(NoteBounce.ball != null) {
            NoteBounce.world.destroyBody(NoteBounce.ball.body);
            NoteBounce.ball.sprite.getTexture().dispose();
            NoteBounce.ball = null;
        }
    }

    public static void loadLevel(int lvl) {
        unloadLevel();
        Level level = levels.get(lvl);

        JsonValue json = new JsonReader().parse(Gdx.files.internal(level.file.path()));

        //BOXES
        JsonValue array = json.get("boxes");
        for(JsonValue jv : array.iterator()) {
            Vector2 v = new Vector2(0,0);
            v.x = (jv.getFloat("x") * NoteBounce.scalePercent) + NoteBounce.bufferWidth;
            v.y = (jv.getFloat("y") * NoteBounce.scalePercent) + NoteBounce.bufferHeight;
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
        //===============================================================================================
        //TRIANGLES
        array = json.get("triangles");
        for(JsonValue jv : array.iterator()) {
            Vector2 v = new Vector2(0, 0);
            v.x = (jv.getFloat("x") * NoteBounce.scalePercent) + NoteBounce.bufferWidth;
            v.y = (jv.getFloat("y") * NoteBounce.scalePercent) + NoteBounce.bufferHeight;
            UserData.Triangle triangle = UserData.Triangle.valueOf(jv.getString("triangle"));
            UserData.Color color = UserData.Color.valueOf(jv.getString("color"));
            UserData.Shade shade = UserData.Shade.valueOf(jv.getString("shade"));
            String[] strings = new String[4];
            for(int i = 0; i < strings.length; i++) {
                strings[i] = jv.getString("m" + i);
            }
            Triangle o = new Triangle(v, triangle, NoteBounce.scalePercent, color, shade, strings);
            NoteBounce.triangles.add(o);
        }
        //===============================================================================================
        //GOALS
        array = json.get("goals");
        for(JsonValue jv : array.iterator()) {
            Vector2 v = new Vector2(0, 0);
            v.x = (jv.getFloat("x") * NoteBounce.scalePercent) + NoteBounce.bufferWidth;
            v.y = (jv.getFloat("y") * NoteBounce.scalePercent) + NoteBounce.bufferHeight;
            Goal o = new Goal(v, NoteBounce.scalePercent);
            NoteBounce.goals.add(o);
        }
        //===============================================================================================
        //DOORS
        array = json.get("doors");
        for(JsonValue jv : array.iterator()) {
            Vector2 v = new Vector2(0, 0);
            v.x = (jv.getFloat("x") * NoteBounce.scalePercent) + NoteBounce.bufferWidth;
            v.y = (jv.getFloat("y") * NoteBounce.scalePercent) + NoteBounce.bufferHeight;
            Door.State state = Door.State.valueOf(jv.getString("state"));
            Door.Plane plane = Door.Plane.valueOf(jv.getString("plane"));
            int id = jv.getInt("id");
            Door o = new Door(v, state, plane, NoteBounce.scalePercent, id);
            NoteBounce.doors.add(o);
        }
        //===============================================================================================
        //SWITCHES
        array = json.get("switches");
        for(JsonValue jv : array.iterator()) {
            Vector2 v = new Vector2(0, 0);
            v.x = (jv.getFloat("x") * NoteBounce.scalePercent) + NoteBounce.bufferWidth;
            v.y = (jv.getFloat("y") * NoteBounce.scalePercent) + NoteBounce.bufferHeight;
            int id = jv.getInt("id");
            DoorSwitch o = new DoorSwitch(v, NoteBounce.scalePercent, id);
            NoteBounce.switches.add(o);
        }
        //===============================================================================================
        //MINES
        array = json.get("mines");
        for(JsonValue jv : array.iterator()) {
            Vector2 v = new Vector2(0, 0);
            v.x = (jv.getFloat("x") * NoteBounce.scalePercent) + NoteBounce.bufferWidth;
            v.y = (jv.getFloat("y") * NoteBounce.scalePercent) + NoteBounce.bufferHeight;
            Mine o = new Mine(v, NoteBounce.scalePercent);
            NoteBounce.mines.add(o);
        }
        //===============================================================================================
        //GUNS
        array = json.get("guns");
        for(JsonValue jv : array.iterator()) {
            Vector2 v = new Vector2(0, 0);
            v.x = (jv.getFloat("x") * NoteBounce.scalePercent) + NoteBounce.bufferWidth;
            v.y = (jv.getFloat("y") * NoteBounce.scalePercent) + NoteBounce.bufferHeight;
            int id = jv.getInt("id");
            NoteBounce.guns[id] = new Gun(v, NoteBounce.scalePercent, id);
        }

        int startgun = json.getInt("startgun");

        if(NoteBounce.guns[startgun] != null) {
            NoteBounce.ball = new Ball(NoteBounce.guns[startgun].center, NoteBounce.scalePercent);
        }
        else {
            NoteBounce.ball = new Ball((float)(NoteBounce.ScreenWidth / 2) + NoteBounce.bufferWidth,
                    (float)(NoteBounce.ScreenHeight / 2) + NoteBounce.bufferHeight, NoteBounce.scalePercent);
        }
        NoteBounce.currentGun = startgun;
    }

    public static void loadLevel(String name) {
        unloadLevel();
        for(int i = 0; i < levels.size; i++) {
            Level l = levels.get(i);
            if(l.name.equals(name)) loadLevel(i);
        }
    }

    public static void createLevelsArray(FileHandle fileHandle) {
        FileHandle[] fileList = fileHandle.list();
        if(fileList.length > 1) {
            for (FileHandle fh : fileList) {
                if (!fh.isDirectory()) {
                    System.out.println("Loading file to array: " + fh.path());
                    levels.add(new Level(new FileHandle(fh.path())));
                }
            }
        }
        levelPtr = 0;
    }

    public static void newLevel() {
        levelPtr++;
        String name = "level" + levelPtr + ".json";
        levels.add(new Level(Gdx.files.internal("levels/" + name)));
    }

    public static void saveLevel(String levelname) {

        if(Edit.startgun == -1) { System.out.println("Error! No starting gun!"); return; }

        FileHandle fileHandle = new FileHandle("levels/" + levelname + ".json");
        System.out.println("Saving: levels/" + levelname + ".json");
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
        //=======================================================================================
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

        string += "\t\"startgun\":" + Edit.startgun + "\n";

        string += "}\n";
        fileHandle.writeString(string, false);

        Level l = new Level(fileHandle);
        levels.add(l);
    }

    public static void loadFirstLevel() {
        levelPtr = 0;
        loadLevel(levelPtr);
    }

    public static void loadPreviousLevel() {
        unloadLevel();
        if(levelPtr == 0) levelPtr = levels.size - 1;
        else levelPtr--;
        loadLevel(levelPtr);
    }

    public static void loadNextLevel() {
        unloadLevel();
        if(levelPtr < levels.size - 1) levelPtr++;
        else levelPtr = 0;
        loadLevel(levelPtr);
    }
}
