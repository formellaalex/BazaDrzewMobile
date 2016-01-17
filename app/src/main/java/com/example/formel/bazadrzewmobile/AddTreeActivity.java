package com.example.formel.bazadrzewmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.formel.bazadrzewmobile.beans.NameLatinBean;
import com.example.formel.bazadrzewmobile.beans.NamePolishBean;
import com.example.formel.bazadrzewmobile.helpers.AuthenticationHelper;
import com.example.formel.bazadrzewmobile.helpers.DialogHelper;
import com.example.formel.bazadrzewmobile.helpers.LocationHelper;
import com.example.formel.bazadrzewmobile.helpers.MediaHelper;
import com.example.formel.bazadrzewmobile.tasks.AddPicturesToDbTask;
import com.example.formel.bazadrzewmobile.tasks.UploadPicturesTask;
import com.example.formel.bazadrzewmobile.tasks.AddTreeTask;
import com.example.formel.bazadrzewmobile.tasks.GeocoderTask;
import com.example.formel.bazadrzewmobile.tasks.GetNamesTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class AddTreeActivity extends AppCompatActivity {


    AddTreeTask addTreeTask;
    GeocoderTask geocoderTask;
    GetNamesTask getNamesTask;
    UploadPicturesTask uploadPicturesTask;
    ProgressDialog progressDialog;
    ConnectionService connectionService;

    @Bind(R.id.data_dodania_txt)
    EditText dataDodaniaTxt;

    @Bind(R.id.nazwa_latin_txt)
    AutoCompleteTextView nazwaLatinTxt;

    @Bind(R.id.nazwa_polska_txt)
    AutoCompleteTextView nazwaPolskaTxt;

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

    @Bind(R.id.opis_txt)
    EditText descriptionTxt;

    @Bind(R.id.dodatkowa_lok_txt)
    EditText dodatkLokalizTxt;

    @Bind(R.id.isPomnikChk)
    CheckBox isPomnikChk;

    @Bind(R.id.inGreenHouseChk)
    CheckBox inGreenHouseChk;

    @Bind(R.id.miastoTxt)
    EditText cityTxt;

    @Bind(R.id.ulicaNumerTxt)
    EditText ulicaNumerTxt;

    LocationManager mLocationManager;

    Location location = null;

    public static final int OSM_ACTIVITY_CODE = 111;
    public static final int GALLERY_PICTURES = 112;

    List<Bitmap> pictureList;

    String picture = null;

    boolean isLatinFull = false;
    boolean isPolishFull = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tree);
        ButterKnife.bind(this);
        dataDodaniaTxt.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        connectionService = new ConnectionService(getApplicationContext());
        geocoderTask = new GeocoderTask(AddTreeActivity.this);
        pictureList = new ArrayList<Bitmap>();
        progressDialog = new ProgressDialog(AddTreeActivity.this);
        progressDialog .setCanceledOnTouchOutside(false);
        DialogHelper.setProgressDialog(AddTreeActivity.this);
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

    public void setProgressDialogMessage(String text){

        progressDialog .setMessage(text);
    }

    @OnClick(R.id.add_complete_tree_btn)
    public void dodajDrzewo(){
            if (TextUtils.isEmpty(dataDodaniaTxt.getText().toString())) {
                dataDodaniaTxt.setError(getString(R.string.error_field_required));
            } else if (TextUtils.isEmpty(nazwaPolskaTxt.getText().toString()) && TextUtils.isEmpty(nazwaPolskaTxt.getText().toString())) {
                nazwaPolskaTxt.setError(getString(R.string.error_field_required));
                nazwaLatinTxt.setError(getString(R.string.error_field_required));
            }
            else if(TextUtils.isEmpty(dataDodaniaTxt.getText().toString())){
                dataDodaniaTxt.setError("To pole jest wymagane");
            }
            else if(nazwaLatinTxt.getText().toString().toLowerCase().equals(nazwaLatinTxt.getText().toString())) {
                nazwaLatinTxt.setError("Nazwa musi zaczynać się z dużej litery!");
            }
            else if(nazwaPolskaTxt.getText().toString().toLowerCase().equals(nazwaPolskaTxt.getText().toString())){
                nazwaPolskaTxt.setError("Nazwa musi zaczynać się z dużej litery!");

            }

            else{
                addTreeBtn.setEnabled(false);
                URL url = null;
                try {
                    url = new URL("http://www.reichel.pl/bdp/webapi/web/app.php/add_tree");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject jsonParams = new JSONObject();
                    String [] lokalizacja = lokalizacjaTxt.getText().toString().split(",");
                    jsonParams.put("locationLatitude",lokalizacja[0]);
                    jsonParams.put("locationLongitude",lokalizacja[1]);
                    jsonParams.put("nameLatin", nazwaLatinTxt.getText().toString());
                    jsonParams.put("namePolish", nazwaPolskaTxt.getText().toString());
                    jsonParams.put("adddate", dataDodaniaTxt.getText().toString());
                    jsonParams.put("token", AuthenticationHelper.TOKEN);
                    if(!TextUtils.isEmpty(ulicaNumerTxt.getText().toString())){
                        jsonParams.put("street", ulicaNumerTxt.getText().toString());
                    }
                    if(!TextUtils.isEmpty(cityTxt.getText().toString())){
                        jsonParams.put("city", cityTxt.getText().toString());
                    }
                    if(isPomnikChk.isChecked()){
                        jsonParams.put("isPomnik", "1");
                    }
                    if(inGreenHouseChk.isChecked()){
                        jsonParams.put("inGreenhouse", "1");
                    }
                    if(!TextUtils.isEmpty(descriptionTxt.getText().toString())){
                        jsonParams.put("description", descriptionTxt.getText().toString());
                    }
                    if(!TextUtils.isEmpty(dodatkLokalizTxt.getText().toString())){
                        jsonParams.put("location", dodatkLokalizTxt.getText().toString());
                    }
                    addTreeTask = new AddTreeTask(jsonParams, AddTreeActivity.this);
                    addTreeTask.setAddPictuesByTreeId(new AddTreeTask.AddPicturesByTreeId() {
                        @Override
                        public void onFinishedAddingTree(String id) {

                            if (null != picture) {
                                Log.d("ZDJECIE", "Dodaję");
                                if (null == id) {
                                    Toast.makeText(AddTreeActivity.this, "Nie udało się dodać zdjęcia.", Toast.LENGTH_SHORT).show();
                                } else {
                                    uploadPicturesTask = new UploadPicturesTask(picture, id, AddTreeActivity.this);
                                    uploadPicturesTask.setFinishUploadingPictures(new UploadPicturesTask.FinishUploadingPictures() {
                                        @Override
                                        public void onFinishingUploadingPictures(String filename, String treeId) {
                                            AddPicturesToDbTask addPicturesToDbTask = new AddPicturesToDbTask(AddTreeActivity.this);
                                            addPicturesToDbTask.execute(treeId, filename);
                                        }
                                    });
                                    uploadPicturesTask.execute();
                                    progressDialog.dismiss();
                                }

                            }
                            else{
                                DialogHelper.dismiss();
                            }
                        }
                    });
                    addTreeTask.execute();
                    addTreeBtn.setEnabled(true);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

    }

    @OnFocusChange(R.id.nazwa_latin_txt)
    public void pobierzNazwyLatin(){
        if(!isLatinFull){
            getNamesTask = new GetNamesTask();
            getNamesTask.setNamesDownloadListener(new GetNamesTask.NamesDownloadListener() {
                @Override
                public void onFinishedNamesDownload(String result) {
                    Type jsonObjectColl = new TypeToken<List<NameLatinBean>>() {}.getType();
                    List<NameLatinBean> namesResult = new Gson().fromJson(result, jsonObjectColl);
                    String[] names = new String[namesResult.size()];
                    for(int i=0;i<namesResult.size(); i++){
                        names[i] = namesResult.get(i).nameLatin;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddTreeActivity.this,
                            android.R.layout.simple_list_item_1, names);
                    AutoCompleteTextView actv = (AutoCompleteTextView)findViewById(R.id.nazwa_latin_txt);
                    actv.setAdapter(adapter);

                }
            });
            getNamesTask.execute("get_names_latin");
            isLatinFull = true;
        }

    }

    @OnFocusChange(R.id.nazwa_polska_txt)
    public void pobierzNazwyPolish(){
        if(!isPolishFull){
            getNamesTask = new GetNamesTask();
            getNamesTask.setNamesDownloadListener(new GetNamesTask.NamesDownloadListener() {
                @Override
                public void onFinishedNamesDownload(String result) {
                    Type jsonObjectColl = new TypeToken<List<NamePolishBean>>() {}.getType();
                    List<NamePolishBean> namesResult = new Gson().fromJson(result, jsonObjectColl);
                    String[] names = new String[namesResult.size()];
                    for(int i=0;i<namesResult.size(); i++){
                        names[i] = namesResult.get(i).namePolish;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddTreeActivity.this,
                            android.R.layout.simple_list_item_1, names);
                    AutoCompleteTextView actv = (AutoCompleteTextView)findViewById(R.id.nazwa_polska_txt);
                    actv.setAdapter(adapter);

                }
            });
            getNamesTask.execute("get_names_polish");
            isPolishFull = true;
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
            setProgressDialogMessage("Lokalizuję");
            progressDialog.show();
        }
        else{
            lokalizacjaTxt.setText(location.getLatitude() + "," + location.getLongitude());
            Geocoder gcd = new Geocoder(AddTreeActivity.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if(!addresses.isEmpty()){
                    cityTxt.setText(addresses.get(0).getLocality());
                    ulicaNumerTxt.setText(addresses.get(0).getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

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
                        setProgressDialogMessage("Lokalizuję");
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

    @OnClick(R.id.add_tree_pictures_btn)
    public void dodajZdjecia(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), GALLERY_PICTURES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OSM_ACTIVITY_CODE){
            switch(resultCode){
                case Activity.RESULT_OK:
                    lokalizacjaTxt.setText(data.getDoubleExtra("LAT", 0) + "," + data.getDoubleExtra("LON", 0));
                    break;
            }
        }
        else if(requestCode == GALLERY_PICTURES && resultCode == RESULT_OK && null != data) {
            // retrieve a collection of selected images
//            Intent intent = new Intent();
//            ArrayList<Parcelable> list = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
//            // iterate over these images
//            if (list != null) {
//                for (Parcelable parcel : list) {
//                    Uri uri = (Uri) parcel;
//                    try {
//                        pictureList.add(MediaHelper.getBitmapFromUri(uri, AddTreeActivity.this));
//                        Log.d("PHOTO", uri.getLastPathSegment());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
            Log.d("ZDJECIA", "WYBIERAM");
            Uri selectedImage = data.getData();
            String path = MediaHelper.getPathFromGallery(AddTreeActivity.this, selectedImage);
            Log.d("ZDJECIA", path);
            picture = path;
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
