package com.salman.firebasefindfriends.findfriends.ui;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;


public class FirebaseBackup extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
