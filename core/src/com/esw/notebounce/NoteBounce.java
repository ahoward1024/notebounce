package com.esw.notebounce;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Alex on 9/21/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class NoteBounce extends ApplicationAdapter implements InputProcessor {

	final boolean DEBUGBUILD = true;

	public final static float PIXELS2METERS = 100.0f; // Yay globals!
	public static final float originalGravity = -200.0f;
	public static float gravity = originalGravity;
	public static String gravityDirection = "Down";

	public static final int basew = 1920, baseh = 1080;

//=====================================================================================================//

	static World world;
	static boolean playNotes = true;
	static boolean goalHit = false;
	static boolean playRipple = false;
	static Sound[] notes = new Sound[8];
	static int notePtr = 0;
	static Sound goalNoise;

	final int velocityIterations = 6;
	final int positionIterations = 2;

	static int ScreenWidth  = 0;
	static int ScreenHeight = 0;

	OrthographicCamera camera; // Orthographic because 2D
	Viewport viewport;

	Box2DDebugRenderer box2DDebugRenderer;
	Matrix4 debugMatrix; // For Box2D's debug drawing projection
	BitmapFont debugMessage;
	ShapeRenderer debugShapeRenderer;

	SpriteBatch batch;
	static Ball ball;
	static Sprite ballSimSprite;
	static Sprite ripple;
	Sprite crosshair;
	ShapeRenderer shapeRenderer;

	CollisionDetection collisionDetector;

	float goalTextTimer = 0.0f;
	static float deltaTime = 0.0f;
	float timestep = 300.0f;
	final float timestepNormal = 300.0f;
	final float timestepSlow = 3000.0f;
	final float timestepFast = 100.0f;

	String inputDebug = "";        // DEBUG
	String mouseClickDebug = "";   // DEBUG
	String ballPositionDebug = ""; // DEBUG
	String ballVelocityDebug = ""; // DEBUG
	String gunPositionDebug = "";  // DEBUG
	String fpsDebug = "FPS: ";     // DEBUG

	boolean showGoalHit = false; // Show "GOAL!" text

	static boolean ballShot = false; // Is the ball shot?
	static boolean moveBallToGun = false; // Toggle to lerp the ball back to the gun
	static boolean drawBallOver = false; // Toggle to draw the ball over the gun after it has been shot

	Vector2 mouseClick = new Vector2(0,0);
	Vector2 mouseUnClick = new Vector2(0,0);

	Vector2 velocity = new Vector2(1,1);
	float angle = 0.0f;
	float power = 0.0f; // Power of the shot
	final float MAX_POWER = 60.0f; // Maximum power of the shot
	static boolean shoot = false;

	boolean touch = false; // Is the mouse clicked or the screen has been touched?
	static boolean reset = false; // Toggle to reset level

	static Array<Vector2> simcoords = new Array<Vector2>();

	static Array<Box> boxes = new Array<Box>();
	static Array<Triangle> triangles = new Array<Triangle>();
	static Array<Modifier> modifiers = new Array<Modifier>();
	static Array<Goal> goals = new Array<Goal>();
	static Array<Door> doors = new Array<Door>();
	static Array<DoorSwitch> switches = new Array<DoorSwitch>();
	static Array<Mine> mines = new Array<Mine>();
	static Gun[] guns = new Gun[9];
	static int currentGun = 0;

	boolean edit = false;

	static int lines = 0;
	static int midlines = 0;

	// 16:9 RESOLUTIONS:
	// 2560x1440, Scale: 133% at 160px
	// 1920x1080, Scale: 100% at 120px (base resolution)
	// 1280x720, Scale: 66% at 80px
	// 800x480, Scale: 33% at 40px

	// ASPECT RATIOS
	// 1.7777778 = 16:9
	// 1.6       = 16:10
	// 1.3333334 = 4:3

	static float scalePercent = 1.0f;

//=====================================================================================================//

	/**
	 * Create a new NoteBounce level and set the ScreenWidth and ScreenHeight.
	 * @param width The width of the screen.
	 * @param height The height of the screen.
	 */
	public NoteBounce(int width, int height) {
		ScreenWidth  = width;
		ScreenHeight = height;
	}

	public static int bufferWidth = 0;
	public static int bufferHeight = 0;

	public static int scaleWidth = 1920;
	public static int scaleHeight = 1080;

	/**
	 * Creates the game world.
	 */
	@Override
	public void create() {
		System.out.println("Screen Width: " + ScreenWidth + " Screen Height: " + ScreenHeight);

		float asr = Utility.getAspectRatio(ScreenWidth, ScreenHeight);
		System.out.println("Aspect ratio : " + asr);

		Vector2 dim = Utility.getScaleDimension(ScreenWidth, ScreenHeight);
		scaleWidth = (int)dim.x;
		scaleHeight = (int)dim.y;
		System.out.println("Scale Width: " + scaleWidth + " Scale Height: " + scaleHeight);

		scalePercent = (float)Utility.GCD(scaleWidth, scaleHeight) / (float)Utility.GCD(basew, baseh);
		System.out.println("Scale percent: " + scalePercent + "%");

		bufferWidth = (ScreenWidth - scaleWidth) / 2;
		bufferHeight = (ScreenHeight - scaleHeight) / 2;
		System.out.println("Buffer Width: " + bufferWidth + " Buffer Height: " + bufferHeight);

		lines = scaleWidth / 16;
		midlines = lines / 2;

		Box2D.init(); // MUST initialize Box2D before using it!
		box2DDebugRenderer = new Box2DDebugRenderer();
		debugShapeRenderer = new ShapeRenderer();
		debugShapeRenderer.setAutoShapeType(true);

		camera = new OrthographicCamera(ScreenWidth, ScreenHeight);
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0.0f);
		camera.update();

		batch = new SpriteBatch();
		// Set the projection matrix to combined to the camera's
		// combined projection and matrix
		batch.setProjectionMatrix(camera.combined);

		debugMessage = new BitmapFont();

		Gdx.input.setInputProcessor(this);

		collisionDetector = new CollisionDetection();
		// Because the world's timestep will be 1/300, we need to make gravity
		// _a lot_ more than the standard 9.8 or 10. Otherwise the ball will act
		// like it is in space after it slows down quite a bit. 200 gives a good balance.
		world = new World(new Vector2(0, gravity * scalePercent), true);
		world.setContactListener(collisionDetector);

		// Build the lines for the bounding tmpbox that makes it so the ball
		// does not go off the screen
		new Boundary(bufferWidth, bufferHeight, ScreenWidth - bufferWidth, bufferHeight, UserData.Edge.bot);
		new Boundary(bufferWidth, ScreenHeight - bufferHeight, ScreenWidth - bufferWidth, ScreenHeight - bufferHeight, UserData.Edge.top);
		new Boundary(ScreenWidth - bufferWidth, bufferHeight, ScreenWidth - bufferWidth, ScreenHeight - bufferHeight, UserData.Edge.left);
		new Boundary(bufferWidth, bufferHeight, bufferWidth, ScreenHeight - bufferHeight, UserData.Edge.right);

        goalNoise = Gdx.audio.newSound(Gdx.files.internal("notes/goal.mp3"));

		// Create the bodyArray to hold of of the notes for the note blocks
		notes[0] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/C4.mp3"));
		notes[1] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/D4.mp3"));
		notes[2] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/E4.mp3"));
		notes[3] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/F4.mp3"));
		notes[4] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/G4.mp3"));
		notes[5] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/A4.mp3"));
		notes[6] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/B4.mp3"));
		notes[7] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/C5.mp3"));
		notePtr = 0;

		crosshair = new Sprite(new Texture(Gdx.files.internal("art/crosshair.png")));

		ballSimSprite = new Sprite(new Texture(Gdx.files.internal("art/simball.png")));
		ballSimSprite.setScale(scalePercent);

		LevelLoader.createLevelsArray(Gdx.files.internal("levels/"));
	}

	@Override
	public void resize(int width, int height) { // WARNING: Resizing does not work on the desktop.
		ScreenWidth = width;
		ScreenHeight = height;
	}

	/**
	 * Reset the state of the current level so the ball can be shot again.
	 */
	static void reset() {
		ballShot = false;
		ball.body.setType(BodyDef.BodyType.StaticBody);
		moveBallToGun = true;
		playNotes = true;
		drawBallOver = false;
		world.setGravity(new Vector2(0, originalGravity));
		simcoords.clear();
		for(int i = 0; i < switches.size; i++) {
			DoorSwitch ds = switches.get(i);
			if(!ds.active) {
				Vector2 v = new Vector2(0,0);
				v.x = ds.sprite.getX();
				v.y = ds.sprite.getY();
				ds.sprite = new Sprite(new Texture(Gdx.files.internal("art/switch.png")));
				ds.sprite.setPosition(v.x, v.y);
				Door d = doors.get(i);
				if(d.state == Door.State.open) d.shut();
				else d.open();
				ds.active = true;
			}
		}
	}

	/**
	 * Move the ball through linear interpolation back to the gun.
	 */
	static void moveBallToGun() {
		shoot = false;
		Vector2 v;
		if(guns[currentGun] != null) {
			v = guns[currentGun].center;
		} else {
			v = Inputs.mouse; // DEBUG
		}

		ball.body.setTransform(Utility.lerp(ball.center.x / PIXELS2METERS, v.x / PIXELS2METERS, deltaTime * 10),
				Utility.lerp(ball.center.y / PIXELS2METERS, v.y / PIXELS2METERS, deltaTime * 10), 0.0f);
		ball.setSpriteToBodyPosition();
		if(Utility.isInsideCircle(ball.center, v, 10.0f)) {
			ball.body.setTransform(v.x / PIXELS2METERS, v.y / PIXELS2METERS, 0.0f);
			ball.setSpriteToBodyPosition();
			moveBallToGun = false;
		}
	}

	/**
	 * Update all of the variables needed to simulate physics.
	 */
	void updatePhysics() {
		world.step(1.0f / timestep, velocityIterations, positionIterations);
		world.clearForces();
	}

	/* NOTE: There are a number of ways we could simulate the ball's path. Some techniques can be
	 *		 combined for a greater effect:
	 * 1) Use one world and toggle between a state of simulation and actually stepping the world
	 *		with the ball being shot. This way we can do collision detection and and use the real physics
	 *		of the ball. This would be the most difficult to implement efficiently. To draw the arc while
	 *		the ball is shooting we would need to combine this with technique 3 (See #4).
	 * 2) Make another world. This pretty much is the worst case scenario, though. We can do
	 * 		the physics calculations trivially, as we would do with technique 1, but in order to simulate
	 * 		collisions we would have to copy the original world entirely. This is the easiest to
	 * 		implement, however, as there is no worry of the worlds stepping on top of one another
	 *		and the path can always be drawn trivially.
	 * 3) Draw sprites at the x,y coordinates using kinematic equations. This option is the most
	 *		efficient but is not ideal because it is not as flexible and we would need to implement
	 *		either technique 1 or 2 to do collisions anyway. This is the least interesting option.
	 * 4) The _best_ way to do this would be to combine techniques. We could toggle the simulation
	 * 		by stepping the world when the mouse is clicked and draw sprites in the locations
	 * 	 	that the ball would go. This way we can also have collision detection and the arc would
	 * 	 	be visible after the ball has been shot. There would need to be an array of sprites
	 * 	 	that we would have to draw to, increasing space, and this would be the most complex out of
	 *		all techniques to do super efficiently.
	 */
	/**
	 * Run a physics simulation that calculates where the ball would go if it were to be shot
	 * with the current power and angle.
	 */
	// TODO simluation gravity changing
	void simulate() {
		if(ball != null) {
			ball.body.getFixtureList().first().setUserData(new UserData(UserData.Type.sim));
			ball.body.setType(BodyDef.BodyType.DynamicBody);
			ball.body.setLinearVelocity(velocity.x * power, velocity.y * power);
			simcoords.clear();

			int steps = 4;
			if(timestep == timestepNormal) {
				steps = 8;
			} else if(timestep == timestepSlow) {
				steps = 20;
			} else if(timestep == timestepFast) {
				steps = 2;
			}
			// NOTE: DO NOT SET THE LOOP THIS HIGH (> 500) FOR A RELEASE BUILD. If the gun is aimed straight
			// up the loop will not break causing it to run every iteration and will cause framerate issues.
			for(int i = 0; i < 200; i++) { // DEBUG
				world.step(1.0f / timestep, velocityIterations, positionIterations);
				if(i % steps == 0) {
					simcoords.add(new Vector2(ball.body.getPosition().x * PIXELS2METERS,
						ball.body.getPosition().y * PIXELS2METERS));
				}
				if(collisionDetector.simhit) break; // Collision detection
			}
			collisionDetector.simhit = false;
			ball.body.setType(BodyDef.BodyType.StaticBody);
			if(guns[currentGun] != null) {
				ball.body.setTransform(guns[currentGun].center.x / PIXELS2METERS,
					guns[currentGun].center.y / PIXELS2METERS, 0.0f);
			} else { // DEBUG
				ball.body.setTransform(mouseClick.x / PIXELS2METERS, mouseClick.y / PIXELS2METERS, 0.0f);
			}
			ball.body.getFixtureList().first().setUserData(new UserData(UserData.Type.ball));
			world.clearForces();
		}
	}

	/**
	 * Update all of the variables needed to calculate sprite positioning
	 */
	void update() {

		// If we have touched the screen or clicked we should update the power and the angle.
		if(touch) {
			crosshair.setCenter(mouseClick.x, mouseClick.y);

			// The power is calculated by getting the distance between where the touch input started
			// and where the touch input is currently at, then divided by four to "smooth" out the value
			// so the player can have a more granular shot based on how far they pulled. 15.0f is added
			// so the ball will _always_ shoot out of the gun. This is so there are not accidental
			// misstaps where the player might not know if they have shot the ball or not
			power = ((float)Math.sqrt(Math.pow((Inputs.mouse.x - mouseClick.x), 2.0) +
				Math.pow((Inputs.mouse.y - mouseClick.y), 2.0)) / 4.0f) + 15.0f;
			if(power > MAX_POWER) power = MAX_POWER;

			power *= scalePercent;

			// TODO: Change the angle so instead of pulling back to get the power,
			// todo make it so the player pulls forward. (Perhaps make this a user option later ???)
			// Find the angle for the gun and ball's projection arc based on where the mouse is
			// located on the screen. Works best if the gun's texture is defaulted to point towards
			// the right.
			angle = (float) Math.atan2(mouseClick.y - Inputs.mouse.y,
				mouseClick.x - Inputs.mouse.x);
			angle *= (180 / Math.PI);
			// Translate the rotation around 360 degrees
			if(angle < 0) angle = 360 + angle;

			if(guns[currentGun] != null) guns[currentGun].rotate(angle); // Only set the rotation if the ball is not shot
			else ball.body.setTransform(mouseClick.x / PIXELS2METERS, mouseClick.y / PIXELS2METERS, 0.0f); // DEBUG
		}

		velocity.setAngle(angle);
		// If we are going to shoot and the ball has not already been shot, shoot the ball.
		// We also need to update the last angle and power calculations
		if(shoot && !ballShot) {
			shoot = false;
			ballShot = true;
			ball.body.setType(BodyDef.BodyType.DynamicBody);
			ball.body.setLinearVelocity(velocity.x * power, velocity.y * power);
		}

		// Set the ball's sprite position the the same position as the ball's Box2D body position
		if(ballShot) {
			ball.setSpriteToBodyPosition();
			for(Gun g : guns)
			if(g != null) {
				if((ball.body.getPosition().x * PIXELS2METERS) > g.endX(angle) &&
					(ball.body.getPosition().y * PIXELS2METERS) > g.endY(angle)) {
					drawBallOver = true;
				}
			}
		}

		if(moveBallToGun) {
			moveBallToGun();
		}

		if(playRipple) {
			ripple.setScale(ripple.getScaleX() + 0.1f, ripple.getScaleY() + 0.1f);
			if(ripple.getScaleX() >= 1 && ripple.getScaleY() >= 1) {
				ripple.getTexture().dispose();
				ripple = null;
				playRipple = false;
			}
		}

		// Go to next level if a goal is hit
		if(goalHit) {
			if(!testing) LevelLoader.loadNextLevel();
			reset();
			goalHit = false;
			showGoalHit = true;
		}

		if(touch) simulate();

		if(reset) reset();

		if(didDampen) {
			switch(dampenType) {
				case left: {
					ball.body.setTransform(ball.body.getPosition().x - (1 / NoteBounce.PIXELS2METERS), ball.body.getPosition().y, ball.body.getAngle());
				} break;
				case right: {
					ball.body.setTransform(ball.body.getPosition().x + (1 / NoteBounce.PIXELS2METERS), ball.body.getPosition().y, ball.body.getAngle());
				} break;
			}
			ball.setSpriteToBodyPosition();
			didDampen = false;
		}

		// Set the ball's sprites rotation so it shows it's spinning
		ball.sprite.setRotation(ball.body.getAngle() * NoteBounce.PIXELS2METERS);
	}

	void drawDottedLine(int dotDist, float x1, float y1, float x2, float y2) {
		Vector2 vec2 = new Vector2(x2, y2).sub(new Vector2(x1, y1));
		float length = vec2.len();
		for(int i = 0; i < length; i += dotDist) {
			vec2.clamp(length - i, length - i);
			debugShapeRenderer.point(x1 + vec2.x, y1 + vec2.y, 0);
		}
	}

	boolean testing = true;
	boolean showBox2D = false;
	/**
	 * Render all of the objects in the game world.
	 */
	@Override
	public void render() {

		// ================ UPDATE ================//

		// WARNING!!!!! ALWAYS GRAB INPUTS FIRST!! If you do not this could have dire consequences
		// as the input states will not be updated since the last frame which could cause keys
		// to always be pressed or never be pressed etc...

		if(Inputs.esc) Gdx.app.exit(); // Kill the app when escape is pressed

		if(Inputs.edit()) {
			edit = ! edit; // Grab the edit key (tab) first
		}
		if(Inputs.grid()) Edit.drawGrid = !Edit.drawGrid;
		if(Inputs.testing()) testing = !testing;

		if(Inputs.space || Inputs.rightsquare) LevelLoader.loadNextLevel();
		else if(Inputs.leftsquare) LevelLoader.loadPreviousLevel();

		if(Inputs.showBox2D()) showBox2D = !showBox2D;

		if(!edit) {
			Gdx.input.setCursorImage(null, 0, 0);
			Edit.destroyAll();

			// Update all of the sprites
			Inputs.getGameInputs();
			if(Edit.drawGrid && Edit.grid == Edit.Grid.on) {
				Edit.drawGrid = false;
				Edit.grid = Edit.Grid.off;
			}
			if(Inputs.lshift) timestep = timestepSlow;
			else if (Inputs.lctrl) timestep = timestepFast;
			else timestep = timestepNormal;
			update();
			// Simulate Box2D physics
			if(ballShot) updatePhysics();
		} else {
			Edit.editLevel(); // TODO clean up editing ???
		}

		// ================ RENDER ================//

		// OpenGL
		//Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 0.7f); // DEBUG: Light Grey
		//Gdx.gl.glClearColor(1, 1, 1, 1); // DEBUG: White
		//Gdx.gl.glClearColor(1, 0, 0, 1); // DEBUG: Red
		Gdx.gl.glClearColor(0, 0, 0, 1); // DEBUG: Black
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		deltaTime = Gdx.graphics.getDeltaTime();
		camera.update(); // Update the camera just before drawing

		// Draw the background first
		debugShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		debugShapeRenderer.setColor(Color.WHITE);
		debugShapeRenderer.rect(bufferWidth, bufferHeight, scaleWidth, scaleHeight);
		debugShapeRenderer.end();

		batch.begin();   // Start the batch drawing

		float ballSimAlpha = 0.75f;
		for(int i = 0; i < simcoords.size; i++) {
			Vector2 tmp = simcoords.get(i);
			ballSimSprite.setCenter(tmp.x, tmp.y);
			ballSimSprite.setAlpha(ballSimAlpha);
			ballSimSprite.draw(batch);
			if(ballSimAlpha > 0.0f) ballSimAlpha -= 0.05f;
			else ballSimAlpha = 0.0f;
		}

		// Draw Boxes
		if(Edit.tmpbox != null) {
			Edit.tmpbox.sprite.draw(batch);
		}
		for(Box b : boxes) {
			b.sprite.draw(batch);
		}

		// Draw Triangles
		if(Edit.tmptriangle != null) {
			Edit.tmptriangle.sprite.draw(batch);
		}
		for(Triangle t : triangles) {
			t.sprite.draw(batch);
		}

		// Draw Modifiers
		if(Edit.tmpmodifier != null) {
			Edit.tmpmodifier.sprite.draw(batch);
		}
		for(Modifier m : modifiers) {
			m.sprite.draw(batch);
		}

		// Draw Mines
		if(Edit.tmpmine != null) {
			Edit.tmpmine.sprite.draw(batch);
		}
		for(Mine m : mines) {
			m.sprite.draw(batch);
		}

		// Draw Goals
		if(Edit.tmpgoal != null) {
			Edit.tmpgoal.sprite.draw(batch);
		}
		for(Goal g : goals) {
			g.sprite.draw(batch);
		}

		// Draw Doors (last so they are over all tiles)
		if(Edit.tmpdoor != null) {
			Edit.tmpdoor.sprite.draw(batch);
		}
		for(Door d : doors) {
			d.sprite.draw(batch);
		}

		// Draw Switches
		if(Edit.tmpswitch != null) {
			Edit.tmpswitch.sprite.draw(batch);
		}
		for(DoorSwitch ds : switches) {
			ds.sprite.draw(batch);
		}

		// Draw the ripple before the ball so it does not cover the ball
		if(playRipple) {
			batch.draw(ripple, ripple.getX(), ripple.getY(), ripple.getOriginX(), ripple.getOriginY(),
				ripple.getWidth(), ripple.getHeight(), ripple.getScaleX(), ripple.getScaleY(),
				ripple.getRotation());
		}

		// Check whether the ball needs to be drawn first (under the gun) or drawn second (over the gun).
		if(drawBallOver) {
			// Now draw the gun so it is over the ball
			for(Gun g : guns) {
				if(g != null) {
					g.sprite.draw(batch);
				}
			}
			// Draw the ball second
			if(ball != null) ball.sprite.draw(batch);
		} else {
			// Draw the ball first so it is under the gun
			if(ball != null) ball.sprite.draw(batch);
			// Now draw the gun so it is over the ball
			for(Gun g : guns) {
				if(g != null) {
					g.sprite.draw(batch);
				}
			}
		}

		if(showGoalHit) {
			debugMessage.setColor(com.badlogic.gdx.graphics.Color.RED);
			debugMessage.draw(batch, "GOAL!", ScreenWidth / 2, ScreenHeight / 2);
			if (goalTextTimer > 3.0f) { // Keep the text up for 10 seconds
				showGoalHit = false;
				goalTextTimer = 0.0f;
			}
			goalTextTimer += deltaTime;
		}

		batch.end();

		if(DEBUGBUILD) drawDebug();

		if(edit || showBox2D) {
			// DEBUG draw Box2D fixtures
			debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS2METERS, PIXELS2METERS, 0);
			box2DDebugRenderer.render(world, debugMatrix); // Render the Box2D debug shapes
		}
	}

	void drawDebug() {
		if(touch) {
			batch.begin();
			crosshair.draw(batch);
			batch.end(); // Have to stop the sprite batch for the shape renderer lines to draw
			debugShapeRenderer.begin();
			debugShapeRenderer.setColor(Color.PURPLE);
			debugShapeRenderer.line(mouseClick.x, mouseClick.y, Inputs.mouse.x, Inputs.mouse.y);
			debugShapeRenderer.line(mouseClick.x, mouseClick.y, mouseClick.x + 100, mouseClick.y); //+X
			debugShapeRenderer.line(mouseClick.x, mouseClick.y, mouseClick.x - 100, mouseClick.y); //-X
			debugShapeRenderer.line(mouseClick.x, mouseClick.y, mouseClick.x, mouseClick.y + 100); //+Y
			debugShapeRenderer.line(mouseClick.x, mouseClick.y, mouseClick.x, mouseClick.y - 100); //-Y
			debugShapeRenderer.setColor(Color.ORANGE);
			debugShapeRenderer.arc(mouseClick.x, mouseClick.y, 100, 0.0f, angle, 32);
			debugShapeRenderer.end();
		}

		// Update the debug strings
		inputDebug = "mouse X: " + Inputs.mouse.x + " | mouse Y: " + Inputs.mouse.y +
			" | Angle: " + String.format("%.2f", angle);
		mouseClickDebug = "mouseClick: " + mouseClick + " | mouseUnClick: " +
			mouseUnClick + " | Power: " + power;
		if(ball != null) {
			ballPositionDebug = "Ball X: " + String.format("%.4f", ball.center.x) +
				" (" + String.format("%.4f", ball.body.getWorldCenter().x * PIXELS2METERS) + ") " +
				" | Ball Y:" + String.format("%.4f", ball.center.y) +
				" (" + String.format("%.4f", ball.body.getWorldCenter().y * PIXELS2METERS) + ")";
			ballVelocityDebug = "Ball Velocity X: " + ball.body.getLinearVelocity().x + " | " +
				"Ball Velocity Y: " + ball.body.getLinearVelocity().y;
		}

		if(guns[currentGun] != null) {
			gunPositionDebug = "Current Gun X: " + String.format("%.2f", guns[currentGun].center.x) +
				" | Y: " + String.format("%.2f", guns[currentGun].center.y) + " | R: " + guns[currentGun].sprite.getRotation() +
			" | ID: " + guns[currentGun].id;
		} else {
			gunPositionDebug = "No current gun";
		}

		int yval = 10;

		batch.begin();
		debugMessage.setColor(com.badlogic.gdx.graphics.Color.GREEN);
		debugMessage.draw(batch, inputDebug, 10, ScreenHeight - yval); yval += 30;
		debugMessage.draw(batch, mouseClickDebug, 10, ScreenHeight - yval); yval += 30;
		debugMessage.draw(batch, ballPositionDebug, 10, ScreenHeight - yval); yval += 30;
		debugMessage.draw(batch, ballVelocityDebug, 10, ScreenHeight - yval); yval += 30;
		debugMessage.draw(batch, gunPositionDebug, 10, ScreenHeight - yval); yval += 30;

		if(world.getGravity().x == 0) {
			if(world.getGravity().y > 0) gravityDirection = "Up";
			else gravityDirection = "Down";
		}
		else {
			if(world.getGravity().x > 0) gravityDirection = "Right";
			else gravityDirection = "Left";
		}
		debugMessage.draw(batch, "Gravity : " + gravityDirection, 10, ScreenHeight - yval); yval += 30;
		debugMessage.draw(batch, "Level: " + LevelLoader.levels.get(LevelLoader.levelPtr).name, 10, ScreenHeight - yval); yval += 30;
		if(edit) {
			debugMessage.setColor(Color.VIOLET);
			debugMessage.draw(batch, "Mode: edit", 10, ScreenHeight - yval); yval += 30;
			debugMessage.draw(batch, "Tool: " + Edit.toolState, 10, ScreenHeight - yval); yval += 30;
			debugMessage.draw(batch, "Edit type: " + Edit.typeState, 10, ScreenHeight - yval); yval += 30;
			debugMessage.draw(batch, "Edit color: " + Edit.colorState, 10, ScreenHeight - yval); yval += 30;
			debugMessage.draw(batch, "ModifierType: " + Edit.modifierState, 10, ScreenHeight - yval); yval += 30;
			debugMessage.draw(batch, "TriangleType: " + Edit.triangleState, 10, ScreenHeight - yval); yval += 30;
			debugMessage.draw(batch, "Door: " + Edit.doorState + " | " + Edit.doorPlane, 10, ScreenHeight - yval); yval += 30;
			debugMessage.draw(batch, "Boxes: " + boxes.size, 10, ScreenHeight - yval); yval += 30;
			debugMessage.draw(batch, "Triangles: " + triangles.size, 10, ScreenHeight - yval); yval += 30;
			debugMessage.draw(batch, "Modifiers: " + modifiers.size, 10, ScreenHeight - yval); yval += 30;
			debugMessage.draw(batch, "Goals: " + goals.size, 10, ScreenHeight - yval); yval += 30;
			debugMessage.draw(batch, "Guns: " + guns.length, 10, ScreenHeight - yval); yval += 30;
			debugMessage.draw(batch, "Doors: " + doors.size, 10, ScreenHeight - yval); yval += 30;
			debugMessage.draw(batch, "Mines: " + mines.size, 10, ScreenHeight - yval);
		}
		else {
			debugMessage.draw(batch, "Mode: play", 10, ScreenHeight - yval);
		}

		debugMessage.setColor(com.badlogic.gdx.graphics.Color.YELLOW);
		debugMessage.draw(batch, fpsDebug + Gdx.graphics.getFramesPerSecond(), ScreenWidth - 60, ScreenHeight - 10);

		yval = 10;
		debugMessage.setColor(com.badlogic.gdx.graphics.Color.RED);
		if(edit && (ScreenWidth != 1920 && ScreenHeight != 1080)) {
			debugMessage.draw(batch, "PLEASE DO NOT EDIT LEVELS AT A RESOLUTION OTHER THAN 1920x1080", ScreenWidth / 2 - 300,
					ScreenHeight / 2);
		}
		debugMessage.draw(batch, "Screen Width: " + ScreenWidth + " | Screen Height: " + ScreenHeight, (ScreenWidth / 2) - 30, ScreenHeight - yval); yval += 30;
		debugMessage.draw(batch, "Scale Width: " + scaleWidth + " | Scale Height: " + scaleHeight, (ScreenWidth / 2) - 30,
			ScreenHeight - yval); yval += 30;
		debugMessage.draw(batch, "Buffer Width: " + bufferWidth + " | Buffer Height: " + bufferHeight, (ScreenWidth / 2) - 30,
			ScreenHeight - yval); yval += 30;
		debugMessage.draw(batch, "Lines: " + lines + " | Midlines: " + midlines, (ScreenWidth/ 2) - 30,
			ScreenHeight - yval);

		if(testing) debugMessage.draw(batch, "TESTING", (ScreenWidth / 2) - 30, 30);
		debugMessage.draw(batch, "Saved: " + Edit.saved, (ScreenWidth / 2) - 30, 60);
		batch.end(); // Stop the batch drawing

		if(Edit.drawGrid) {
			int linewidth = 5;
			debugShapeRenderer.begin();
			debugShapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(Color.RED));
			for(int i = bufferWidth; i <= (scaleWidth + bufferWidth); i += midlines) {
				drawDottedLine(5, i, bufferHeight, i, (scaleHeight + bufferHeight));
			}
			for(int i = bufferHeight; i <= (scaleHeight + bufferHeight); i += midlines) {
				drawDottedLine(5, bufferWidth, i, (scaleWidth + bufferWidth), i);
			}
			debugShapeRenderer.setColor(Color.GRAY);
			for(int i = bufferWidth; i <= (scaleWidth + bufferWidth); i += lines) {
				debugShapeRenderer.line(i, bufferHeight, i, (scaleHeight + bufferHeight));
			}
			for(int i = bufferHeight; i <= (scaleHeight + bufferHeight); i += lines) {
				debugShapeRenderer.line(bufferWidth, i, (scaleWidth + bufferWidth), i);
			}
			debugShapeRenderer.end();
		}

		if(edit) {
			if(Edit.toolState == Edit.Tool.erase) {
				Gdx.gl.glEnable(GL20.GL_BLEND);
				Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				debugShapeRenderer.begin();
				debugShapeRenderer.set(ShapeRenderer.ShapeType.Filled);
				debugShapeRenderer.setColor(1, 0, 0, 0.5f);
				for(Box o : boxes) {
					if(o != null) {
						debugShapeRenderer.circle(o.center.x, o.center.y, (o.sprite.getWidth() * scalePercent) / 3);
					}
				}
				for(Triangle t : triangles) {
					if(t != null) {
						Vector2 v = new Vector2(0,0);
						float pad = (t.sprite.getWidth() / 4) * t.scale;
						if(t.triangle == UserData.TriangleType.botleft) {
							v = new Vector2(t.center.x - pad, t.center.y - pad);
						} else if(t.triangle == UserData.TriangleType.botright) {
							v = new Vector2(t.center.x + pad, t.center.y - pad);
						} else if(t.triangle == UserData.TriangleType.topleft) {
							v = new Vector2(t.center.x - pad, t.center.y + pad);
						} else if(t.triangle == UserData.TriangleType.topright) {
							v = new Vector2(t.center.x + pad, t.center.y + pad);
						}
						debugShapeRenderer.circle(v.x, v.y, (t.sprite.getWidth() * scalePercent) / 4);
					}
				}
				for(Modifier m : modifiers) {
					if(m != null) {
						Vector2 v = new Vector2(0,0);
						float pad = (m.sprite.getWidth() / 2) * m.scale;
						if(m.edge == UserData.Edge.top) {
							v = new Vector2(m.center.x, m.center.y + pad);
						} else if(m.edge == UserData.Edge.bot) {
							v = new Vector2(m.center.x, m.center.y - pad);
						} else if(m.edge == UserData.Edge.left) {
							v = new Vector2(m.center.x - pad, m.center.y);
						} else if(m.edge == UserData.Edge.right) {
							v = new Vector2(m.center.x + pad, m.center.y);
						}
						debugShapeRenderer.circle(v.x, v.y, (m.sprite.getWidth() * scalePercent) / 8);
					}
				}
				for(Goal o : goals) {
					if(o != null) {
						debugShapeRenderer.circle(o.center.x, o.center.y, (o.sprite.getWidth() * scalePercent) / 3);
					}
				}
				for(Door o : doors) {
					if(o != null) {
						debugShapeRenderer.circle(o.center.x, o.center.y, (o.sprite.getWidth() * scalePercent) / 8);
					}
				}
				for(DoorSwitch o : switches) {
					if(o != null) {
						debugShapeRenderer.circle(o.center.x, o.center.y, (o.sprite.getWidth() * scalePercent) / 8);
					}
				}
				for(Mine o : mines) {
					if(o != null) {
						debugShapeRenderer.circle(o.center.x, o.center.y, (o.sprite.getWidth() * scalePercent) / 8);
					}
				}
				for(Gun o : guns) {
					if(o != null) {
						debugShapeRenderer.circle(o.center.x, o.center.y, (o.sprite.getWidth() * scalePercent) / 8);
					}
				}
				debugShapeRenderer.end();
				Gdx.gl.glDisable(GL20.GL_BLEND);
			}
			debugShapeRenderer.begin();
			debugShapeRenderer.setColor(Color.PURPLE);
			for(int i = 0; i < doors.size && i < switches.size; i++) {
				Vector2 d = doors.get(i).center;
				Vector2 s = switches.get(i).center;
				debugShapeRenderer.line(d,s);
			}
			debugShapeRenderer.end();
		}
	}

	static void playGoalNoise() {
		goalNoise.play();
		playNotes = false;
	}

	static void playRipple() {
		playRipple = true;
		ripple = new Sprite(new Texture(Gdx.files.internal("art/ripple.png")));
		ripple.setCenter((ball.body.getPosition().x * PIXELS2METERS),
			(ball.body.getPosition().y * PIXELS2METERS));
		ripple.setScale(0.1f, 0.1f);
	}

	static void playNote(int i) {
		notes[i].play();
	}

	// !!! MOVE
	static boolean didDampen = false;
	static CollisionDetection.ImpulseType dampenType;

	public boolean keyDown (int keycode) { return false; }

	public boolean keyUp (int keycode) {
		return false;
	}

	public boolean keyTyped (char character) {
		return false;
	}

	public boolean touchDown (int x, int y, int pointer, int button) {
		if(!edit) {
			if(ballShot) {
				reset();
				reset = true;
			} else {
				mouseClick.x = x;
				mouseClick.y = ScreenHeight - y;
				touch = true;
				reset = false;
			}
		}
		return false;
	}

	public boolean touchUp (int x, int y, int pointer, int button) {
		// If we are not doing a reset of the level (reset is true when we click inside of
		// the gun's boundary
		if(!reset && !edit) {
			shoot = true;
			touch = false;
			mouseUnClick.x = x; mouseUnClick.y = y;
		}

		return false;
	}

	public boolean touchDragged (int x, int y, int pointer) {
		return false;
	}

	public boolean mouseMoved (int x, int y) {
		return false;
	}

	public boolean scrolled (int amount) {
		return false;
	}
}
