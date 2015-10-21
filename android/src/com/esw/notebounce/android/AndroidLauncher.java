package com.esw.notebounce.android;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.esw.notebounce.NoteBounce;

public class AndroidLauncher extends AndroidApplication {
	public static int ScreenWidth  = 1920; // DEFAULT
	public static int ScreenHeight = 1080; // DEFAULT

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		// NOTE: We _MUST_ get the screen size for android or else _nothing_ will work right.
		// This includes input on the screen/where things are drawn.
		// According to the newest android API, we should be using a Display and Point objects and
		// calling display.getSize(point) but our target API for LibGDX is 8, not 13.
		// The deprecated methods display.getWidth()/display.getHeight() will do for now.
		Display display = getWindowManager().getDefaultDisplay();

		if(Build.VERSION.SDK_INT >= 13) {
			Point point = new Point();
			display.getSize(point);
			ScreenWidth = point.x;
			ScreenHeight = point.y;
		} else {
			ScreenWidth = display.getWidth(); // COMPATIBILITY ??? STC
			ScreenHeight = display.getHeight(); // COMPATIBILITY ??? STC
		}
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new NoteBounce(ScreenWidth, ScreenHeight), config);
	}
}
