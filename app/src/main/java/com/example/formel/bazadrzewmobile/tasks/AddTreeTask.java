package com.example.formel.bazadrzewmobile.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.formel.bazadrzewmobile.helpers.AuthenticationHelper;
import com.example.formel.bazadrzewmobile.helpers.DialogHelper;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by formel on 06.01.16.
 */

public class AddTreeTask extends AsyncTask<Void, Void, Integer> {

    private JSONObject jsonParams;
    private HttpURLConnection conn;
    private OutputStream os;
    private Context ctx;
    private AddPicturesByTreeId addPicturesByTreeId;
    private String treeId = null;

    public AddTreeTask(JSONObject mJsonParams, Context ctx) {
        jsonParams = mJsonParams;
        this.ctx = ctx;
    }

    public void setAddPictuesByTreeId(AddPicturesByTreeId addPictuesByTreeId){
        this.addPicturesByTreeId = addPictuesByTreeId;
    }

    @Override
    protected void onPreExecute(){
        DialogHelper.setMessage("Dodawanie drzewa");
        DialogHelper.show();
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


                InputStream is = new BufferedInputStream(conn.getInputStream());
                InputStreamReader isr = new InputStreamReader(is);
                StringBuilder sb=new StringBuilder();
                BufferedReader br = new BufferedReader(isr);
                String read = br.readLine();

                while(read != null) {
                    sb.append(read);
                    read = br.readLine();

                }

                Log.i("STREAM", sb.toString());
                treeId = sb.toString();

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
    protected void onPostExecute(Integer response){
        if(HttpURLConnection.HTTP_OK == response){
            addPicturesByTreeId.onFinishedAddingTree(treeId);
        }
        else{
            DialogHelper.dismiss();
            Toast.makeText(ctx, "Wystąpił błąd: " + response,Toast.LENGTH_SHORT).show();

        }

    }

    public interface AddPicturesByTreeId{
        public void onFinishedAddingTree(String id);
    }


}