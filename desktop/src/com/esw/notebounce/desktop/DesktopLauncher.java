package com.esw.notebounce.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.esw.notebounce.NoteBounce;

public class DesktopLauncher {

	public static int ScreenWidth  = 1280;
	public static int ScreenHeight = 720;

	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width  = ScreenWidth;
		config.height = ScreenHeight;
		//config.resizable = false; // Not needed! Everything seems to scale automagically!!! Yay!!
		new LwjglApplication(new NoteBounce(ScreenWidth, ScreenHeight), config);
	}
}
