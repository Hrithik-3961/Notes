package com.hrithik.notes;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;


@Entity(tableName = "Notes")
public class Note {

    @PrimaryKey(autoGenerate = true)
    @Exclude
    private long id;

    private String title;
    private String description;

    public Note() {
        //required for Firebase
    }

    public Note(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Exclude
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
