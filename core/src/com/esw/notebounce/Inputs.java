package com.esw.notebounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

// !!! To file
@SuppressWarnings("unused")
class Inputs {

    // Mouse
    static Vector2 mouse = new Vector2(0,0);
    static Vector2 imouse = new Vector2(0,0);
    // Regular                   // Shifted
    static boolean mouseleft;    static boolean shiftMouseLeft;
    static boolean mousemiddle;  static boolean shiftMouseMiddle;
    static boolean mouseright;   static boolean shiftMouseRight;
    static boolean mouseforward; static boolean shiftMouseFoward;
    static boolean mouseback;    static boolean shiftMouseBack;
    static boolean clicked;

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
    static boolean h;            static boolean H;
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
    static boolean period;          static boolean rightangle;
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

    /**
     * Grab all of the mouse inputs.
     */
    public static void getMouseInputs() {
        // LibGDX specifies the mouse's (0,0) to be in the upper left corner while the
        // graphic's (0,0) is in the lower left. Getting the ScreenHeight - mouse.y
        // normalizes mouse inputs to the graphics coordinates.
        mouse.x = Gdx.input.getX(); mouse.y = NoteBounce.ScreenHeight - Gdx.input.getY();
        imouse.x = mouse.x; imouse.y = mouse.y + NoteBounce.ScreenHeight;
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

        leftsquare = Gdx.input.isKeyJustPressed(Input.Keys.LEFT_BRACKET); // Load previous level
        rightsquare = Gdx.input.isKeyJustPressed(Input.Keys.RIGHT_BRACKET); // Load next level

        tick = Gdx.input.isKeyJustPressed(Input.Keys.GRAVE); // Toggle grid

        esc = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE); // Kill the app
    }

    /**
     * Grab all of the mouse inputs, then grab all of the inputs from the keys that are needed
     * to edit levels.
     */
    public static void getEditInputs() { // TODO BETTER EDIT CONTROLS
        getMouseInputs();

        t = Gdx.input.isKeyJustPressed(Input.Keys.T); // TriangleType
        b = Gdx.input.isKeyJustPressed(Input.Keys.B); // Box1
        g = Gdx.input.isKeyJustPressed(Input.Keys.G); // Gun/Ball
        v = Gdx.input.isKeyJustPressed(Input.Keys.V); // Goal
        c = Gdx.input.isKeyJustPressed(Input.Keys.C); // Erase

        y = Gdx.input.isKeyJustPressed(Input.Keys.Y); // Blue
        u = Gdx.input.isKeyJustPressed(Input.Keys.U); // Green
        i = Gdx.input.isKeyJustPressed(Input.Keys.I); // Yellow
        o = Gdx.input.isKeyJustPressed(Input.Keys.O); // Cyan
        p = Gdx.input.isKeyJustPressed(Input.Keys.P); // Magenta

        q = Gdx.input.isKeyJustPressed(Input.Keys.Q); // TriangleType botleft
        w = Gdx.input.isKeyJustPressed(Input.Keys.W); // TriangleType topleft
        e = Gdx.input.isKeyJustPressed(Input.Keys.E); // TriangleType botright
        r = Gdx.input.isKeyJustPressed(Input.Keys.R); // TriangleType botright

        a = Gdx.input.isKeyJustPressed(Input.Keys.A); // Acceleration modifier
        s = Gdx.input.isKeyJustPressed(Input.Keys.S); // Gravity modifier
        d = Gdx.input.isKeyJustPressed(Input.Keys.D); // Dampen modifier

        l = Gdx.input.isKeyJustPressed(Input.Keys.L); // Doors
        comma = Gdx.input.isKeyJustPressed(Input.Keys.COMMA); // Door shut
        period = Gdx.input.isKeyJustPressed(Input.Keys.PERIOD); // Door open
        semicolon = Gdx.input.isKeyJustPressed(Input.Keys.SEMICOLON); // Door open
        singlequote = Gdx.input.isKeyJustPressed(Input.Keys.APOSTROPHE); // Door open

        m = Gdx.input.isKeyJustPressed(Input.Keys.M); // Mine

        n = Gdx.input.isKeyJustPressed(Input.Keys.N); // With lctrl for new level

        up    = Gdx.input.isKeyJustPressed(Input.Keys.UP); // ModifierType up
        down  = Gdx.input.isKeyJustPressed(Input.Keys.DOWN); // ModifierType down
        left  = Gdx.input.isKeyJustPressed(Input.Keys.LEFT); // ModifierType left
        right = Gdx.input.isKeyJustPressed(Input.Keys.RIGHT); // ModifierType right

        one   = Gdx.input.isKeyJustPressed(Input.Keys.NUM_1); // Shade 0
        two   = Gdx.input.isKeyJustPressed(Input.Keys.NUM_2); // Shade 1
        three = Gdx.input.isKeyJustPressed(Input.Keys.NUM_3); // Shade 2
        four  = Gdx.input.isKeyJustPressed(Input.Keys.NUM_4); // Shade 3
        five  = Gdx.input.isKeyJustPressed(Input.Keys.NUM_5); // Shade 4
        six   = Gdx.input.isKeyJustPressed(Input.Keys.NUM_6); // Shade 5
        seven = Gdx.input.isKeyJustPressed(Input.Keys.NUM_7); // Shade 6
        eight = Gdx.input.isKeyJustPressed(Input.Keys.NUM_8); // Shade 7
        nine  = Gdx.input.isKeyJustPressed(Input.Keys.NUM_9); // Shade 8

        numone   = Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1); // Gun pos 1
        numtwo   = Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2); // Gun pos 2
        numthree = Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3); // Gun pos 3
        numfour  = Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_4); // Gun pos 4
        numfive  = Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_5); // Gun pos 5
        numsix   = Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_6); // Gun pos 6
        numseven = Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_7); // Gun pos 7
        numeight = Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_8); // Gun pos 8
        numnine  = Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_9); // Gun pos 9

        lshift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT); // Snap to lines

        lctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT); // Snap to midlines

        space = Gdx.input.isKeyJustPressed(Input.Keys.SPACE); // Load next level

        leftsquare = Gdx.input.isKeyJustPressed(Input.Keys.LEFT_BRACKET); // Load previous level
        rightsquare = Gdx.input.isKeyJustPressed(Input.Keys.RIGHT_BRACKET); // Load next level

        esc = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE); // Kill app
    }

    public static boolean edit() { return Gdx.input.isKeyJustPressed(Input.Keys.TAB); }

    public static boolean grid() { return Gdx.input.isKeyJustPressed(Input.Keys.GRAVE); }

    public static boolean testing() { return Gdx.input.isKeyJustPressed(Input.Keys.SLASH); }

}