package com.example.formel.bazadrzewmobile.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.formel.bazadrzewmobile.R;
import com.example.formel.bazadrzewmobile.adapters.TreePicturesAdapter;
import com.example.formel.bazadrzewmobile.beans.TreeListBean;
import com.example.formel.bazadrzewmobile.helpers.AuthenticationHelper;
import com.example.formel.bazadrzewmobile.helpers.HttpHelper;
import com.example.formel.bazadrzewmobile.helpers.LocationHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TreeInfoActivity extends AppCompatActivity {
    @Bind(R.id.info_nazwa_latin_txt)
    TextView nameLatinTxt;
    @Bind(R.id.info_nazwa_polska_txt)
    TextView namePolishTxt;
    @Bind(R.id.info_adddate_txt)
    TextView addDateTxt;
    @Bind(R.id.info_city_txt)
    TextView cityTxt;
    @Bind(R.id.info_district_txt)
    TextView districtTxt;
    @Bind(R.id.info_description_txt)
    TextView descriptionTxt;
    @Bind(R.id.info_location_txt)
    TextView locationTxt;
    @Bind(R.id.info_login_txt)
    TextView loginTxt;
    @Bind(R.id.trees_grid_view)
    GridView gridView;

    TreeListBean treeInfo;
    Location location = null;
    LocationManager mLocationManager;
    HttpURLConnection conn;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_info);
        ButterKnife.bind(this);
        treeInfo = new Gson().fromJson(getIntent().getStringExtra("tree"), TreeListBean.class);
        nameLatinTxt.setText(treeInfo.getName_latin());
        namePolishTxt.setText(treeInfo.getName_polish());
        cityTxt.setText(treeInfo.getCity());
        districtTxt.setText(treeInfo.getDistrictName());
        descriptionTxt.setText(treeInfo.getDescription());
        locationTxt.setText(treeInfo.getLocation());
        loginTxt.setText(treeInfo.getLogin());


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (LocationHelper.checkLocationPermission(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Nie masz dostępu do usługi lokalizacji.", Toast.LENGTH_SHORT).show();
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 5,
                    10, mLocationListener);
        }


    }

    @OnClick(R.id.info_nawiguj)
    public void nawigujStart() {
        if(!isNumeric(treeInfo.getLocationLatitude()) || !isNumeric(treeInfo.getLocationLongitude())){
            Toast.makeText(getApplicationContext(), "Nieznana lokalizacja drzewa.", Toast.LENGTH_SHORT).show();
        }
        else{
            if (!LocationHelper.isLocationEnabled(getApplicationContext())) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(TreeInfoActivity.this);
                dialog.setTitle("Lokalizacja");
                dialog.setMessage("GPS jest wyłączony. Czy chcesz uruchomić ekran ustawień aby go włączyć?");
                dialog.setPositiveButton("Ustawienia", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        TreeInfoActivity.this.startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                dialog.show();
            } else {
                if(!LocationHelper.checkLocationPermission(getApplicationContext())){
                    location = mLocationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr="+location.getLatitude() +
                                    "," + location.getLongitude() + "&daddr="+treeInfo.getLocationLatitude()
                                    +","+ treeInfo.getLocationLongitude()));

                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Coś poszło nie tak.", Toast.LENGTH_SHORT).show();

                }
            }
        }


    }

    @OnClick(R.id.show_trees_btn)
    public void pokazZdjecia(){
        GetPicturesTask gpt = new GetPicturesTask();
        gpt.execute();
    }


    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("LOCATION", "STATUS: " + Integer.toString(status));
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("LOCATION", "ENABLED");
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(final Location mLocation) {
            location = mLocation;
            Log.d("LOCATION", Double.toString(location.getLatitude()));
        }
    };

    public class GetPicturesTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getApplicationContext());
            dialog.setMessage("Proszę czekać...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        @Override
        protected String doInBackground(Void... params) {

            try {
                URL urlToRequest = new URL("http://www.reichel.pl/bdp/webapi/web/get_pictures/" + treeInfo.getDescription());
                conn = (HttpURLConnection) urlToRequest.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(1000);
                conn.setRequestProperty("token", AuthenticationHelper.TOKEN);
                conn.setRequestProperty("Content-length", "0");
                conn.setUseCaches(false);
                conn.setAllowUserInteraction(false);
                conn.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                conn.disconnect();
                return sb.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return HttpHelper.EMPTY_RESPONSE;
        }

        @Override
        protected void onPostExecute(String result){
            Type jsonObjectColl = new TypeToken<List<String>>() {}.getType();
            List<String> treePictureResult = new Gson().fromJson(result, jsonObjectColl);
            if(treePictureResult.isEmpty()){
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Brak zdjęć dla obiektu", Toast.LENGTH_SHORT).show();
            }
            else{
                List<Bitmap> bitmaps = new ArrayList<Bitmap>();
                for(String picture : treePictureResult){
                    URL url = null;
                    try {
                        url = new URL("http://http://reichel.pl/bdp/" + picture);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    try {
                        bitmaps.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                gridView.setAdapter(new TreePicturesAdapter(TreeInfoActivity.this, bitmaps));
                gridView.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }

        }
    }

}
