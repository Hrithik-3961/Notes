package com.hrithik.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;
    private DatabaseReference databaseReference;
    private NoteAdapter adapter;

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private Button delete;
    private Button close;
    private Button logout;
    private TextView noNotes;
    private ItemTouchHelper helper;

    private boolean selection = false;
    private Map<Integer, View> map = new HashMap<>();

    private String path;

    public static final int ADD_REQUEST_CODE = 1;
    public static final int EDIT_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.search_bar);
        delete = findViewById(R.id.delete);
        close = findViewById(R.id.close);
        logout = findViewById(R.id.logout);
        noNotes = findViewById(R.id.no_notes);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount != null)
            path = signInAccount.getId() + ".Notes";

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, Login.class));
                finishAffinity();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i : map.keySet()) {
                    map.get(i).setSelected(false);
                    map.get(i).setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.note_card));
                }
                selection = false;
                helper.attachToRecyclerView(recyclerView);
                map = new HashMap<>();
                delete.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                close.setVisibility(View.GONE);
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        FirebaseDatabase.getInstance("https://notes-bd749-default-rtdb.firebaseio.com/").setPersistenceEnabled(true);
        databaseReference = FirebaseDatabase.getInstance("https://notes-bd749-default-rtdb.firebaseio.com/").getReference(path);
        databaseReference.keepSynced(true);

        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = new ViewModelProvider(this, new NoteViewModelFactory(getApplication())).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.submitList(notes);
                if (noNotes.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noNotes.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noNotes.setVisibility(View.VISIBLE);
                }
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
            public void onItemClick(Note note, View item, int position) {
                if (selection) {
                    if (item.isSelected()) {
                        item.setSelected(false);
                        item.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.note_card));
                        map.remove(position);
                        if (map == null || map.size() == 0) {
                            selection = false;
                            helper.attachToRecyclerView(recyclerView);
                            searchView.setVisibility(View.VISIBLE);
                            delete.setVisibility(View.GONE);
                            close.setVisibility(View.GONE);
                        }
                    } else {
                        item.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.note_card_selected));
                        item.setSelected(true);
                        if (!map.containsKey(position))
                            map.put(position, item);
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, AddEditNote.class);
                    intent.putExtra(AddEditNote.EXTRA_ID, note.getId());
                    intent.putExtra(AddEditNote.EXTRA_TITLE, note.getTitle());
                    intent.putExtra(AddEditNote.EXTRA_DESCRIPTION, note.getDescription());
                    intent.putExtra(AddEditNote.EXTRA_PINNED, note.isPinned());
                    startActivityForResult(intent, EDIT_REQUEST_CODE);
                }

            }
        });

        adapter.setLongClickListener(new NoteAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(Note note, View item, int position) {

                helper.attachToRecyclerView(null);
                selection = true;
                item.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.note_card_selected));
                item.setSelected(true);
                if (!map.containsKey(position))
                    map.put(position, item);
                searchView.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.VISIBLE);
                close.setVisibility(View.VISIBLE);
            }
        });

        dragAndDrop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK) {

            String title = data.getStringExtra(AddEditNote.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNote.EXTRA_DESCRIPTION);
            Note note = new Note(title, description, false);
            noteViewModel.insert(note);
            final long[] id = {-1};
            noteViewModel.getLastNote().observe(this, new Observer<Note>() {
                @Override
                public void onChanged(Note note) {
                    id[0] = note.getId();
                    databaseReference.child(String.valueOf(id[0])).setValue(note);
                }
            });

        } else if (requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            long id = data.getLongExtra(AddEditNote.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, "Unable to update the note.", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(AddEditNote.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNote.EXTRA_DESCRIPTION);
            boolean pinned = data.getBooleanExtra(AddEditNote.EXTRA_PINNED, false);
            Note note = new Note(title, description, pinned);
            note.setId(id);
            noteViewModel.update(note);
            databaseReference.child(String.valueOf(id)).setValue(note);
        }
    }

    public void dragAndDrop() {

        helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
                ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {

                int positionDragged = dragged.getAdapterPosition();
                int targetPosition = target.getAdapterPosition();

                adapter.notifyItemMoved(positionDragged, targetPosition);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });

        helper.attachToRecyclerView(recyclerView);
    }

    public void deleteNotes(View v) {
        for (int i : map.keySet()) {
            Note note = adapter.getNoteAt(i);
            noteViewModel.delete(note);
            databaseReference.child(String.valueOf(note.getId())).removeValue();
        }
        map = new HashMap<>();
        delete.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
        close.setVisibility(View.GONE);
    }

}