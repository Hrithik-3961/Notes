package com.hrithik.notes;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteRepository {

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;
    private LiveData<Note> lastNote;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public NoteRepository(Application application) {

        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);
        noteDao = noteDatabase.noteDao();
        allNotes = noteDao.getAllNotes();
        lastNote = noteDao.getLastNote();
    }

    public long insert(final Note note) {
        final long[] id = new long[1];
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                id[0] = noteDao.insert(note);
            }
        });

        return id[0];
    }

    public void update(final Note note) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                noteDao.update(note);
            }
        });
    }

    public void delete(final Note note) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                noteDao.delete(note);
            }
        });
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public LiveData<Note> getLastNote(){
        return lastNote;
    }
}
