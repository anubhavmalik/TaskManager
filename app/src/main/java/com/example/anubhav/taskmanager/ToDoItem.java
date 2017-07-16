package com.example.anubhav.taskmanager;

/**
 * Created by Anubhav on 03-07-2017.
 */

public class ToDoItem {
    String title, date, time, details, category;
    int id;//,order;


    public ToDoItem(int id, String title, String date, String time, String details, String category){//, int order) {
        this.id=id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.details = details;
        this.category = category;


    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
