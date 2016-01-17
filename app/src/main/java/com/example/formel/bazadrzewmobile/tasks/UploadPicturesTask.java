package com.example.formel.bazadrzewmobile.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.formel.bazadrzewmobile.helpers.DialogHelper;
import com.example.formel.bazadrzewmobile.helpers.HttpHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;

/**
 * Created by formel on 12.01.16.
 */
public class UploadPicturesTask extends AsyncTask<String, String,String> {

    String filename;
    String treeId;
    Context ctx;
    private FinishUploadingPictures fup;

    public UploadPicturesTask(String filename, String treeId, Context ctx){
        this.ctx = ctx;
        this.filename = filename;
        this.treeId = treeId;
    }

    public void setFinishUploadingPictures(FinishUploadingPictures fup){
        this.fup = fup;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://www.reichel.pl/bdp/webapi/web/upload_picture");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            final File file = new File(filename);

            FileBody fb = new FileBody(file);

            builder.addPart("file", fb);
            final HttpEntity httpEntity = builder.build();

            HttpHelper.ProgressiveEntity postEntity = new HttpHelper.ProgressiveEntity(httpEntity);

            post.setEntity(postEntity);
            HttpResponse response = client.execute(post);

            return HttpHelper.getResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(String result){

        if (null != result) {
            Log.d("PICTURE RESULT", result);
            fup.onFinishingUploadingPictures(result, treeId);
        } else {
            DialogHelper.dismiss();
            Toast.makeText(ctx, "Wystąpił błąd podczas dodawania drzewa. " + result + "  ", Toast.LENGTH_LONG).show();
        }
    }

    public interface FinishUploadingPictures{
        public void onFinishingUploadingPictures(String filename, String treeId);
    }

}
