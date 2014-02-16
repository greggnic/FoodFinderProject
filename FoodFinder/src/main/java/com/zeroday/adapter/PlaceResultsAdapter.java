package com.zeroday.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zeroday.foodfinder.R;
import com.zeroday.places.Place;

import java.util.List;

/**
 * Created by nick on 2/4/14.
 */
public class PlaceResultsAdapter extends ArrayAdapter<Place> {
    private final Context context;
    private final List<Place> places;
    public String next_page_token;
    public int activeIndex;

    public PlaceResultsAdapter(Context context, int resource, List<Place> places) {
        super(context, resource, places);
        this.context = context;
        this.places = places;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService((Context.LAYOUT_INFLATER_SERVICE));

        View rowView = inflater.inflate(R.layout.list_item, null);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
        textView.setText(places.get(position).name);

        return rowView;
    }


}
