package com.ANC_TeamEagles.mypurse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ANC_TeamEagles.mypurse.utils.Constants;
import com.ANC_TeamEagles.mypurse.utils.PrefManager;

/**
 * Created by nezspencer on 8/23/17.
 */

public class NetworkChecker extends BroadcastReceiver {

    private MainActivity activity;
    private ConnectivityManager connectivityManager;
    private PrefManager manager;

    public NetworkChecker() {
        super();
    }

    public NetworkChecker(MainActivity activity) {
        super();
        this.activity = activity;
        manager = new PrefManager(activity);
        connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!isNetworkAvailable() && manager.getUserEmail().equals(Constants.DEFAULT_EMAIL)){
            activity.hideNoConnectivityDialog();
            activity.showNoConnectivityDialog();
        }
        else
            activity.hideNoConnectivityDialog();

    }

    private boolean isNetworkAvailable() {

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
