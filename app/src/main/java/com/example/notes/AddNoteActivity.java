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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity {

    private EditText addTitle;
    private EditText addNote;
    private Button saveNote;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Long countLong;
    Integer i = 0;
    private String TAG = "TAG";
    private String idNote = " ";
    Map map;
    private boolean tag;
    private String unique_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        initViews();
        saveNotes();

    }
    private void initViews() {

        // this is how we retrieve the data from the previous activity.

        tag = getIntent().getBooleanExtra("tag", false);
        idNote = getIntent().getStringExtra("id");

        // getting the counter values through shared preference.

        SharedPreferences countVerify = AddNoteActivity.this.getSharedPreferences("Notes", Context.MODE_PRIVATE);
        i = Integer.parseInt(countVerify.getString("count","0"));
        addTitle = findViewById(R.id.add_title);
        addNote = findViewById(R.id.add_note);
        saveNote = findViewById(R.id.save_note);
        database = FirebaseDatabase.getInstance();

        // generating the unique id

        unique_id = android.provider.Settings.Secure.getString(getContentResolver()
                , android.provider.Settings.Secure.ANDROID_ID);
        reference = database.getReference(unique_id).child("Notes");

        // getting the data if we are coming from the card view

        if (tag){
            getData();
        }

        reference.keepSynced(true);

    }

    private void getData() {
        Log.e(TAG, "getData: " );
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (tag) {
                   for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                       map = (Map) snapshot.getValue();
                       String id = (String) map.get(("id"));
                       if (id.equalsIgnoreCase(idNote)) {
                           Log.e(TAG, "onDataChange: " + id);
                           addTitle.setText((String) map.get("title"));
                           addNote.setText((String) map.get("note"));
                       }
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

    }

    private void saveNotes() {

        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = addTitle.getText().toString().trim();
                final String note = addNote.getText().toString().trim();

                // method to check weather data is there or not

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
                    if (!tag) {
                        i++;
                        //incrementing the counter value to create a unique id for every note
                        // and storing it inside shared preference.

                        SharedPreferences.Editor counter = AddNoteActivity.this.getSharedPreferences("Notes", Context.MODE_PRIVATE).edit();
                        counter.putString("count", String.valueOf(i));
                        counter.apply();

                        //storing the new counter in the database to make access easier.

                        SharedPreferences countSet = AddNoteActivity.this.getSharedPreferences("Notes", Context.MODE_PRIVATE);
                        String count = countSet.getString("count", "0");

                        //getting reference to database

                        DatabaseReference referenceNew = database.getReference(unique_id).child("Notes").child("note" + count);

                        //we are adding a unique id to access the data in the recycler view
                        // method to get date and transfer it into simple date format.


                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        long millis = System.currentTimeMillis();
                        java.util.Date date = new java.util.Date(millis);

                        // adding values to the database under corresponding nodes

                        referenceNew.child("time").setValue(simpleDateFormat.format(date));
                        referenceNew.child("title").setValue(title);
                        referenceNew.child("id").setValue(count);
                        referenceNew.child("note").setValue(note);

                        // after adding the values we will redirect to the next activity

                        Toast.makeText(getApplicationContext(), "Note successfully added.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);
                        startActivity(intent);



                    }else {
                        tag = false;

                        // doing the same as before but just updating the data instead
                        // of adding a new data

                        DatabaseReference referenceNew = database.getReference(unique_id).child("Notes").child("note" + idNote);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        long millis = System.currentTimeMillis();
                        java.util.Date date = new java.util.Date(millis);

                        referenceNew.child("time").setValue(simpleDateFormat.format(date));
                        referenceNew.child("title").setValue(title);
                        referenceNew.child("id").setValue(idNote);
                        referenceNew.child("note").setValue(note);


                        Toast.makeText(getApplicationContext(), "Note successfully added.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }

            }
        });

    }


}
// Thank you for following