package com.example.anubhav.taskmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ArrayList<ToDoItem> arrayList;
    ListView listView;
    ToDoAdapter toDoAdapter;
    int ADD_REQUEST = 10, EDIT_REQUEST = 10;
    int exit_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.list_item);
        arrayList = new ArrayList<ToDoItem>();
        toDoAdapter = new ToDoAdapter(this, arrayList);
        updateDatabase();
        listView.setAdapter(toDoAdapter);
        toDoAdapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, ToDoDetails.class);
                i.putExtra(IntentConstants.to_do_title, arrayList.get(position).title);
                i.putExtra(IntentConstants.to_do_date, arrayList.get(position).date);
                i.putExtra(IntentConstants.to_do_details, arrayList.get(position).details);
                i.putExtra(IntentConstants.to_do_time, arrayList.get(position).time);
                i.putExtra(IntentConstants.to_do_id, arrayList.get(position).id);
                i.putExtra(IntentConstants.to_do_category, arrayList.get(position).category);
                startActivityForResult(i, EDIT_REQUEST);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Accomplished");
                builder.setMessage(arrayList.get(position).title + " is accomplished ?");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

                builder.setPositiveButton("okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ToDoOpenHelper todoOpenHelper = ToDoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
                        SQLiteDatabase db = todoOpenHelper.getWritableDatabase();
                        db.delete(todoOpenHelper.tablename, "id = ?", new String[]{arrayList.get(pos).id + ""});
                        arrayList.remove(pos);
                        toDoAdapter.notifyDataSetChanged();
                    }
                });
                builder.create();
                builder.show();
                return true;
            }
        });

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
            i.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Todo app");
            startActivity(i);
        }
        if (id == R.id.about) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SCREEN_OFF);
            i.getAction();
        }

        if (id == R.id.app_bar_search) {
            SearchView searchView = (SearchView) item.getActionView();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    toDoAdapter.getFilter().filter(newText);
                    return true;
                }
            });
        }

        return true;
    }

    public void updateDatabase() {
        ToDoOpenHelper toDoOpenHelper = ToDoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
        SQLiteDatabase database = toDoOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(toDoOpenHelper.tablename, null, null, null, null, null, null);
        arrayList.clear();
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(toDoOpenHelper.title));
            String detail = cursor.getString(cursor.getColumnIndex(toDoOpenHelper.detail));
            String date = cursor.getString(cursor.getColumnIndex(toDoOpenHelper.date));
            String time = cursor.getString(cursor.getColumnIndex(toDoOpenHelper.time));
            String category = cursor.getString(cursor.getColumnIndex(toDoOpenHelper.category));
            int id = cursor.getInt(cursor.getColumnIndex(toDoOpenHelper.id));
            ToDoItem toDoItem = new ToDoItem(id, title, date, time, detail, category);
            arrayList.add(toDoItem);
        }
        toDoAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_REQUEST) {
            if (resultCode == RESULT_OK) {
                updateDatabase();

            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Details not saved", Toast.LENGTH_SHORT).show();
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
}