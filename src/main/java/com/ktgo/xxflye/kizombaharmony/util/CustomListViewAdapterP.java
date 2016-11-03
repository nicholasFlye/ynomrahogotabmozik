package com.ktgo.xxflye.kizombaharmony.util;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by XxFLYE on 2/9/2015.
 */

public class CustomListViewAdapterP extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] lessonName;
    private final String[] progressRatio;

    public CustomListViewAdapterP(Activity context, String[] lessonName, String[] progressRatio) {
        super(context, com.ktgo.xxflye.kizombaharmony.R.layout.progresslistlayout, lessonName);

        this.context=context;
        this.lessonName = lessonName;
        this.progressRatio = progressRatio;
    }

    private class ViewHolder{

        TextView textView;
        TextView textView2;

    }

    @Override
    public View getView(int position,View view,ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater=context.getLayoutInflater();
        if (view == null) {

            view = inflater.inflate(com.ktgo.xxflye.kizombaharmony.R.layout.progresslistlayout, null);
            holder = new ViewHolder();
            holder.textView = (TextView) view.findViewById(com.ktgo.xxflye.kizombaharmony.R.id.LevelProgressLabel);
            holder.textView2 = (TextView) view.findViewById(com.ktgo.xxflye.kizombaharmony.R.id.progressnumber);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.textView.setText(lessonName[position]);
        holder.textView.setTextColor(Color.parseColor("#000000"));
        holder.textView2.setText(progressRatio[position]);
        holder.textView2.setTextColor(Color.parseColor("#000000"));

        return view;
    }
}