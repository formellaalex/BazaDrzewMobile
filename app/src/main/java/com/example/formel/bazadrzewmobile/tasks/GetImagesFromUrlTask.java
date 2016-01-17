package com.example.formel.bazadrzewmobile.tasks;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.example.formel.bazadrzewmobile.beans.PictureBean;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by formel on 10.01.16.
 */
public class GetImagesFromUrlTask extends AsyncTask<Void, Void, List<Drawable>> {

    List<PictureBean> urls;
    ImagesFromUrlDownloadListener ifudl;

    public GetImagesFromUrlTask(List<PictureBean> urls){

        this.urls = urls;
    }

    public void setImagesFromUrlDownloadListener(ImagesFromUrlDownloadListener ifudl){
        this.ifudl = ifudl;
    }

    @Override
    protected List<Drawable> doInBackground(Void... params) {
        List<Drawable> drawables = new ArrayList<Drawable>();
        for(PictureBean url : urls){
            try {
                InputStream is = (InputStream) new URL("http://reichel.pl/bdp/" + url.filename).getContent();
                Drawable d = Drawable.createFromStream(is, "");
                drawables.add(d);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return drawables;

    }

    @Override
    public void onPostExecute(List<Drawable> drawables){
        ifudl.onFinishedImagesDownload(drawables);
    }

    public interface ImagesFromUrlDownloadListener{
        public void onFinishedImagesDownload(List<Drawable> drawables);
    }

}