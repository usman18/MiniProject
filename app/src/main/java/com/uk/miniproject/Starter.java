package com.uk.miniproject;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by usman on 03-10-2018.
 */

public class Starter extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //enabling offline persistence, such a saviour
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
