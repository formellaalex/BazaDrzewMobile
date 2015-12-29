package com.example.formel.bazadrzewmobile;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.formel.bazadrzewmobile.helpers.AuthenticationHelper;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
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

    @Bind(R.id.wpisz_lokaliz_btn)
    Button wpiszLokalizacjeBtn;

    @Bind(R.id.lokaliz_btn)
    Button otworzMapeBtn;

    @Bind(R.id.add_complete_tree_btn)
    Button addTreeBtn;

    @Bind(R.id.lokaliz_txt)
    TextView lokalizacjaTxt;

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
                addTreeTask = new AddTreeTask(jsonParams);
                addTreeTask.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    @OnClick(R.id.wpisz_lokaliz_btn)
    public void pokazLokalizTxt(){
        lokalizacjaTxt.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.lokaliz_btn)
    public void otworzMape(){
        Intent osmActivity = new Intent(AddTreeActivity.this, OSMActivity.class);
        startActivity(osmActivity);
    }


    public class AddTreeTask extends AsyncTask<Void, Void, Integer> {

        private JSONObject jsonParams;
        HttpURLConnection conn;
        OutputStream os;
        AddTreeTask(JSONObject mJsonParams) {
            jsonParams = mJsonParams;
        }


        @Override
        protected Integer doInBackground(Void... params) {
            Integer responseCode = 404;
            try {
                URL urlToRequest = new URL("http://www.reichel.pl/bdp/webapi/web/add_tree");
                String postContent = jsonParams.toString();
                conn = (HttpURLConnection) urlToRequest.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(15000);

                // handle POST parameters
                if (jsonParams != null) {

                    if (Log.isLoggable("PARAMS", Log.INFO)) {
                        Log.i("PARAMS", "POST parameters: " + postContent);
                    }

                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");
                    conn.setFixedLengthStreamingMode(
                            postContent.getBytes().length);
                    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                    conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                    conn.setRequestProperty("token", AuthenticationHelper.TOKEN);

                    conn.connect();

                    os = new BufferedOutputStream(conn.getOutputStream());
                    os.write(postContent.getBytes());
                    //clean up
                    os.flush();
                }

//
//                InputStream is = new BufferedInputStream(conn.getInputStream());
//                InputStreamReader isr = new InputStreamReader(is);
//                StringBuilder sb=new StringBuilder();
//                BufferedReader br = new BufferedReader(isr);
//                String read = br.readLine();
//
//                while(read != null) {
//                    sb.append(read);
//                    read = br.readLine();
//
//                }
//
//                Log.i("STREAM", sb.toString());

                responseCode = conn.getResponseCode();
            } catch (MalformedURLException e) {
                // handle invalid URL
            } catch (SocketTimeoutException e) {
                // hadle timeout
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer result){

            if (HttpURLConnection.HTTP_OK == result) {
                Toast.makeText(AddTreeActivity.this, "Dodano drzewo", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddTreeActivity.this, "Wystąpił błąd podczas dodawania drzewa. " + result.toString() + "  ", Toast.LENGTH_LONG).show();

            }
        }


    }

}
