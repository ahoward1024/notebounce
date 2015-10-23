package com.esw.notebounce.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.esw.notebounce.NoteBounce;

import java.awt.Dimension;
import java.awt.Toolkit;

public class DesktopLauncher {

	public static int ScreenWidth  = 1920;
	public static int ScreenHeight = 1080;

	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		System.out.println("Native: " + screenSize.getSize());
		// If the native resoluiton of the monitor is 1920x1080 then automatically
		// go to fullscreen (this is so I can edit levels on my laptop [when windows
		// goes to make a window where the window resolution is greater than the monitor's
		// resolution in either dimension it does a slight scaling to keep the window
		// bounds inside the monitor. This throws off grid snapping])
		if(screenSize.getWidth() == 1920 && screenSize.getHeight() == 1080) {
			config.width = screenSize.width;
			config.height = screenSize.height;
			config.resizable = false;
			config.fullscreen = true;
		} else {
			config.width = ScreenWidth;
			config.height = ScreenHeight;
			config.resizable = true;
		}
		config.useHDPI = true;
		new LwjglApplication(new NoteBounce(ScreenWidth, ScreenHeight), config);
	}
}
