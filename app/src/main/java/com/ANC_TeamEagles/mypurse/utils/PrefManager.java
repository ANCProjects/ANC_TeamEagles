package com.ANC_TeamEagles.mypurse.utils;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Administrator on 8/3/2017.
 */

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "MyPurse-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor = pref.edit();
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void saveUserEmail(String email){
        editor = pref.edit();
        editor.putString(Constants.KEY_EMAIL,encodeEmail(email))
                .apply();
    }

    public String getUserEmail(){
        return pref.getString(Constants.KEY_EMAIL,"anonymous");
    }

    public static String encodeEmail(String email){
        return email.replace(".",",");
    }
}
