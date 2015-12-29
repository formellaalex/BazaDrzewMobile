package com.example.formel.bazadrzewmobile.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.formel.bazadrzewmobile.R;
import com.example.formel.bazadrzewmobile.beans.TreeListBean;
import com.google.gson.Gson;

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

    TreeListBean treeInfo;
    Location location = null;
    LocationManager mLocationManager;

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

        if (checkLocationPermission()) {
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
            if (!isLocationEnabled(getApplicationContext())) {
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
                if(!checkLocationPermission()){
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

    public boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


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

}
