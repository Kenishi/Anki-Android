/****************************************************************************************
* Copyright (c) 2009 Edu Zamora <edu.zasu@gmail.com> 									*
* Copyright (c) 2009 Casey Link <unnamedrambler@gmail.com> 								*
* 																						*
* This program is free software; you can redistribute it and/or modify it under 		*
* the terms of the GNU General Public License as published by the Free Software 		*
* Foundation; either version 3 of the License, or (at your option) any later 			*
* version. 																				*
* 																						*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY 		*
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 		*
* PARTICULAR PURPOSE. See the GNU General Public License for more details. 				*
* 																						*
* You should have received a copy of the GNU General Public License along with 			*
* this program. If not, see <http://www.gnu.org/licenses/>. 							*
****************************************************************************************/

package com.ichi2.anki;

import java.io.File;
import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.ichi2.async.Connection;
import com.tomgibara.android.veecheck.Veecheck;
import com.tomgibara.android.veecheck.util.PrefSettings;

/**
 * Application class. This file mainly contains Veecheck stuff.
 */
public class AnkiDroidApp extends Application {

	private static final String TAG = "AnkiDroid";

	private static ArrayList<String> cursorMethods = new ArrayList<String>();
	private static ArrayList<String> cursorNames = new ArrayList<String>();

	/**
	 * Singleton instance of this class.
	 */
	private static AnkiDroidApp instance;

	/**
	 * Currently loaded Anki deck.
	 */
	private Deck loadedDeck;

	/**
	 * On application creation.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		Connection.setContext(getApplicationContext());

		// Error Reporter
		CustomExceptionHandler customExceptionHandler = CustomExceptionHandler.getInstance();
		customExceptionHandler.Init(instance.getApplicationContext());
		Thread.setDefaultUncaughtExceptionHandler(customExceptionHandler);
		
		SharedPreferences preferences = PrefSettings.getSharedPrefs(this);
		// Assign some default settings if necessary
		if (preferences.getString(PrefSettings.KEY_CHECK_URI, null) == null) {
			Editor editor = preferences.edit();
			// Test Update Notifications
			// Some ridiculously fast polling, just to demonstrate it working...
			/*
			 * editor.putBoolean(PrefSettings.KEY_ENABLED, true);
			 * editor.putLong(PrefSettings.KEY_PERIOD, 30 * 1000L);
			 * editor.putLong(PrefSettings.KEY_CHECK_INTERVAL, 60 * 1000L);
			 * editor.putString(PrefSettings.KEY_CHECK_URI,
			 * "http://ankidroid.googlecode.com/files/test_notifications.xml");
			 */
			editor.putString(PrefSettings.KEY_CHECK_URI,
					"http://ankidroid.googlecode.com/files/last_release.xml");

			// Create the folder "AnkiDroid", if not exists, where the decks
			// will be stored by default
			new File(getStorageDirectory() + "/AnkiDroid").mkdir();

			// Put the base path in preferences pointing to the default
			// "AnkiDroid" folder
			editor.putString("deckPath", getStorageDirectory() + "/AnkiDroid");

			editor.commit();
		}

		// Reschedule the checks - we need to do this if the settings have
		// changed (as above)
		// It may also necessary in the case where an application has been
		// updated
		// Here for simplicity, we do it every time the application is launched
		Intent intent = new Intent(Veecheck.getRescheduleAction(this));
		sendBroadcast(intent);
	}

	public static AnkiDroidApp getInstance() {
		return instance;
	}

	public static String getStorageDirectory() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	public static Resources getAppResources() {
		return instance.getResources();
	}

	public static Deck deck() {
		return instance.loadedDeck;
	}

	public static void setDeck(Deck deck) {
		instance.loadedDeck = deck;
	}

	public static boolean isSdCardMounted() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	public static boolean isUserLoggedIn() {
		SharedPreferences preferences = PrefSettings.getSharedPrefs(instance);
		String username = preferences.getString("username", "");
		String password = preferences.getString("password", "");

		if (!username.equalsIgnoreCase("") && !password.equalsIgnoreCase("")) {
			return true;
		}

		return false;
	}
	
	public static int getDisplayHeight()
	{
		Display display = ((WindowManager) instance.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		return display.getHeight();
	}
	
	public static int getDisplayWidth()
	{
		Display display = ((WindowManager) instance.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		return display.getWidth();
	}
	
	public static void registerCursor(String method, String name) {
		cursorMethods.add(method);
		cursorNames.add(name);
	}

	public static void logCursors() {
		for (int i = 0; i < cursorMethods.size(); i++) {
			Log.i(TAG, "Cursor " + cursorNames.get(i));
			Log.i(TAG, " on method " + cursorMethods.get(i));
		}
	}
	
}