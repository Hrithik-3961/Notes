package com.hrithik.notes;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Note {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;
    private String description;
    private boolean pinned;

    public Note() {
        //required for Firebase
    }

    public Note(String title, String description, boolean pinned) {
        this.title = title;
        this.description = description;
        this.pinned = pinned;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
}
