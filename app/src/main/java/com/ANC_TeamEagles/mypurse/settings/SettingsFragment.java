package com.ANC_TeamEagles.mypurse.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.widget.Toast;

import com.ANC_TeamEagles.mypurse.MainActivity;
import com.ANC_TeamEagles.mypurse.R;
import com.ANC_TeamEagles.mypurse.utils.Constants;


/**
 * Created by nezspencer on 9/13/17.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {


    private final static String TAG = SettingsFragment.class.getName();
    public final static String SETTINGS_SHARED_PREFERENCES_FILE_NAME = TAG + ".SETTINGS_SHARED_PREFERENCES_FILE_NAME";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //getPreferenceManager().setSharedPreferencesName(SETTINGS_SHARED_PREFERENCES_FILE_NAME);
        //getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
        addPreferencesFromResource(R.xml.pref_purse);

        EditTextPreference accEdit = (EditTextPreference)findPreference("start_bal_key");
        String accStr = getPreferenceScreen().getSharedPreferences().getString("start_bal_key","0");
        accEdit.setDefaultValue(accStr);

        EditTextPreference expenEdit = (EditTextPreference)findPreference("expendable_key");
        String expenStr = getPreferenceScreen().getSharedPreferences().getString("expendable_key",
                "0");
        expenEdit.setDefaultValue(expenStr);

        EditTextPreference thresEdit = (EditTextPreference)findPreference("low_threshold_key");
        String threStr = getPreferenceScreen().getSharedPreferences().getString("low_threshold_key",
                "0");
        thresEdit.setDefaultValue(threStr);


        Log.d(MainActivity.TAG,getPreferenceManager().getSharedPreferencesName());
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);






    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Log.d(MainActivity.TAG," on pref changed");
        double accBal = 0;
        double expendableAmt = 0;
        double thresholdAmt = 0;

        String keyAccBal = getActivity().getString(R.string.pref_key_start_balance);
        String keyExpendable = getActivity().getString(R.string.pref_key_expendable);
        String keyThreshold = getActivity().getString(R.string.pref_key_expendable_threshold);

        try {
             accBal = Double.parseDouble(sharedPreferences.getString(keyAccBal,"0"));
        }
        catch (NumberFormatException e){
            accBal = sharedPreferences.getLong(Constants.KEY_ACC_BAL_AMT,0);
        }

        try {
            expendableAmt = Double.parseDouble(sharedPreferences.getString(keyExpendable,"0"));
        }
        catch (NumberFormatException e){
            expendableAmt = sharedPreferences.getLong(Constants.KEY_AMOUNT_TO_SPEND,0);
        }

        try {
            thresholdAmt = Double.parseDouble(sharedPreferences.getString(keyThreshold,"0"));
        }
        catch (NumberFormatException e){
            thresholdAmt = sharedPreferences.getLong(Constants.KEY_THRESHOLD_AMT,0);
        }




        double amt =0;
        String strAmt ="x";
        if (key.equals(keyAccBal)){
            try{
                strAmt = sharedPreferences.getString(key,null);
                amt = Double.parseDouble(strAmt);

                if (amt < 0){
                    Toast.makeText(getActivity(),"Number should be positive",Toast.LENGTH_SHORT)
                            .show();
                    sharedPreferences.edit().putString(key,""+accBal).apply();
                }
            }
            catch (NumberFormatException e){
                Toast.makeText(getActivity(),strAmt+" is not a number",Toast.LENGTH_SHORT).show();
                sharedPreferences.edit().putString(key,""+accBal).apply();
            }
        }
        else if (key.equals(keyExpendable)){

            try {
                strAmt = sharedPreferences.getString(key,null);
                //check if its a valid number
                amt = Double.parseDouble(strAmt);


                if (amt < 0) {
                    Toast.makeText(getActivity(), "Number should be positive", Toast.LENGTH_SHORT)
                            .show();
                    sharedPreferences.edit().putString(key,""+expendableAmt).apply();
                }
                else if (amt > accBal)
                {
                    Toast.makeText(getActivity(),"Amount cannot be greater than start balance",Toast.LENGTH_SHORT)
                            .show();
                    sharedPreferences.edit().putString(key,""+expendableAmt).apply();

                }



            }
            catch (NumberFormatException e){
                Toast.makeText(getActivity(),strAmt+" is not a number",Toast.LENGTH_SHORT).show();
                sharedPreferences.edit().putString(key,""+expendableAmt).apply();
            }

        }

        else if (key.equals(keyThreshold)){
            try {
                strAmt = sharedPreferences.getString(key,null);
                //check if its a valid number
                amt = Double.parseDouble(strAmt);


                if (amt < 0) {
                    Toast.makeText(getActivity(), "Number should be positive", Toast.LENGTH_SHORT)
                            .show();
                    sharedPreferences.edit().putString(key,""+thresholdAmt).apply();
                }
                else if (amt > expendableAmt)
                {
                    Toast.makeText(getActivity(),"Amount cannot be greater than expendable amount",
                            Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putString(key,""+thresholdAmt).apply();

                }



            }
            catch (NumberFormatException e){
                Toast.makeText(getActivity(),strAmt+" is not a number",Toast.LENGTH_SHORT).show();
                sharedPreferences.edit().putString(key,""+thresholdAmt).apply();
            }
        }
    }
}
