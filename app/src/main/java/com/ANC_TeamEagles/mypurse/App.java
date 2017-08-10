package com.ANC_TeamEagles.mypurse;

import android.app.Application;

import com.ANC_TeamEagles.mypurse.utils.PrefManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by nezspencer on 8/8/17.
 */

public class App extends Application {

    private FirebaseDatabase appDatabase;
    public static DatabaseReference userRef;
    @Override
    public void onCreate() {
        super.onCreate();
        appDatabase = FirebaseDatabase.getInstance();
        appDatabase.setPersistenceEnabled(true);
        PrefManager manager = new PrefManager(this);
        userRef = appDatabase.getReference().child(manager.getUserEmail());
    }

}
