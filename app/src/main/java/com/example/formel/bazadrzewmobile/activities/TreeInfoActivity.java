package com.example.formel.bazadrzewmobile.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.formel.bazadrzewmobile.R;
import com.example.formel.bazadrzewmobile.adapters.TreePicturesAdapter;
import com.example.formel.bazadrzewmobile.beans.PictureBean;
import com.example.formel.bazadrzewmobile.beans.TreeListBean;
import com.example.formel.bazadrzewmobile.helpers.LocationHelper;
import com.example.formel.bazadrzewmobile.helpers.MediaHelper;
import com.example.formel.bazadrzewmobile.tasks.GetImagesFromUrlTask;
import com.example.formel.bazadrzewmobile.tasks.GetPicturesListTask;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
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
    @Bind(R.id.info_show_pictures)
    Button infoShowPictures;
    @Bind(R.id.info_is_pomnik_txt)
    TextView ispomnikTxt;
    @Bind(R.id.info_in_greenhouse_txt)
    TextView inGreenhouseTxt;
    @Bind(R.id.info_street_txt)
    TextView streetTxt;

    TreeListBean treeInfo;
    Location location = null;
    LocationManager mLocationManager;
    HttpURLConnection conn;
    ProgressDialog dialog;

    GetPicturesListTask getPicturesTask;

    GetImagesFromUrlTask getImagesFromUrlTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_info);
        ButterKnife.bind(this);
        treeInfo = new Gson().fromJson(getIntent().getStringExtra("tree"), TreeListBean.class);
        nameLatinTxt.setText(treeInfo.nameLatin);
        namePolishTxt.setText(treeInfo.namePolish);
        cityTxt.setText(treeInfo.city);
        districtTxt.setText(treeInfo.districtName);
        descriptionTxt.setText(treeInfo.description);
        locationTxt.setText(treeInfo.location);
        loginTxt.setText(treeInfo.login);
        streetTxt.setText(treeInfo.street);
        if(treeInfo.isPomnik == 0){
            ispomnikTxt.setText("Nie");
        }
        else{
            ispomnikTxt.setText("Tak");
        }

        if(treeInfo.inGreenhouse == 0){
            inGreenhouseTxt.setText("Nie");
        }
        else{
            inGreenhouseTxt.setText("tak");
        }
        getPicturesTask = new GetPicturesListTask(TreeInfoActivity.this,treeInfo.idtreeObjects);
        getPicturesTask.setPicturesListDownloadListener(new GetPicturesListTask.PicturesListDownloadListener() {

            @Override
            public void onFinishedPictureDownload(List<PictureBean> bitmaps,final ProgressDialog dialog) {
                if(!bitmaps.isEmpty()){
                    getImagesFromUrlTask = new GetImagesFromUrlTask(bitmaps);
                    getImagesFromUrlTask.setImagesFromUrlDownloadListener(new GetImagesFromUrlTask.ImagesFromUrlDownloadListener() {
                        @Override
                        public void onFinishedImagesDownload(List<Drawable> drawables) {
                            gridView.setAdapter(new TreePicturesAdapter(TreeInfoActivity.this, drawables));
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                            gridView.setVisibility(View.VISIBLE);}
                    });
                    getImagesFromUrlTask.execute();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Brak zdjęć.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (LocationHelper.checkLocationPermission(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Nie masz dostępu do usługi lokalizacji.", Toast.LENGTH_SHORT).show();
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 2,
                    10, mLocationListener);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                BitmapDrawable item = (BitmapDrawable) parent.getItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(TreeInfoActivity.this, ImageFullScreenActivity.class);
                Bitmap bitmap = item.getBitmap();
                Uri uri = MediaHelper.storeImageAndGetUri(TreeInfoActivity.this, bitmap);
                intent.putExtra("uri", uri);
                //Start details activity
                startActivity(intent);
            }
        });


    }


    @OnClick(R.id.info_nawiguj)
    public void nawigujStart() {
        if(!LocationHelper.isNumeric(treeInfo.locationLatitude) || !LocationHelper.isNumeric(treeInfo.locationLongitude)){
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
                    if(null == location) {
                        Toast.makeText(getApplicationContext(), "Poczekaj na pobranie Twojej lokalizacji", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        location = mLocationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr="+location.getLatitude() +
                                        "," + location.getLongitude() + "&daddr="+treeInfo.locationLatitude
                                        +","+ treeInfo.locationLongitude));

                        startActivity(intent);
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(), "Coś poszło nie tak", Toast.LENGTH_SHORT).show();

                }
            }
        }


    }

    @OnClick(R.id.info_show_pictures)
    public void pokazZdjecia(){
        infoShowPictures.setEnabled(false);
        getPicturesTask.execute();
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


}
