package com.example.notes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

public class AddNoteActivity extends AppCompatActivity {

    private EditText addTitle;
    private EditText addNote;
    private Button saveNote;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Long countLong;
    Integer i = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        initViews();
        saveNotes();

    }
    private void initViews() {
        SharedPreferences countVerify = AddNoteActivity.this.getSharedPreferences("Notes", Context.MODE_PRIVATE);
        i = Integer.parseInt(countVerify.getString("count","0"));
        addTitle = findViewById(R.id.add_title);
        addNote = findViewById(R.id.add_note);
        saveNote = findViewById(R.id.save_note);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Notes");
        reference.keepSynced(true);

    }

    private void saveNotes() {

        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = addTitle.getText().toString().trim();
                final String note = addNote.getText().toString().trim();

                if (title.isEmpty()){
                    new AlertDialog.Builder(AddNoteActivity.this)
                            .setTitle("Alert")
                            .setMessage("Title is empty.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                } else if (note.isEmpty()) {
                    new AlertDialog.Builder(AddNoteActivity.this)
                            .setTitle("Alert")
                            .setMessage("Note is empty.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                }
                else{
                    i++;
                    SharedPreferences.Editor counter = AddNoteActivity.this.getSharedPreferences("Notes", Context.MODE_PRIVATE).edit();
                    counter.putString("count", String.valueOf(i));
                    counter.apply();
                    //storing the new counter in the database to make access easier.
                    SharedPreferences countSet = AddNoteActivity.this.getSharedPreferences("Notes", Context.MODE_PRIVATE);
                    String count = countSet.getString("count","0");
                    DatabaseReference referenceNew = database.getReference("Notes").child("note"+count);
                    //we are adding a unique id to access the data in the recycler view
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    long millis = System.currentTimeMillis();
                    java.util.Date date = new java.util.Date(millis);
                    referenceNew.child("time").setValue(simpleDateFormat.format(date));
                    referenceNew.child("title").setValue(title);
                    referenceNew.child("note").setValue(note);
                    SharedPreferences.Editor editor = AddNoteActivity.this.getSharedPreferences("Notes", Context.MODE_PRIVATE).edit();
                    editor.putString("title", title);
                    editor.putString("note", note);
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "Note successfully added.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        });

    }


}
