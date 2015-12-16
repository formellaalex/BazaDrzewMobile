package com.example.formel.bazadrzewmobile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by formel on 07.11.15.
 */
public class ConnectionService {

    Context mContext;
    public ConnectionService(Context mContext) {
        this.mContext = mContext;
    }


    public boolean isOnline() {
        ConnectivityManager cm;
        cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
