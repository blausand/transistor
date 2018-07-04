/**
 * ShortcutHelper.java
 * Implements the ShortcutHelper class
 * A ShortcutHelper creates and handles station shortcuts on the Home screen
 *
 * This file is part of
 * TRANSISTOR - Radio App for Android
 *
 * Copyright (c) 2015-18 - Y20K.org
 * Licensed under the MIT-License
 * http://opensource.org/licenses/MIT
 */


package net.blausand.wickedwatch.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.widget.Toast;

import net.blausand.wickedwatch.MainActivity;
import net.blausand.wickedwatch.R;
import net.blausand.wickedwatch.core.Station;


/**
 * ShortcutHelper class
 */
public final class ShortcutHelper implements TransistorKeys {

    /* Define log tag */
    private static final String LOG_TAG = ShortcutHelper.class.getSimpleName();


    /* Places shortcut on Home screen */
    public static void placeShortcut(Context context, Station station) {

        // credit: https://medium.com/@BladeCoder/using-support-library-26-0-0-you-can-do-bb75911e01e8
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(context, station.getStationName())
                    .setShortLabel(station.getStationName())
                    .setLongLabel(station.getStationName())
                    .setIcon(IconCompat.createWithBitmap(createShortcutIcon(context, station)))
                    .setIntent(createShortcutIntent(context, station))
                    .build();
            ShortcutManagerCompat.requestPinShortcut(context, shortcut, null);
            Toast.makeText(context, R.string.toastmessage_shortcut_created, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, R.string.toastmessage_shortcut_not_created, Toast.LENGTH_LONG).show();
        }

    }


    /* Removes shortcut for given station from Home screen */
    public static void removeShortcut(Context context, Station station) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // from API level 26 ("Android O") on shortcuts are handled by ShortcutManager, which cannot remove shortcuts. The user must remove them manually.
        } else {
            // the pre 26 way: create and launch intent put shortcut on Home screen
            Intent removeIntent = new Intent();
            removeIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, station.getStationName());
            removeIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, createShortcutIcon(context, station));
            removeIntent.putExtra("duplicate", false);
            removeIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, createShortcutIntent(context, station));
            removeIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
            context.getApplicationContext().sendBroadcast(removeIntent);
        }
    }


    /* Creates Intent for a station shortcut */
    private static Intent createShortcutIntent (Context context, Station station) {

        String stationUri = station.getStreamUri().toString();

        // create intent to start MainActivity
        Intent shortcutIntent = new Intent(context, MainActivity.class);
        shortcutIntent.setAction(ACTION_SHOW_PLAYER);
        shortcutIntent.putExtra(EXTRA_STREAM_URI, stationUri);
        shortcutIntent.putExtra(EXTRA_PLAYBACK_STATE, true);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        LogHelper.v(LOG_TAG, "Intent for Home screen shortcut: " + shortcutIntent.toString() + " Activity: " + context);

        return shortcutIntent;
    }


    /* Create shortcut icon */
    private static Bitmap createShortcutIcon(Context context, Station station) {
        ImageHelper imageHelper = new ImageHelper(station, context);
        return imageHelper.createShortcut(192);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // only return the station image
//            return imageHelper.getInputImage();
//        } else {
//            // return station image in circular frame
//            return imageHelper.createShortcut(192);
//        }

    }

}