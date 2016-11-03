package com.ktgo.xxflye.kizombaharmony.util;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by XxFLYE on 1/6/2015.
 */

public class CustomListViewAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] lessonName;
    private final Integer[] vidthumbnails;

    public CustomListViewAdapter(Activity context, String[] lessonName, Integer[] vidthumbnails) {
        super(context, com.ktgo.xxflye.kizombaharmony.R.layout.lessonlistview, lessonName);

        this.context=context;
        this.lessonName = lessonName;
        this.vidthumbnails = vidthumbnails;
    }

    private class ViewHolder{

        ImageView imageView;
        TextView textView;

    }

    @Override
    public View getView(int position,View view,ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater=context.getLayoutInflater();
        if (view == null) {

            view = inflater.inflate(com.ktgo.xxflye.kizombaharmony.R.layout.lessonlistview, null);
            holder = new ViewHolder();
            holder.textView = (TextView) view.findViewById(com.ktgo.xxflye.kizombaharmony.R.id.lessonname);
            holder.imageView = (ImageView) view.findViewById(com.ktgo.xxflye.kizombaharmony.R.id.videoThumbnail);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.textView.setText(lessonName[position]);
        holder.textView.setTextColor(Color.parseColor("#000000"));
        holder.imageView.setImageResource(vidthumbnails[position]);

        return view;
    }
}