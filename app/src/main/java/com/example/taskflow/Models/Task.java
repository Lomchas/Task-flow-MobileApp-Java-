package com.example.taskflow.Models;

public class Task {
    private String id;
    private String title;
    private String description;
    private String label;
    private boolean completed;
    private String userId;

    public Task() {

    }

    public Task(String id, String title, String description, String label, boolean isCompleted, String userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.label = label;
        this.completed = isCompleted;
        this.userId = userId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
