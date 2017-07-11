package com.example.anubhav.taskmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Anubhav on 03-07-2017.
 */

public class ToDoAdapter extends ArrayAdapter<ToDoItem> {
    ArrayList<ToDoItem> arrayList;
    Context context;
    TextView titleTextView, dateTextView, timeTextView;

    public ToDoAdapter(@NonNull Context context, ArrayList<ToDoItem> arrayList) {
        super(context, 0, arrayList);
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.todoitem_layout, null);
            titleTextView = (TextView) convertView.findViewById(R.id.title_item);
            dateTextView = (TextView) convertView.findViewById(R.id.date_item);
            timeTextView = (TextView) convertView.findViewById(R.id.time_item);
            titleTextView.setMinHeight(30);
            titleTextView.setTextSize(45);
            dateTextView.setTextSize(15);
            timeTextView.setTextSize(15);
            ViewHolder viewHolder = new ViewHolder(titleTextView, dateTextView, timeTextView);
            convertView.setTag(viewHolder);
        }
        ToDoItem t = arrayList.get(position);
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.titletextfield.setText(t.title);
        viewHolder.datetextfield.setText(t.date);
        viewHolder.timetextfield.setText(t.time);
        return convertView;
    }

    static class ViewHolder {
        TextView titletextfield, datetextfield, timetextfield;

        public ViewHolder(TextView titletextfield, TextView datetextfield, TextView timetextfield) {
            this.titletextfield = titletextfield;
            this.datetextfield = datetextfield;
            this.timetextfield = timetextfield;
        }
    }
}