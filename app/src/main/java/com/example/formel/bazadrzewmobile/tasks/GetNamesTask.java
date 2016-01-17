package com.example.formel.bazadrzewmobile.tasks;

import android.os.AsyncTask;

import com.example.formel.bazadrzewmobile.helpers.AuthenticationHelper;
import com.example.formel.bazadrzewmobile.helpers.HttpHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by formel on 10.01.16.
 */
public class GetNamesTask extends AsyncTask<String, Void, String> {

    private HttpURLConnection conn;

    private NamesDownloadListener ndl;

    public void setNamesDownloadListener(NamesDownloadListener ndl){
        this.ndl = ndl;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL urlToRequest = new URL("http://www.reichel.pl/bdp/webapi/web/" + params[0]);
            conn = (HttpURLConnection) urlToRequest.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(10000);
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
    public void onPostExecute(String result){

        if(!HttpHelper.EMPTY_RESPONSE.equals(result)){
            ndl.onFinishedNamesDownload(result);
        }

    }

    public interface NamesDownloadListener{
        public void onFinishedNamesDownload(String result);
    }
}
