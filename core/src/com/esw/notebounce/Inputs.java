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
    static boolean mouseleft;    boolean shiftMouseLeft;
    static boolean mousemiddle;  boolean shiftMouseMiddle;
    static boolean mouseright;   boolean shiftMouseRight;
    static boolean mouseforward; boolean shiftMouseFoward;
    static boolean mouseback;    boolean shiftMouseBack;

    // Modifiers          // Left                // Right
    static boolean shift; static boolean lshift; static boolean rshift;
    static boolean ctrl;  static boolean lctrl;  static boolean rctrl;
    static boolean alt;   static boolean lalt;   static boolean ralt;
    static boolean meta;  static boolean lmeta;  static boolean rmeta;
    static boolean menu;
    static boolean esc;

    // Alpha keys  /* NOTE: A capital key will be tied to shift!! */
    // Unshifted                 // Shifted
    static boolean tab;          boolean shifttab;
    static boolean q;            boolean Q;
    static boolean w;            boolean W;
    static boolean e;            boolean E;
    static boolean r;            boolean R;
    static boolean t;            boolean T;
    static boolean y;            boolean Y;
    static boolean u;            boolean U;
    static boolean i;            boolean I;
    static boolean o;            boolean O;
    static boolean p;            boolean P;
    static boolean leftsquare;   boolean leftcurly;
    static boolean rightsquare;  boolean rightcurly;
    static boolean backslash;    boolean pipe;
    static boolean a;            boolean A;
    static boolean s;            boolean S;
    static boolean d;            boolean D;
    static boolean f;            boolean F;
    static boolean j;            boolean J;
    static boolean k;            boolean K;
    static boolean l;            boolean L;
    static boolean semicolon;    boolean colon;
    static boolean singlequote;  boolean doublequote;
    static boolean enter;        boolean shiftenter;
    static boolean z;            boolean Z;
    static boolean x;            boolean X;
    static boolean c;            boolean C;
    static boolean v;            boolean V;
    static boolean b;            boolean B;
    static boolean n;            boolean N;
    static boolean m;            boolean M;
    static boolean comma;        boolean leftangle;
    static boolean dot;          boolean rightangle;
    static boolean forwardslash; boolean question;
    static boolean space;

    // Number keys/symbols
    // Unshifted           // Shifted
    static boolean tick;   boolean tilde;
    static boolean one;    boolean bang;
    static boolean two;    boolean at;
    static boolean three;  boolean hash;
    static boolean four;   boolean dollar;
    static boolean five;   boolean percent;
    static boolean six;    boolean caret;
    static boolean seven;  boolean and;
    static boolean eight;  boolean star;
    static boolean nine;   boolean leftparen;
    static boolean zero;   boolean rightparen;
    static boolean dash;   boolean underscore;
    static boolean equals; boolean plus;

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
     * (e.g. a and A or 8 and *) can be called directily (in this case inputs.a and inputs.A or
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
    public static void getEditInputs() { // TODO edit inputs
        getMouseInputs();
    } // TODO edit inputs

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