package com.example.formel.bazadrzewmobile.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.example.formel.bazadrzewmobile.R;
import com.example.formel.bazadrzewmobile.adapters.TreeListRowAdapter;
import com.example.formel.bazadrzewmobile.beans.TreeListBean;
import com.example.formel.bazadrzewmobile.helpers.AuthenticationHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ListTreesActivity extends AppCompatActivity {

    HttpURLConnection conn;
    private final String EMPTY_STRING = "";
    ListView treeListListview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_trees);
        treeListListview = (ListView)findViewById(R.id.treeListListview);


        GetTreesTask getTreesTask = new GetTreesTask();
        getTreesTask.execute();
    }

    public class GetTreesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL urlToRequest = new URL("http://www.reichel.pl/bdp/webapi/web/get_trees");
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

            return EMPTY_STRING;
        }

        @Override
        protected void onPostExecute(String result){
            if(EMPTY_STRING.equals(result)){
                Toast.makeText(getApplicationContext(), "Błąd podczas pobierania danych.", Toast.LENGTH_SHORT).show();
            }
            else{
                Type jsonObjectColl = new TypeToken<List<TreeListBean>>() {}.getType();
                List<TreeListBean> treeListResult = new Gson().fromJson(result, jsonObjectColl);
                TreeListRowAdapter tlrw = new TreeListRowAdapter(ListTreesActivity.this, R.layout.list_trees_row, treeListResult);
                treeListListview.setAdapter(tlrw);


            }
        }




    }


}
