package com.example.formel.bazadrzewmobile.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.example.formel.bazadrzewmobile.helpers.LocationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by formel on 06.01.16.
 */
public class GeocoderTask extends AsyncTask<String, Void, String[]> {

    ProgressDialog dialog;
    Context ctx;

    AddressDownloadListener addressDownloadListener;

    public GeocoderTask(Context ctx){
        this.ctx = ctx;
    }


    public void setAddressDownloadListener(AddressDownloadListener addressDownloadListener){
        this.addressDownloadListener = addressDownloadListener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(ctx);
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected String[] doInBackground(String... params) {
        String response;
        try {
            response = LocationHelper.getLatLongByURL("http://maps.google.com/maps/api/geocode/json?address=" + params[0] + "&sensor=false");
            Log.d("response", "" + response);
            return new String[]{response};
        } catch (Exception e) {
            return new String[]{"error"};
        }
    }

    @Override
    protected void onPostExecute(String... result) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        double lng = 0;
        try {
            lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        double lat = 0;
        try {
            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("latitude", "" + lat);
        Log.d("longitude", "" + lng);
        Location location = new Location("");
        location.setLongitude(lng);
        location.setLatitude(lat);
        addressDownloadListener.onFinishedDownloadingData(location);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static interface AddressDownloadListener{
        public void onFinishedDownloadingData(Location location);
    }
}