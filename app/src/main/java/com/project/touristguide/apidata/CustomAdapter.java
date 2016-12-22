package com.project.touristguide.apidata;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.touristguide.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Harshit on 29 Oct 2016.
 */
public class CustomAdapter extends ArrayAdapter {
    List<String> place_name;
    List<String> distance;
    List<String> plImage;
    List<String> pl_rating;
    int resource;

    public CustomAdapter(Context applicationContext, int customlistitem, List<String> place, List<String> dist, List<String> image, List<String> rating) {
        super(applicationContext, customlistitem, place);
        this.resource = customlistitem;
        this.place_name = place;
        this.distance = dist;
        this.plImage = image;
        this.pl_rating = rating;
    }

    class ViewHolder {
        TextView place_name, distance, rating;
        ImageView place_image;
    }

    ViewHolder holder;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(resource, null);
            holder = new ViewHolder();
            holder.place_name = (TextView) convertView.findViewById(R.id.place_name);
            holder.distance = (TextView) convertView.findViewById(R.id.place_distance);
            holder.place_image = (ImageView) convertView.findViewById(R.id.place_image);
            holder.rating = (TextView) convertView.findViewById(R.id.place_rating);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            holder.place_name.setText("" + place_name.get(position));
            holder.distance.setText("" + distance.get(position));
            holder.rating.setText("Rating: " + pl_rating.get(position));
            Picasso.with(this.getContext()).load(plImage.get(position)).into(holder.place_image);
            holder.place_image.setAnimation(Place_list_Activity.anim);

        } catch (Exception e) {
            holder.place_name.setText("" + place_name.get(position));
            holder.distance.setText("");
            holder.rating.setText("");
        }
        return convertView;
    }
}
