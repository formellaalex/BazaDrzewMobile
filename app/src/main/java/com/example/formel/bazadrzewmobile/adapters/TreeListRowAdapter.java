package com.example.formel.bazadrzewmobile.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.formel.bazadrzewmobile.R;
import com.example.formel.bazadrzewmobile.activities.TreeInfoActivity;
import com.example.formel.bazadrzewmobile.beans.TreeListBean;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by formel on 16.12.15.
 */
public class TreeListRowAdapter extends ArrayAdapter<TreeListBean> {
    Context context;
    int layoutResourceId;
    List<TreeListBean> data = null;
    List<TreeListBean> filteredData = null;

    public TreeListRowAdapter(Context context, int layoutResourceId, List<TreeListBean> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.filteredData = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TreeListRowHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TreeListRowHolder();
            holder.nameLatinTxt = (TextView)row.findViewById(R.id.row_nazwa_latin_txt);
            holder.namePolishTxt = (TextView)row.findViewById(R.id.row_nazwa_polska_txt);
            holder.districtTxt = (TextView)row.findViewById(R.id.row_wojewodztwo_txt);

            row.setTag(holder);


        }
        else
        {
            holder = (TreeListRowHolder)row.getTag();
        }

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent treeInfoActivity = new Intent(context, TreeInfoActivity.class);
                treeInfoActivity.putExtra("tree", new Gson().toJson(data.get(position)));
                Log.d("POSITION", Integer.toString(position));
                context.startActivity(treeInfoActivity);
            }
        });

        TreeListBean object =filteredData.get(position);
        holder.namePolishTxt.setText(object.namePolish);
        holder.nameLatinTxt.setText(object.nameLatin);
        holder.districtTxt.setText(object.city);

        return row;
    }

    public TreeListBean getItem(int position){

        return data.get(position);
    }

    static class TreeListRowHolder
    {
        TextView nameLatinTxt;
        TextView namePolishTxt;
        TextView districtTxt;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<TreeListBean> list = data;

            int count = list.size();
            final ArrayList<TreeListBean> nlist = new ArrayList<TreeListBean>(count);

            TreeListBean filteredBean;

            for (int i = 0; i < count; i++) {
                filteredBean = list.get(i);
                if (filteredBean.namePolish.toLowerCase().contains(filterString)) {
                    nlist.add(filteredBean);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            filteredData = (ArrayList<TreeListBean>) results.values;
            notifyDataSetChanged();
        }

    }
}

