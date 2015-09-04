package com.esw.notebounce.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.esw.notebounce.NoteBounce;

public class AndroidLauncher extends AndroidApplication {
	public static int ScreenWidth  = 1920;
	public static int ScreenHeight = 1080;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new NoteBounce(ScreenWidth, ScreenHeight), config);
	}
}
