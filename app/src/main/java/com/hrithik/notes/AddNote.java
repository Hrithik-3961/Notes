package com.hrithik.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNote extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;

    private DatabaseReference databaseReference;

    public static final String path = "Uploads";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);

        editTextTitle = findViewById(R.id.title_input);
        editTextDescription = findViewById(R.id.desciption_input);

        databaseReference = FirebaseDatabase.getInstance("https://notes-bd749-default-rtdb.firebaseio.com/").getReference(path);
    }

    public void saveNote(View view) {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if(title.isEmpty() || description.isEmpty())
            Toast.makeText(this, "Cannot save note without title or description", Toast.LENGTH_SHORT).show();
        else{
            Note note = new Note(title, description, false);
            String uploadId = databaseReference.push().getKey();
            databaseReference.child(uploadId).setValue(note);
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_DESCRIPTION, description);

        setResult(RESULT_OK, intent);
        finish();
    }

    public void back(View view) {
        onBackPressed();
    }
}