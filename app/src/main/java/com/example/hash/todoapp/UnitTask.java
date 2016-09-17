package com.example.hash.todoapp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hash on 9/8/16.
 */
public class UnitTask {

    public String title;
    public String description;
    boolean completed;

    public UnitTask(String title, String description, boolean completed) {
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public UnitTask() {
        // Do Nothing
    }

}
