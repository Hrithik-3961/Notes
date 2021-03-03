package com.hrithik.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;
    private DatabaseReference databaseReference;

    private NoteAdapter adapter;

    private FloatingActionButton fab;
    private RecyclerView recyclerView;

    public static final String path = "Uploads";
    public static final int ADD_REQUEST_CODE = 1;
    public static final int EDIT_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = new ViewModelProvider(this, new NoteViewModelFactory(getApplication())).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.submitList(notes);
            }
        });

        fab = findViewById(R.id.floating);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditNote.class);
                startActivityForResult(intent, ADD_REQUEST_CODE);
            }
        });

        adapter.setOnClickListener(new NoteAdapter.OnClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNote.class);
                intent.putExtra(AddEditNote.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNote.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNote.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddEditNote.EXTRA_PINNED, note.isPinned());
                startActivityForResult(intent, EDIT_REQUEST_CODE);

            }
        });

        adapter.setLongClickListener(new NoteAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(Note note) {

            }
        });

        //retrieveNotes();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddEditNote.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNote.EXTRA_DESCRIPTION);
            Note note = new Note(title, description, false);
            noteViewModel.insert(note);
            String uploadId = databaseReference.push().getKey();
            databaseReference.child(uploadId).setValue(note);
        }
        else if(requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK){
            int id = data.getIntExtra(AddEditNote.EXTRA_ID, -1);
            if(id == -1) {
                Toast.makeText(this, "Unable to update the note.", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(AddEditNote.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNote.EXTRA_DESCRIPTION);
            boolean pinned = data.getBooleanExtra(AddEditNote.EXTRA_PINNED, false);
            Note note = new Note(title, description, pinned);
            note.setId(id);
            noteViewModel.update(note);

            //update the database
            String uploadId = databaseReference.push().getKey();
            databaseReference.child(uploadId).setValue(note);
        }
        else
            Toast.makeText(this, "Unable to save the note.", Toast.LENGTH_SHORT).show();
    }

    private void retrieveNotes() {

        FirebaseDatabase.getInstance("https://notes-bd749-default-rtdb.firebaseio.com/").setPersistenceEnabled(true);
        databaseReference = FirebaseDatabase.getInstance("https://notes-bd749-default-rtdb.firebaseio.com/").getReference(path);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot notesSnapshot : snapshot.getChildren()){
                    Note note = notesSnapshot.getValue(Note.class);
                    noteViewModel.insert(note);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}