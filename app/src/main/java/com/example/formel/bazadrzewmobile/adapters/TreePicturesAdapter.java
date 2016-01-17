package com.example.formel.bazadrzewmobile.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by formel on 08.01.16.
 */
public class TreePicturesAdapter extends BaseAdapter {
    private Context mContext;

    // Keep all Images in array
    List<Drawable> picturesList;

    // Constructor
    public TreePicturesAdapter(Context c, List<Drawable> picturesList){
        this.picturesList = picturesList;
        mContext = c;
    }

    @Override
    public int getCount() {
        return picturesList.size();
    }

    @Override
    public Object getItem(int position) {
        return picturesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView imageView = new ImageView(mContext);
        imageView.setImageDrawable(picturesList.get(position));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(70, 70));

        return imageView;
    }
}
