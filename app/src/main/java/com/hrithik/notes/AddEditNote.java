package com.hrithik.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditNote extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;

    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);

        editTextTitle = findViewById(R.id.title_input);
        editTextDescription = findViewById(R.id.desciption_input);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
        }
    }

    public void saveNote(View view) {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if(title.isEmpty() || description.isEmpty()){
            Toast.makeText(this, "Title and Description cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_DESCRIPTION, description);

        long id = getIntent().getLongExtra(EXTRA_ID, -1);
        if (id != -1)
            intent.putExtra(EXTRA_ID, id);

        setResult(RESULT_OK, intent);
        finish();
    }

    public void back(View view) {
        onBackPressed();
    }
}