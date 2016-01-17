package com.example.formel.bazadrzewmobile.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.formel.bazadrzewmobile.R;
import com.example.formel.bazadrzewmobile.helpers.MediaHelper;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImageFullScreenActivity extends AppCompatActivity {

    @Bind(R.id.fullScreenImage)
    ImageView fullScreenImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);
        ButterKnife.bind(this);

        Uri uri = getIntent().getParcelableExtra("uri");
        Bitmap bmp = null;
        try {
            bmp = MediaHelper.getBitmapFromUri(uri, ImageFullScreenActivity.this);
            fullScreenImage.setImageBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
