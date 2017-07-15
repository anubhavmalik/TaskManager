package com.example.anubhav.taskmanager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    ArrayList<ToDoItem> arrayList;
    int ADD_REQUEST = 10, EDIT_REQUEST = 10, UNDO_FLAG = 0, exit_count = 0;
    RecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("To-Do Manager");
        arrayList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        recyclerAdapter = new RecyclerAdapter(this, arrayList, new RecyclerAdapter.ToDoClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(MainActivity.this, ToDoDetails.class);
                i.putExtra(IntentConstants.to_do_title, arrayList.get(position).title);
                i.putExtra(IntentConstants.to_do_date, arrayList.get(position).date);
                i.putExtra(IntentConstants.to_do_details, arrayList.get(position).details);
                i.putExtra(IntentConstants.to_do_time, arrayList.get(position).time);
                i.putExtra(IntentConstants.to_do_id, arrayList.get(position).id);
                i.putExtra(IntentConstants.to_do_category, arrayList.get(position).category);
                startActivityForResult(i, EDIT_REQUEST);


//        }, new RecyclerAdapter.ToDoLongClickListener() {
//            @Override
//            public void onItemLongClick(View view, final int position) {
//                final int pos = position;
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("Accomplished");
//                builder.setCancelable(false);
//                builder.setMessage(arrayList.get(position).title + " is accomplished ?");
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//
            }
//                });
//
//                builder.setPositiveButton("okay", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        ToDoOpenHelper todoOpenHelper = ToDoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
//                        SQLiteDatabase db = todoOpenHelper.getWritableDatabase();
//                        db.delete(todoOpenHelper.tablename, "id = ?", new String[]{arrayList.get(pos).id + ""});
//                        arrayList.remove(pos);
//                        recyclerAdapter.notifyItemRemoved(pos);
//                    }
//                });
//                builder.create();
//                builder.show();
//            }

        });
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {


            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int from = viewHolder.getAdapterPosition();
                final int to = target.getAdapterPosition();
                Collections.swap(arrayList, from, to);
                recyclerAdapter.notifyItemMoved(from, to);
//                Handler h= new Handler();
//                h.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                    }
//                },1000);

                databaseSwap(from, to);


                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                UNDO_FLAG = 0;

                Snackbar.make(recyclerView, "Task Removed", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UNDO_FLAG = undoClicked();
                                updateDatabase();
                            }
                        }).setActionTextColor(Color.BLUE).show();

                Handler h= new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (UNDO_FLAG ==0) {
                            ToDoOpenHelper todoOpenHelper = ToDoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
                            SQLiteDatabase db = todoOpenHelper.getWritableDatabase();
                            db.delete(todoOpenHelper.tablename, "id = ?", new String[]{arrayList.get(position).id + ""});
                            arrayList.remove(position);
                            recyclerAdapter.notifyItemRemoved(position);
                        } else {
                            Snackbar.make(recyclerView, "Reverted !", Snackbar.LENGTH_SHORT).show();
                            updateDatabase();
                        }
                    }
                },1500);
                    }
                });

        itemTouchHelper.attachToRecyclerView(recyclerView);
        updateDatabase();
        recyclerAdapter.notifyDataSetChanged();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ToDoDetails.class);
                startActivityForResult(i, ADD_REQUEST);
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(v, "Adds a new task.", Snackbar.LENGTH_SHORT).show();
                return true;
            }
        });

        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) findViewById(R.id.search_item);

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerAdapter.filter(newText);
                recyclerAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.feedback) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SENDTO);
            Uri uri = Uri.parse("mail_to:anubhavmalikdeveloper@gmail.com");
            i.setData(uri);
            i.putExtra(Intent.EXTRA_SUBJECT, "Feedback for app");
            startActivity(i);
        }
        if (id == R.id.about) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SCREEN_OFF);
            i.getAction();
        }


        return true;
    }

    public void updateDatabase() {
        ToDoOpenHelper toDoOpenHelper = ToDoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
        SQLiteDatabase database = toDoOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(toDoOpenHelper.tablename, null, null, null, null, null, null);
        arrayList.clear();
        int position = 0;
        while (cursor.moveToNext()) {

            String title = cursor.getString(cursor.getColumnIndex(toDoOpenHelper.title));
            String detail = cursor.getString(cursor.getColumnIndex(toDoOpenHelper.detail));
            String date = cursor.getString(cursor.getColumnIndex(toDoOpenHelper.date));
            String time = cursor.getString(cursor.getColumnIndex(toDoOpenHelper.time));
            String category = cursor.getString(cursor.getColumnIndex(toDoOpenHelper.category));
            int id = cursor.getInt(cursor.getColumnIndex(toDoOpenHelper.id));
            ToDoItem toDoItem = new ToDoItem(id, title, date, time, detail, category);
            arrayList.add(toDoItem);
            recyclerAdapter.notifyItemInserted(position);
            position++;
        }
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_REQUEST) {
            if (resultCode == RESULT_OK) {
                updateDatabase();

            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "No details changed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (exit_count < 1) {
            exit_count++;
            Toast.makeText(this, "Press back " + exit_count + " more time to exit.", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    public int undoClicked() {
        return 1;
    }

    public void databaseSwap(int pos1, int pos2) {
        ToDoOpenHelper toDoOpenHelper = ToDoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
        SQLiteDatabase database = toDoOpenHelper.getWritableDatabase();
        ToDoItem item1 = arrayList.get(pos1);
        ToDoItem item2 = arrayList.get(pos2);
        String title1 = item2.title, date1 = item2.date, time1 = item2.time, detail1 = item2.details, category1 = item2.category;
        String title2 = item1.title, date2 = item1.date, time2 = item1.time, detail2 = item1.details, category2 = item1.category;


        ContentValues contentValues = new ContentValues();
        contentValues.put(toDoOpenHelper.title, title1);
        contentValues.put(toDoOpenHelper.date, date1);
        contentValues.put(toDoOpenHelper.time, time1);
        contentValues.put(toDoOpenHelper.detail, detail1);
        contentValues.put(toDoOpenHelper.category, category1);
        database.update(toDoOpenHelper.tablename, contentValues, "id=?", new String[]{item1.id + ""});


        contentValues = new ContentValues();
        contentValues.put(toDoOpenHelper.title, title2);
        contentValues.put(toDoOpenHelper.date, date2);
        contentValues.put(toDoOpenHelper.time, time2);
        contentValues.put(toDoOpenHelper.detail, detail2);
        contentValues.put(toDoOpenHelper.category, category2);
        database.update(toDoOpenHelper.tablename, contentValues, "id=?", new String[]{item2.id + ""});


//        updateDatabase();
    }
}