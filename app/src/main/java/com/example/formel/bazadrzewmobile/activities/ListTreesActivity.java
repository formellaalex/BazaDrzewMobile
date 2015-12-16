package com.example.formel.bazadrzewmobile.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.formel.bazadrzewmobile.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ListTreesActivity extends AppCompatActivity {

    HttpURLConnection conn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_trees);

        GetTreesTask getTreesTask = new GetTreesTask();
        getTreesTask.execute();
    }

    public class GetTreesTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

            try {
                URL urlToRequest = new URL("http://www.reichel.pl/bdp/webapi/web/get_trees");
                conn = (HttpURLConnection) urlToRequest.openConnection();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


}
