package com.example.formel.bazadrzewmobile.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * Created by formel on 06.01.16.
 */
public class GeocoderTask extends AsyncTask<String, Void, Address> {

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
    protected Address doInBackground(String... params) {
        String response;
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> adress = geocoder.getFromLocationName(params[0], 1);
            if(adress.size() == 0){
                return null;
            }
            return adress.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Address address) {
        if(null == address){
            Toast.makeText(ctx, "Nie znaleziono adresu", Toast.LENGTH_SHORT).show();
        }
        else {
//        JSONObject jsonObject = null;
//        try {
//            jsonObject = new JSONObject(result[0]);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        double lng = 0;
//        try {
//            lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
//                    .getJSONObject("geometry").getJSONObject("location")
//                    .getDouble("lng");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        double lat = 0;
//        try {
//            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
//                    .getJSONObject("geometry").getJSONObject("location")
//                    .getDouble("lat");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

            Log.d("latitude", "" + address.getLatitude());
            Log.d("longitude", "" + address.getLongitude());
            Location location = new Location("");
            location.setLongitude(address.getLongitude());
            location.setLatitude(address.getLatitude());
            addressDownloadListener.onFinishedDownloadingData(location);

        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static interface AddressDownloadListener{
        public void onFinishedDownloadingData(Location location);
    }
}