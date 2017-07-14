package com.example.anubhav.taskmanager;

/**
 * Created by Anubhav on 03-07-2017.
 */

public class ToDoItem {
    String title, date, time, details, category;
    int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ToDoItem(int id, String title, String date, String time, String details, String category) {
        this.id=id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.details = details;
        this.category = category;
    }
}
