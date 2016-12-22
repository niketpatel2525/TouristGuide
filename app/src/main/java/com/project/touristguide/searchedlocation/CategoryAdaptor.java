package com.project.touristguide.searchedlocation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.touristguide.CategoryFragment;
import com.project.touristguide.R;

import java.util.ArrayList;

/**
 * Created by nbpat on 10/30/2016.
 */

public class CategoryAdaptor extends ArrayAdapter<com.project.touristguide.searchedlocation.CategoryFragment.Item> {
    int resource;
    ArrayList<com.project.touristguide.searchedlocation.CategoryFragment.Item> list;

    public CategoryAdaptor(Context context,
                           int resource,
                           ArrayList<com.project.touristguide.searchedlocation.CategoryFragment.Item> items) {
        super(context, resource, items);
        this.resource = resource;
        this.list = items;
    }

    class ViewHolder {
        TextView tvIndex, tvTitle, tvTag;
        ImageView ivImg;
    }

    ViewHolder holder;

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        holder = null;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(resource, null);
            holder = new ViewHolder();
            holder.tvIndex = (TextView) convertView.findViewById(R.id.tvindex);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvtitle);
            holder.tvTag = (TextView) convertView.findViewById(R.id.tvTag);
            holder.ivImg = (ImageView) convertView.findViewById(R.id.ivImg);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int index = position + 1;
        holder.tvIndex.setText(index + "");
        holder.tvTitle.setText(getItem(position).getTitle());
        holder.tvTag.setText((getItem(position).getTagLine()));

        holder.ivImg.setBackground(getContext().getApplicationContext().getResources().getDrawable(getItem(position).getImg()));
        return convertView;
    }
}
