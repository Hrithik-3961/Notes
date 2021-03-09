package com.hrithik.notes;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    long insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM Notes")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM Notes ORDER BY id DESC LIMIT 1")
    LiveData<Note> getLastNote();
}
