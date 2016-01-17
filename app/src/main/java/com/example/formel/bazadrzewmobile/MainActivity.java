package com.example.formel.bazadrzewmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.formel.bazadrzewmobile.activities.ListTreesActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.add_tree_btn)
    public void openAddTreeActivity(){
        Intent addTreeActivity = new Intent(MainActivity.this, AddTreeActivity.class);
        startActivity(addTreeActivity);
    }

    @OnClick(R.id.show_trees_btn)
    public void openTreeListActivity(){
        Intent listTreesActivity = new Intent(MainActivity.this, ListTreesActivity.class);
        startActivity(listTreesActivity);
    }
}
