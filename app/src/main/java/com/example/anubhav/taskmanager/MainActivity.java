package com.example.anubhav.taskmanager;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    ArrayList<ToDoItem> arrayList,toDoItems;
    int ADD_REQUEST = 10, EDIT_REQUEST = 11, UNDO_FLAG = 0, exit_count = 0;
    RecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("To-Do Manager");
        arrayList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        checkBox=(CheckBox) findViewById(R.id.checkBox);



        recyclerAdapter = new RecyclerAdapter(this, arrayList, new RecyclerAdapter.ToDoClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(MainActivity.this, ScrollingDetailsActivity.class);
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
//            }
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
            }

        });
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int from = viewHolder.getAdapterPosition();
                final int to = target.getAdapterPosition();

                Collections.swap(arrayList, from, to);
                recyclerAdapter.notifyItemMoved(from, to);
//                arrayListReOrder(from,to);
                Log.d("from on move", from + "");
                Log.d("to on move", to + "");
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
                                updateArrayList();
                            }
                        }).setActionTextColor(Color.BLUE).show();

                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (UNDO_FLAG == 0) {
                            ToDoOpenHelper todoOpenHelper = ToDoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
                            SQLiteDatabase db = todoOpenHelper.getWritableDatabase();
                            db.delete(todoOpenHelper.tablename, "id = ?", new String[]{arrayList.get(position).id + ""});
                            arrayList.remove(position);
                            recyclerAdapter.notifyItemRemoved(position);
                        } else {
                            Snackbar.make(recyclerView, "Reverted !", Snackbar.LENGTH_SHORT).show();
                            updateArrayList();
                        }
                    }
                }, 1500);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
        updateArrayList();
        recyclerAdapter.notifyDataSetChanged();
        toDoItems=new ArrayList<>();
        toDoItems=arrayList;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ScrollingDetailsActivity.class);
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
                recyclerAdapter.filter(newText,checkBox.isChecked());
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
            Intent i = new Intent(Intent.ACTION_SEND);
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

    public void updateArrayList() {
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
//            int order= cursor.getInt(cursor.getColumnIndex(toDoOpenHelper.order));
            int id = cursor.getInt(cursor.getColumnIndex(toDoOpenHelper.id));
            ToDoItem toDoItem = new ToDoItem(id, title, date, time, detail, category);
            arrayList.add(toDoItem);
            recyclerAdapter.notifyItemInserted(position);
            position++;
        }
        recyclerAdapter.notifyDataSetChanged();
        cursor.close();
//        toDoItems=arrayList;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_REQUEST) {
            if (resultCode == RESULT_OK) {
                updateArrayList();
            }
            if (resultCode == RESULT_CANCELED) {
                Log.i("tag canceled","RESULT_CANCELED");
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

//    public void databaseOrderUpdate(int pos1, int pos2) {
//        ToDoOpenHelper toDoOpenHelper = ToDoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
//        SQLiteDatabase database = toDoOpenHelper.getWritableDatabase();
//        ToDoItem item1 = arrayList.get(pos1);
//        ToDoItem item2 = arrayList.get(pos2);
//        Log.i("id 1", item1.id + "");
//        Log.i("id 2", item2.id + "");
//        int order1=item2.order;
//        int order2=item1.order;
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(toDoOpenHelper.order, order1);
//
//        long i = database.update(toDoOpenHelper.tablename, contentValues, "id=?", new String[]{item1.id + ""});
////        database.update(toDoOpenHelper.tablename, contentValues, "id=?", new String[]{item1.id + ""});
//        Log.i("pos ID : from ", i + "");
//
//
//        contentValues = new ContentValues();
//        contentValues.put(toDoOpenHelper.order, order2);
//        long i2 = database.update(toDoOpenHelper.tablename, contentValues, "id=?", new String[]{item2.id + ""});
////        database.update(toDoOpenHelper.tablename, contentValues, "id=?", new String[]{item1.id + ""});
//        Log.i("pos ID : to ", i2 + "");
//

//        updateArrayList();
//    }
//    public void arrayListReOrder(int from, int to){
//        int order1=toDoItems.get(from).order;
//        int order2=toDoItems.get(to).order;
//
//        arrayList.get(from).order=arrayList.get(to).order;
//        arrayList.get(to).order=arrayList.get(order1).order;
//        recyclerAdapter.notifyDataSetChanged();
//    }

}
