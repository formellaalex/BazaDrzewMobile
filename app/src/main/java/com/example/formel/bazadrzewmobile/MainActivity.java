package com.example.formel.bazadrzewmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.formel.bazadrzewmobile.activities.ListTreesActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    Button addTreeBtn;

    @Bind(R.id.show_trees_btn)
    Button treesListBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        addTreeBtn = (Button)findViewById(R.id.add_tree_btn);
        addTreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addTreeActivity = new Intent(MainActivity.this, AddTreeActivity.class);
                addTreeActivity.putExtra("lat", "");
                addTreeActivity.putExtra("lon", "");
                addTreeActivity.putExtra("author", "");
                startActivity(addTreeActivity);
            }
        });

    }


    @OnClick(R.id.show_trees_btn)
    public void openTreeListActivity(){
        Intent listTreesActivity = new Intent(MainActivity.this, ListTreesActivity.class);
        startActivity(listTreesActivity);
    }
}
