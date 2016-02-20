package com.dandddeveloper.leibnizvp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by denis_000 on 02.04.2015.
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "MyActivity";
    @Override
    public void onReceive(Context context, Intent intent) {
        String i = "Log recieved";
        Log.v(TAG, "index=" + i);
        SettingsActivity.displayNotificationActivity(context);
    }

}
