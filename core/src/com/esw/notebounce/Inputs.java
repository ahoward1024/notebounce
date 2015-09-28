package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

// !!! To file
@SuppressWarnings("unused")
class Inputs {

    // Mouse
    static Vector2 mouse = new Vector2(0,0);
    // Regular                   // Shifted
    static boolean mouseleft;    static boolean shiftMouseLeft;
    static boolean mousemiddle;  static boolean shiftMouseMiddle;
    static boolean mouseright;   static boolean shiftMouseRight;
    static boolean mouseforward; static boolean shiftMouseFoward;
    static boolean mouseback;    static boolean shiftMouseBack;

    // Modifiers          // Left                // Right
    static boolean shift; static boolean lshift; static boolean rshift;
    static boolean ctrl;  static boolean lctrl;  static boolean rctrl;
    static boolean alt;   static boolean lalt;   static boolean ralt;
    static boolean meta;  static boolean lmeta;  static boolean rmeta;
    static boolean menu;
    static boolean esc;

    // Alpha keys  /* NOTE: A capital key will be tied to shift!! */
    // Unshifted                 // Shifted
    static boolean tab;          static boolean shifttab;
    static boolean q;            static boolean Q;
    static boolean w;            static boolean W;
    static boolean e;            static boolean E;
    static boolean r;            static boolean R;
    static boolean t;            static boolean T;
    static boolean y;            static boolean Y;
    static boolean u;            static boolean U;
    static boolean i;            static boolean I;
    static boolean o;            static boolean O;
    static boolean p;            static boolean P;
    static boolean leftsquare;   static boolean leftcurly;
    static boolean rightsquare;  static boolean rightcurly;
    static boolean backslash;    static boolean pipe;
    static boolean a;            static boolean A;
    static boolean s;            static boolean S;
    static boolean d;            static boolean D;
    static boolean f;            static boolean F;
    static boolean g;            static boolean G;
    static boolean j;            static boolean J;
    static boolean k;            static boolean K;
    static boolean l;            static boolean L;
    static boolean semicolon;    static boolean colon;
    static boolean singlequote;  static boolean doublequote;
    static boolean enter;        static boolean shiftenter;
    static boolean z;            static boolean Z;
    static boolean x;            static boolean X;
    static boolean c;            static boolean C;
    static boolean v;            static boolean V;
    static boolean b;            static boolean B;
    static boolean n;            static boolean N;
    static boolean m;            static boolean M;
    static boolean comma;        static boolean leftangle;
    static boolean dot;          static boolean rightangle;
    static boolean forwardslash; static boolean question;
    static boolean space;

    // Number keys/symbols
    // Unshifted           // Shifted
    static boolean tick;   static boolean tilde;
    static boolean one;    static boolean bang;
    static boolean two;    static boolean at;
    static boolean three;  static boolean hash;
    static boolean four;   static boolean dollar;
    static boolean five;   static boolean percent;
    static boolean six;    static boolean caret;
    static boolean seven;  static boolean and;
    static boolean eight;  static boolean star;
    static boolean nine;   static boolean leftparen;
    static boolean zero;   static boolean rightparen;
    static boolean dash;   static boolean underscore;
    static boolean equals; static boolean plus;

    static boolean F1;
    static boolean F2;
    static boolean F3;
    static boolean F4;
    static boolean F5;
    static boolean F6;
    static boolean F7;
    static boolean F8;
    static boolean F9;
    static boolean F10;
    static boolean F11;
    static boolean F12;

    static boolean sysrq; boolean prtscn;
    static boolean scrlock;
    static boolean pause; boolean brk;

    static boolean insert;
    static boolean home;
    static boolean pageup;
    static boolean delete;
    static boolean end;
    static boolean pagedown;

    static boolean up;
    static boolean down;
    static boolean left;
    static boolean right;

    // Num Keys
    static boolean numone;
    static boolean numtwo;
    static boolean numthree;
    static boolean numfour;
    static boolean numfive;
    static boolean numsix;
    static boolean numseven;
    static boolean numeight;
    static boolean numnine;
    static boolean numzero;
    static boolean numlock;
    static boolean numforwardslash;
    static boolean numstar;
    static boolean numplus;
    static boolean numminus;
    static boolean numenter;
    static boolean numdot;

    float ScreenWidth = 0;
    float ScreenHeight = 0;

    /**
     * This class is designed to grab all input states needed for the game and for editing levels.
     * It was also designed for ease of use as all keys that have a "shifted" state
     * (e.g. a and A or 8 and *) can be called directly (in this case inputs.a and inputs.A or
     * inputs.eight and inputs.star) instead of having to manually poll for the modifier key.
     * In any case, all of the modifier keys can also be called so we can use it in multi-key commands.
     * @param width The width of the screen (for mouse input to be normalized)
     * @param height The height of the screen (for mouse input to be normalized)
     */
    Inputs(int width, int height) {
        ScreenWidth = width;
        ScreenHeight = height;
    }

    /**
     * Grab all of the mouse inputs.
     */
    public static void getMouseInputs() {
        // LibGDX specifies the mouse's (0,0) to be in the upper left corner while the
        // graphic's (0,0) is in the lower left. Getting the ScreenHeight - mouse.y
        // normalizes mouse inputs to the graphics coordinates.
        mouse.x = Gdx.input.getX(); mouse.y = NoteBounce.ScreenHeight - Gdx.input.getY();
        mouseleft = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        mousemiddle = Gdx.input.isButtonPressed(Input.Buttons.MIDDLE);
        mouseright = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
        mouseforward = Gdx.input.isButtonPressed(Input.Buttons.FORWARD);
        mouseback = Gdx.input.isButtonPressed(Input.Buttons.BACK);
    }

    /**
     * Grab all of the mouse inputs, then grab all of the inputs from the keys that are needed
     * to play the game.
     */
    public static void getGameInputs() {
        getMouseInputs();

        lshift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT); // Timestep slow
        lctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT); // Timestep fast

        f = Gdx.input.isKeyJustPressed(Input.Keys.F); // Reset
        space = Gdx.input.isKeyJustPressed(Input.Keys.SPACE); // Next level
    }

    /**
     * Grab all of the mouse inputs, then grab all of the inputs from the keys that are needed
     * to edit levels.
     */
    public static void getEditInputs() {
        getMouseInputs();

        // BOXES
        one   = Gdx.input.isKeyJustPressed(Input.Keys.NUM_1); // blues
        two   = Gdx.input.isKeyJustPressed(Input.Keys.NUM_2); // greens
        three = Gdx.input.isKeyJustPressed(Input.Keys.NUM_3); // cyans
        four  = Gdx.input.isKeyJustPressed(Input.Keys.NUM_4); // magentas
        five  = Gdx.input.isKeyJustPressed(Input.Keys.NUM_5); // yellows
        six   = Gdx.input.isKeyJustPressed(Input.Keys.NUM_6); // goal
        seven = Gdx.input.isKeyJustPressed(Input.Keys.NUM_7); // doors

        if(Edit.state == Edit.State.box || Edit.state == Edit.State.triangle) {
            q = Gdx.input.isKeyJustPressed(Input.Keys.Q); // Shade 0
            w = Gdx.input.isKeyJustPressed(Input.Keys.E); // Shade 1
            e = Gdx.input.isKeyJustPressed(Input.Keys.R); // Shade 2
            r = Gdx.input.isKeyJustPressed(Input.Keys.T); // Shade 3
            t = Gdx.input.isKeyJustPressed(Input.Keys.Y); // Shade 4
            u = Gdx.input.isKeyJustPressed(Input.Keys.U); // Shade 5
            i = Gdx.input.isKeyJustPressed(Input.Keys.I); // Shade 6
            o = Gdx.input.isKeyJustPressed(Input.Keys.O); // Shade 7
            p = Gdx.input.isKeyJustPressed(Input.Keys.P); // Shade 8
        }


        // Doors

        // Gun
        g = Gdx.input.isKeyJustPressed(Input.Keys.G); // Gun/Ball

        shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || // Snap to large grid
            Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || // Snap to fine grid
            Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);



    } // TODO edit inputs ??? more ???

    /**
     * Grab whether the edit key was pressed or not
     * @return The state of the edit key (grave)
     */
    public static boolean edit() {
        return Gdx.input.isKeyJustPressed(Input.Keys.GRAVE);
    }

    /**
     * Grab all of the mouse inputs, then grab all of the inputs from all of the keys.
     */
    public static void getAllInputs() {
        getMouseInputs();
    } // TODO all inputs... maybe...

}