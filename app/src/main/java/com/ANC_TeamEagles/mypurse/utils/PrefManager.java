package com.ANC_TeamEagles.mypurse.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by Administrator on 8/3/2017.
 */

public class PrefManager {
    SharedPreferences pref;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "MyPurse-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefManager(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        pref.edit().putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime).apply();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void saveUserEmail(String email){
       pref.edit().putString(Constants.KEY_EMAIL,encodeEmail(email)).apply();
    }

    public String getUserEmail(){
        return pref.getString(Constants.KEY_EMAIL, Constants.DEFAULT_EMAIL);
    }

    public static String encodeEmail(String email){
        return email.replace(".",",");
    }
}
