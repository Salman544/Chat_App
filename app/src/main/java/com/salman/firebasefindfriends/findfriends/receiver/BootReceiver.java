package com.salman.firebasefindfriends.findfriends.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.salman.firebasefindfriends.findfriends.service.BackgroundServices;


public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

            Intent i = new Intent(context, BackgroundServices.class);
            context.startService(i);

    }
}
