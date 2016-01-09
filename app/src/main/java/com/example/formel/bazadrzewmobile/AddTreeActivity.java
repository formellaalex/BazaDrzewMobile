package com.example.formel.bazadrzewmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.formel.bazadrzewmobile.helpers.LocationHelper;
import com.example.formel.bazadrzewmobile.tasks.AddTreeTask;
import com.example.formel.bazadrzewmobile.tasks.GeocoderTask;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTreeActivity extends AppCompatActivity {

    EditText dataDodaniaTxt;
    EditText nazwaLatinTxt;
    EditText nazwaPolskaTxt;
    AddTreeTask addTreeTask;
    GeocoderTask geocoderTask;
    ProgressDialog progressDialog;
    ConnectionService connectionService;

    @Bind(R.id.wpisz_lokaliz_btn)
    Button wpiszLokalizacjeBtn;

    @Bind(R.id.lokaliz_btn)
    Button otworzMapeBtn;

    @Bind(R.id.aktualna_lokaliz_btn)
    Button aktualnaLokaliz;

    @Bind(R.id.add_complete_tree_btn)
    Button addTreeBtn;

    @Bind(R.id.lokaliz_txt)
    TextView lokalizacjaTxt;

    LocationManager mLocationManager;

    Location location = null;

    public static final int OSM_ACTIVITY_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tree);
        dataDodaniaTxt = (EditText) findViewById(R.id.data_dodania_txt);
        nazwaLatinTxt = (EditText) findViewById(R.id.nazwa_latin_txt);
        nazwaPolskaTxt = (EditText) findViewById(R.id.nazwa_polska_txt);
        ButterKnife.bind(this);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        dataDodaniaTxt.setText(dateFormat.format(date));
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        progressDialog = new ProgressDialog(AddTreeActivity.this);
        progressDialog .setMessage("Lokalizuję...");
        progressDialog .setCanceledOnTouchOutside(false);
        connectionService = new ConnectionService(getApplicationContext());
        geocoderTask = new GeocoderTask(AddTreeActivity.this);

        geocoderTask.setAddressDownloadListener(new GeocoderTask.AddressDownloadListener() {
            @Override
            public void onFinishedDownloadingData(Location location) {
                lokalizacjaTxt.setText(location.getLatitude() + "," + location.getLongitude());
            }
        });
        if (!LocationHelper.isLocationEnabled(AddTreeActivity.this)) {
            LocationHelper.showLocationDialog(AddTreeActivity.this);
        } else {
            if (!LocationHelper.checkLocationPermission(AddTreeActivity.this)) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 2,
                        10, mLocationListener);
            }
        }

    }

    @OnClick(R.id.add_complete_tree_btn)
    public void dodajDrzewo(){
//                if (TextUtils.isEmpty(nazwaLatinTxt.getText().toString())) {
//                    nazwaLatinTxt.setError(getString(R.string.error_field_required));
//                } else if (TextUtils.isEmpty(dataDodaniaTxt.getText().toString())) {
//                    dataDodaniaTxt.setError(getString(R.string.error_field_required));
//                } else if (TextUtils.isEmpty(nazwaPolskaTxt.getText().toString())) {
//                    nazwaPolskaTxt.setError(getString(R.string.error_field_required));
//                } else {
            URL url = null;
            try {
                url = new URL("http://www.reichel.pl/bdp/webapi/web/app.php/add_tree");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("lat", 10);
                jsonParams.put("lon", 20);
                jsonParams.put("name_latin", nazwaLatinTxt.getText().toString());
                jsonParams.put("name_polish", nazwaPolskaTxt.getText().toString());
                jsonParams.put("add_date", dataDodaniaTxt.getText().toString());
                addTreeTask = new AddTreeTask(jsonParams, getApplicationContext());
                addTreeTask.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    @OnClick(R.id.wpisz_lokaliz_btn)
    public void pokazLokalizTxt(){

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        final EditText adresTxt = new EditText(AddTreeActivity.this);
        alertBuilder.setMessage("Wpisz adres pod jakim znajduje sie drzewo.");
        alertBuilder.setTitle("Adres");

        alertBuilder.setView(adresTxt);

        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (connectionService.isOnline()) {
                    String address = adresTxt.getText().toString();
                    geocoderTask.execute(address);
                } else {
                    Toast.makeText(getApplicationContext(), "Nie masz dostępu do internetu!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        alertBuilder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alertBuilder.show();


    }

    @OnClick(R.id.aktualna_lokaliz_btn)
    public void pobierzAktualnaLokalizacje(){
        if(location == null){
            progressDialog.show();
        }
        else{
            lokalizacjaTxt.setText(location.getLatitude() + "," + location.getLongitude());
        }
    }


    @OnClick(R.id.lokaliz_btn)
    public void otworzMape() {
            if (!LocationHelper.isLocationEnabled(AddTreeActivity.this)) {
                LocationHelper.showLocationDialog(AddTreeActivity.this);
            } else {
                if (!LocationHelper.checkLocationPermission(AddTreeActivity.this)) {
                    Intent osmActivity = new Intent(AddTreeActivity.this, OSMActivity.class);
                    if(location == null){
                        progressDialog.show();
                    }
                    else{
                        osmActivity.putExtra("LAT", location.getLatitude());
                        osmActivity.putExtra("LON", location.getLongitude());
                        startActivityForResult(osmActivity, OSM_ACTIVITY_CODE);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Nie masz dostępu do usług lokalizacji.", Toast.LENGTH_SHORT).show();

                }
            }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OSM_ACTIVITY_CODE){
            Log.d("OSM", "POSZŁOOOOOOOOOOOOOO");
            Log.d("OSM", "Kod: " + resultCode);
            switch(resultCode){

                case Activity.RESULT_OK:
                    Log.d("OSM", "YES YES YES");
                    lokalizacjaTxt.setText(data.getDoubleExtra("LAT", 0) + "," + data.getDoubleExtra("LON", 0));
                    break;
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
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            location = mLocation;
        }
    };

}
