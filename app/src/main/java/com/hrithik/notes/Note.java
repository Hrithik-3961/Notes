package com.hrithik.notes;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private boolean pinned;

    public Note(String title, String description, boolean pinned) {
        this.title = title;
        this.description = description;
        this.pinned = pinned;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
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
}
