package com.esw.notebounce.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.esw.notebounce.notebounce;

public class AndroidLauncher extends AndroidApplication {
	public static int ScreenWidth  = 960;
	public static int ScreenHeight = 540;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new notebounce(ScreenWidth, ScreenHeight), config);
	}
}
