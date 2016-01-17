package com.example.formel.bazadrzewmobile.helpers;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by formel on 17.01.16.
 */
public class DialogHelper {
    private static ProgressDialog progressDialog = null;

    public static void setProgressDialog(Context ctx){
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public static void setMessage(String text){
        progressDialog.setMessage(text);
    }

    public static void dismiss(){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    public static void show(){
        if(progressDialog != null && !progressDialog.isShowing()){
            progressDialog.show();
        }
    }

}
