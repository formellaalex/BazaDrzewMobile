package com.example.formel.bazadrzewmobile.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by formel on 13.01.16.
 */
public class RegisterTask  extends AsyncTask<Void, Void, Integer> {

    private JSONObject jsonParams;
    private HttpURLConnection conn;
    private OutputStream os;
    private RegisterUser registerUser;
    private ProgressDialog progressDialog;
    private Context ctx;

    public RegisterTask(JSONObject mJsonParams, Context ctx) {
        jsonParams = mJsonParams;
        this.ctx = ctx;
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void setRegisterUser(RegisterUser registerUser){
        this.registerUser = registerUser;
    }
    @Override
    protected void onPreExecute(){

        progressDialog.setMessage("Proszę czekać...");
        progressDialog.show();
    }

    @Override
    protected Integer doInBackground(Void... params) {



        Integer responseCode = 404;
        try {
            URL urlToRequest = new URL("http://www.reichel.pl/bdp/webapi/web/register");
            String postContent = jsonParams.toString();
            conn = (HttpURLConnection) urlToRequest.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);

            if (jsonParams != null) {
                // handle POST parameters
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setFixedLengthStreamingMode(
                        postContent.getBytes().length);
                Log.d("PARAMS", postContent);
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                conn.connect();

                os = new BufferedOutputStream(conn.getOutputStream());
                os.write(postContent.getBytes());
                os.flush();
            }


//            InputStream is = new BufferedInputStream(conn.getInputStream());
//            InputStreamReader isr = new InputStreamReader(is);
//            StringBuilder sb=new StringBuilder();
//            BufferedReader br = new BufferedReader(isr);
//            String read = br.readLine();
//
//            while(read != null) {
//                sb.append(read);
//                read = br.readLine();
//
//            }

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
        registerUser.onFinishedRegistering(response);
        progressDialog.dismiss();
    }

    public interface RegisterUser{
        public void onFinishedRegistering(Integer responseCode);
    }
}
