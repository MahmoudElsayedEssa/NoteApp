package com.example.architectureexapmle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                adapter.submitList(notes);
            }
        });

        FloatingActionButton button = findViewById(R.id.button_add_note);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AddOrEditNoteActivity.class);
                addNoteActivityResultLauncher.launch(intent);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getBaseContext(), "Note deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddOrEditNoteActivity.class);

                intent.putExtra(AddOrEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddOrEditNoteActivity.EXTRA_TILTE, note.getTitle());
                intent.putExtra(AddOrEditNoteActivity.EXTRA_DESCRIOTION, note.getDescription());
                intent.putExtra(AddOrEditNoteActivity.EXTRA_PRIORITY, note.getPriority());

                EditNoteActivityResultLauncher.launch(intent);
            }
        });
    }

    ActivityResultLauncher<Intent> addNoteActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        String title = data.getStringExtra(AddOrEditNoteActivity.EXTRA_TILTE);
                        String description = data.getStringExtra(AddOrEditNoteActivity.EXTRA_DESCRIOTION);
                        int priority = data.getIntExtra(AddOrEditNoteActivity.EXTRA_PRIORITY, 1);

                        noteViewModel.insert(new Note(title, description, priority));
                        Toast.makeText(getBaseContext(), "Note save", Toast.LENGTH_SHORT).show();

                    } else
                        Toast.makeText(getBaseContext(), "Note not save", Toast.LENGTH_SHORT).show();
                }
            });

    ActivityResultLauncher<Intent> EditNoteActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        int id = data.getIntExtra(AddOrEditNoteActivity.EXTRA_ID,-1);
                        if (id == -1) {
                            Toast.makeText(getBaseContext(), "Note can't be updated", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String title = data.getStringExtra(AddOrEditNoteActivity.EXTRA_TILTE);
                        String description = data.getStringExtra(AddOrEditNoteActivity.EXTRA_DESCRIOTION);
                        int priority = data.getIntExtra(AddOrEditNoteActivity.EXTRA_PRIORITY, 1);

                        Note note = new Note(title, description, priority);
                        note.setId(id);

                        noteViewModel.update(note);

                        Toast.makeText(getBaseContext(), "Note updated", Toast.LENGTH_SHORT).show();

                    } else
                        Toast.makeText(getBaseContext(), "Note can't be updated", Toast.LENGTH_SHORT).show();

                }
            });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                noteViewModel.deleteAllNotes();
                Toast.makeText(getBaseContext(), "All notes deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}