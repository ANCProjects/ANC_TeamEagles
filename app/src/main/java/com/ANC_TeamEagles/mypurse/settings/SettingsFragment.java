package com.ANC_TeamEagles.mypurse.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.ANC_TeamEagles.mypurse.MainActivity;
import com.ANC_TeamEagles.mypurse.R;



/**
 * Created by nezspencer on 9/13/17.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {


    private final static String TAG = SettingsFragment.class.getName();
    public final static String SETTINGS_SHARED_PREFERENCES_FILE_NAME = TAG + ".SETTINGS_SHARED_PREFERENCES_FILE_NAME";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName(SETTINGS_SHARED_PREFERENCES_FILE_NAME);
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
        addPreferencesFromResource(R.xml.pref_purse);
        Log.d(MainActivity.TAG,getPreferenceManager().getSharedPreferencesName());
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);






    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Log.d(MainActivity.TAG," on pref changed");
    }
}
