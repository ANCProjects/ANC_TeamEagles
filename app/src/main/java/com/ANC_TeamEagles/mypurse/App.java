package com.ANC_TeamEagles.mypurse;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by nezspencer on 8/8/17.
 */

public class App extends Application {

    public static FirebaseDatabase appDatabase;
    @Override
    public void onCreate() {
        super.onCreate();
        appDatabase = FirebaseDatabase.getInstance();
        appDatabase.setPersistenceEnabled(true);
    }

}
