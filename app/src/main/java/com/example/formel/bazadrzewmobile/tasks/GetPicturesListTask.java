package com.example.formel.bazadrzewmobile.tasks;

/**
 * Created by formel on 10.01.16.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.formel.bazadrzewmobile.beans.PictureBean;
import com.example.formel.bazadrzewmobile.helpers.AuthenticationHelper;
import com.example.formel.bazadrzewmobile.helpers.HttpHelper;
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

/**
 * Created by formel on 09.01.16.
 */
public class GetPicturesListTask extends AsyncTask<Void, Void, String> {

    private ProgressDialog dialog;
    private Context ctx;
    private HttpURLConnection conn;
    private int treeId;
    List<Drawable> bitmaps = new ArrayList<Drawable>();
    private PicturesListDownloadListener pdl;

    public GetPicturesListTask(Context ctx,int treeId ){
        this.treeId = treeId;
        this.ctx = ctx;
    }

    public void setPicturesListDownloadListener(PicturesListDownloadListener pdl){
        this.pdl = pdl;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(ctx);
        dialog.setMessage("Proszę czekać...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    @Override
    protected String doInBackground(Void... params) {

        try {
            URL urlToRequest = new URL("http://www.reichel.pl/bdp/webapi/web/get_pictures/" + treeId);
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
        Type jsonObjectColl = new TypeToken<List<PictureBean>>() {}.getType();
        List<PictureBean> treePictureResult = new Gson().fromJson(result, jsonObjectColl);
        if(treePictureResult.isEmpty()){
            dialog.dismiss();
            Toast.makeText(ctx, "Brak zdjęć dla obiektu", Toast.LENGTH_SHORT).show();
        }
        else {
            pdl.onFinishedPictureDownload(treePictureResult, dialog);

        }


    }


    public interface PicturesListDownloadListener{
        public void onFinishedPictureDownload(List<PictureBean> bitmaps, ProgressDialog dialog);
    }
}
