package com.example.formel.bazadrzewmobile.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.formel.bazadrzewmobile.helpers.AuthenticationHelper;
import com.example.formel.bazadrzewmobile.helpers.DialogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by formel on 17.01.16.
 */
public class AddPicturesToDbTask  extends AsyncTask<String, String,Integer> {

    private HttpURLConnection conn;
    private OutputStream os;
    private Context ctx;

    public AddPicturesToDbTask(Context ctx){
        this.ctx = ctx;
    }
    @Override
    protected Integer doInBackground(String... params) {
        URL urlToRequest = null;
        JSONObject jsonParams = new JSONObject();
        try {
            urlToRequest = new URL("http://www.reichel.pl/bdp/webapi/web/add_picture_to_table");
            jsonParams.put("idtree_objects", params[0]);
            jsonParams.put("filename", params[1]);
            jsonParams.put("adddate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            jsonParams.put("token", AuthenticationHelper.TOKEN);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            conn = (HttpURLConnection) urlToRequest.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(10000);
        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        conn.setFixedLengthStreamingMode(
               jsonParams.toString().getBytes().length);
        conn.setUseCaches(false);
        conn.setAllowUserInteraction(false);
        try {
            conn.connect();
            os = new BufferedOutputStream(conn.getOutputStream());
            os.write(jsonParams.toString().getBytes());
            //clean up
            os.flush();
            return conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Integer result){
        if(!(HttpURLConnection.HTTP_OK == result)){
            Toast.makeText(ctx, "Wystąpił błąd przy dodawaniu zdjęć. Problem: " + result, Toast.LENGTH_SHORT).show();
        }

        DialogHelper.dismiss();
    }
}
