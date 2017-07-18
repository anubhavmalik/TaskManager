package com.example.anubhav.taskmanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScrollingDetailsActivity extends AppCompatActivity/* implements AdapterView.OnItemSelectedListener */ {

    TextView datetextview, timetextview;
    EditText titletextview, detailtextview;
//    Button save_button;
//    String category_text;
    ImageButton calendarButton, clockButton;
    Spinner spinner;
    ArrayList<String> category_array;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        final int id = i.getIntExtra(IntentConstants.to_do_id, -1);
        category_array= new ArrayList<>();
        category_array.clear();
        category_array.add("Work");category_array.add("Home");category_array.add("Food");category_array.add("Pickup");category_array.add("Shopping");category_array.add("Others");

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,category_array);
        spinner.setAdapter(arrayAdapter);
//        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(category_array.indexOf(i.getStringExtra(IntentConstants.to_do_category)));
        calendarButton = (ImageButton) findViewById(R.id.calendar_image_btn);
        clockButton = (ImageButton) findViewById(R.id.clock_image_btn);
        titletextview = (EditText) findViewById(R.id.titleEditText);
        titletextview.setText(i.getStringExtra(IntentConstants.to_do_title));
        detailtextview = (EditText) findViewById(R.id.detailEditText);
        detailtextview.setText(i.getStringExtra(IntentConstants.to_do_details));
        timetextview = (TextView) findViewById(R.id.timeEditText);
        timetextview.setText(i.getStringExtra(IntentConstants.to_do_time));
        clockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                showTimePicker(ScrollingDetailsActivity.this, hour, minute, false);
            }
        });
        datetextview = (TextView) findViewById(R.id.dateEditText);
        datetextview.setText(i.getStringExtra(IntentConstants.to_do_date));
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalendar = Calendar.getInstance();
                int initialmonth = newCalendar.get(java.util.Calendar.MONTH);
                int initialyear = newCalendar.get(java.util.Calendar.YEAR);

                showDatePicker(ScrollingDetailsActivity.this, initialyear, initialmonth, 1);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String new_title = titletextview.getText().toString();
                String new_date = datetextview.getText().toString();
                String new_detail = detailtextview.getText().toString();
                String new_time = timetextview.getText().toString();
                String new_category = spinner.getSelectedItem().toString();

                if (new_title.length() != 0) {

                    ToDoOpenHelper toDoOpenHelper = ToDoOpenHelper.getTodoOpenHelperInstance(ScrollingDetailsActivity.this);
                    SQLiteDatabase database = toDoOpenHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(toDoOpenHelper.title, new_title);
                    contentValues.put(toDoOpenHelper.date, new_date);
                    contentValues.put(toDoOpenHelper.time, new_time);
                    contentValues.put(toDoOpenHelper.detail, new_detail);
                    contentValues.put(toDoOpenHelper.category, new_category);
                    if (id != -1) {
                        database.update(toDoOpenHelper.tablename, contentValues, "id = ? ", new String[]{id + ""});
                    } else {
                        database.insert(toDoOpenHelper.tablename, null, contentValues);
                    }
                    setResult(RESULT_OK);
                    Snackbar.make(view, "Task saved.", Snackbar.LENGTH_SHORT).show();
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);

                } else {
                    titletextview.setError("Title can't be empty.");
                }
            }
        });
    }



    public void showDatePicker(Context context, int year, int month, int defaultdate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                datetextview.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, 1);
        datePickerDialog.show();
    }

    public void showTimePicker(Context context, int hour, int minute, boolean mode) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time, minute_string = "";
                if (minute < 10) {
                    minute_string = "0" + minute;
                } else if (minute > 9) {
                    minute_string = "" + minute;
                }
                if (hourOfDay < 12) {
                    time = "am";

                } else {
                    time = "pm";
                    hourOfDay = hourOfDay - 12;

                }
                if (hourOfDay == 0) {
                    hourOfDay = 12;
                }
                timetextview.setText(hourOfDay + ":" + minute_string + " " + time);
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        category_text = spinner.getItemAtPosition(position).toString();
//        if (category_text==(category_array.get(position).toString())  /** || category_text == "Custom"||position==5 || spinner.getItemAtPosition(position)==category_array.get(position-1)**/ ) {
//            final EditText input = (EditText) findViewById(R.id.custom_edit_text);
//            AlertDialog.Builder builder = new AlertDialog.Builder(ScrollingDetailsActivity.this);
//            builder.setTitle("Custom");
//            builder.setMessage("Add your own category");
//            View v= getLayoutInflater().inflate(R.layout.custom_dialog_view,null);
//            builder.setView(v);
//            builder.setIcon(R.drawable.circle_drawable);
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    String new_category_text = input.getText().toString();
//                    new_category_text = category_text;
//                    spinner.setSelection(5, true);
//                }
//            });

//        }

//    }

//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
    public long getepoch(String dateformat,String timeformat) {
        String date_time = dateformat + " " + timeformat;
        SimpleDateFormat datetime = new SimpleDateFormat("dd//MM/yyyy hh:mm a");
        long difference = 19800;
        long actual_time=0;

        try {
            Date d = datetime.parse(date_time);
            actual_time = d.getTime() + difference;
            return actual_time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return actual_time;
    }
}


