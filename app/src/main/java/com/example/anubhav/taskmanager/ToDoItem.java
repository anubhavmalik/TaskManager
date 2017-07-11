package com.example.anubhav.taskmanager;

/**
 * Created by Anubhav on 03-07-2017.
 */

public class ToDoItem {
    String title, date, time, details, category;
    int id;

    public ToDoItem(int id, String title, String date, String time, String details, String category) {
        this.id=id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.details = details;
        this.category = category;
    }
}
