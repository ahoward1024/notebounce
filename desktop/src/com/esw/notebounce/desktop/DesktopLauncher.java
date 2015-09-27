package com.esw.notebounce.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.esw.notebounce.NoteBounce;

public class DesktopLauncher {

	public static int ScreenWidth  = 800;
	public static int ScreenHeight = 480;

	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width  = ScreenWidth;
		config.height = ScreenHeight;
		config.resizable = true;
		config.useHDPI = true;
		new LwjglApplication(new NoteBounce(ScreenWidth, ScreenHeight), config);
	}
}
