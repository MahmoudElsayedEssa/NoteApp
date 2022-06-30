package com.example.architectureexapmle;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AddOrEditNoteActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.example.architectureexapmle.EXTRA_ID";
    public static final String EXTRA_TILTE = "com.example.architectureexapmle.EXTRA_TILTE";
    public static final String EXTRA_DESCRIOTION = "com.example.architectureexapmle.EXTRA_DESCRIOTION";
    public static final String EXTRA_PRIORITY = "com.example.architectureexapmle.EXTRA_PRIORITY";

    EditText editTextTitle;
    EditText editTextDescription;
    NumberPicker numberPickerPriorit;


//    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextTitle = findViewById(R.id.add_edit_text_title);
        editTextDescription = findViewById(R.id.add_edit_text_description);
        numberPickerPriorit = findViewById(R.id.add_numberPicker);

        numberPickerPriorit.setMinValue(1);
        numberPickerPriorit.setMaxValue(10);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Note");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TILTE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIOTION));
            numberPickerPriorit.setValue(intent.getIntExtra(EXTRA_PRIORITY, 1));

        } else
            setTitle("Add Note");

//        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);


    }

    private void saveNote() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        int priority = numberPickerPriorit.getValue();
        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT);
            return;
        }
//        noteViewModel.insert(new Note(title,description,priority));
//        startActivity(new Intent(this, MainActivity.class));


        Intent data = new Intent();
        data.putExtra(EXTRA_TILTE, title);
        data.putExtra(EXTRA_DESCRIOTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1)
            data.putExtra(EXTRA_ID, id);

        setResult(RESULT_OK, data);
        finish();
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}